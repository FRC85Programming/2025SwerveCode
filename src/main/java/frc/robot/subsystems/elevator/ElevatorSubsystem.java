package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.util.Positions;

public class ElevatorSubsystem extends SubsystemBase {

    private SparkFlex elevatorMotorLeft = new SparkFlex(53, MotorType.kBrushless);
    private SparkFlex elevatorMotorRight = new SparkFlex(54, MotorType.kBrushless);

    private DigitalInput highLimit = new DigitalInput(Constants.ElevatorConstants.ELEVATOR_HIGH_LIMIT);
    private DigitalInput lowLimit = new DigitalInput(Constants.ElevatorConstants.ELEVATOR_LOW_LIMIT);

    private Encoder elevatorEncoder = new Encoder(Constants.ElevatorConstants.ELEVATOR_ENCODER_1, Constants.ElevatorConstants.ELEVATOR_ENCODER_2);

    private PIDController elevatorController = new PIDController(0.0001, 0, 0.0);

    private ElevatorSimulation elevatorSim = new ElevatorSimulation();

    double setPoint = 0.0;
    double elevatorConversionFactor = 0.15;

    boolean hasHomed = false;

    public ElevatorSubsystem() {
        setSetpoint(0.0);
    }
    
    @Override
    public void periodic() {
        if (hasHomed) {
            runToPosition(setPoint);
        }
        checkEncoderReset();
        if (highLimit.get() || lowLimit.get()) {
            setElevatorSpeed(0);
        }
        SmartDashboard.putNumber("Elevator Encoder", elevatorEncoder.get());
        SmartDashboard.putNumber("Current Height", elevatorEncoder.get()*0.813/11786);

    }

    @Override
    public void simulationPeriodic() {
        // Advances the sim by 0.02 seconds
        if (RobotBase.isSimulation()) {
            elevatorSim.updateSim();
        }
    }

    public void setSetpoint(double setPoint) {
        this.setPoint = setPoint;
    }

    public void runToPosition(double setPoint) {
        double currentHeight = getElevatorPosition();
        SmartDashboard.putNumber("Current Height", currentHeight);

        // PID calculates required motor speed (-1 to 1)
        double output = elevatorController.calculate(elevatorEncoder.get(), getHeightInClicks(setPoint));

        // Clamp output if necessary
        output = Math.max(-1, Math.min(1, output));

        setElevatorSpeed(-output);
        SmartDashboard.putNumber("Elevator Speed Output", output);

        if (RobotBase.isSimulation()) {
            elevatorSim.setInputVoltage(output * 12.0);
        }
    }

    public double getElevatorPosition() {
        if (!Robot.isSimulation()) {
            return elevatorEncoder.get();
        } else {
            return elevatorSim.getElevatorSimPosition();
        }
    }

    public void checkEncoderReset() {
        SmartDashboard.putBoolean("Low Limit", lowLimit.get());
        SmartDashboard.putBoolean("High Limit", highLimit.get());

        if (lowLimit.get()) {
            elevatorEncoder.reset();
            hasHomed = true;
        }
    }

    public double getHeightInClicks(double desiredHeight) {
        return desiredHeight*11786/0.813;
    }

    public void setElevatorSpeed(double speed) {
        speed = MathUtil.clamp(speed, -0.2, 0.2);
        elevatorMotorLeft.set(speed);
        elevatorMotorRight.set(-speed);

        if (RobotBase.isSimulation()) {
            elevatorSim.setInputVoltage(speed * 12.0);
        }
    }

    public Pose3d getElevatorSimPose() {
        return elevatorSim.getElevatorSimPose();
    }

    public double getSetpoint(Positions position) {
        switch (position) {
            case L1:
                return Constants.ElevatorConstants.L1_ELEVATOR_POSITION;
            case L2:
                return Constants.ElevatorConstants.L2_ELEVATOR_POSITION;
            case L3:
                return Constants.ElevatorConstants.L3_ELEVATOR_POSITION;
            case L4:
                return Constants.ElevatorConstants.L4_ELEVATOR_POSITION;
            case HOME:
                return Constants.ElevatorConstants.HOME_ELEVATOR_POSITION;
            case INTAKE_FLOOR:
                return Constants.ElevatorConstants.INTAKE_FLOOR_ELEVATOR_POSITION;
            case INTAKE_STATION:
                return Constants.ElevatorConstants.INTAKE_STATION_ELEVATOR_POSITION;
            default:
                return Constants.ElevatorConstants.HOME_ELEVATOR_POSITION;
        }
    }
}