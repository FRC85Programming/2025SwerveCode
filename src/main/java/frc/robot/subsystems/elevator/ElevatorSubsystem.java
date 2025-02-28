package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SmartMotionConfigAccessor;

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

    private ElevatorSimulation elevatorSim = new ElevatorSimulation(this);

    double setPoint = 0.0;
    double elevatorConversionFactor = 0.15;

    boolean hasHomed = false;

    public ElevatorSubsystem() {
        setSetpoint(0.0);
        SmartDashboard.putNumber("Elevator Tolerance", 0.01);
    }
    
    @Override
    public void periodic() {
        if (hasHomed || Robot.isSimulation()) {
            runToPosition(setPoint);
        } else {
            setElevatorSpeed(0.05);
        }
        checkEncoderReset();
        if (highLimit.get() || lowLimit.get()) {
            if (!Robot.isSimulation()) {
                setElevatorSpeed(0);
            }
        }
        SmartDashboard.putNumber("Elevator Encoder", elevatorEncoder.get());
        SmartDashboard.putNumber("Current Height", getElevatorPosition());
        SmartDashboard.putNumber("Height minus offset", Math.abs(getElevatorPosition() - setPoint));
        SmartDashboard.putBoolean("Is in range", isInTolerance());

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

        // PID calculates required motor speed (-1 to 1)
        //double output = elevatorController.calculate(elevatorEncoder.get(), getHeightInClicks(setPoint));
        double output = elevatorController.calculate(getElevatorEncoder(), getHeightInClicks(setPoint));


        // Clamp output if necessary
        output = Math.max(-1, Math.min(1, output));

        setElevatorSpeed(-output);
        SmartDashboard.putNumber("Elevator Speed Output", output);
    }

    public double getElevatorPosition() {
        if (!Robot.isSimulation()) {
            return elevatorEncoder.get()*0.813/11786;
        } else {
            return elevatorSim.getElevatorSimPosition();
        }
    }

    public boolean isInTolerance() {
        return Math.abs(getElevatorPosition() - setPoint) < 0.2 && setPoint != 0;
    }

    public void checkEncoderReset() {
        SmartDashboard.putBoolean("Low Limit", lowLimit.get());
        SmartDashboard.putBoolean("High Limit", highLimit.get());

        if (lowLimit.get() && !Robot.isSimulation()) {
            elevatorEncoder.reset();
            hasHomed = true;
        }
    }

    public double getHeightInClicks(double desiredHeight) {
        return desiredHeight*11786/0.813;
    }

    public void setElevatorSpeed(double speed) {
        speed = MathUtil.clamp(speed, -0.4, 0.4);
        elevatorMotorLeft.set(speed);
        elevatorMotorRight.set(-speed);

        if (RobotBase.isSimulation()) {
            elevatorSim.setInputVoltage(-speed * 12.0);
            elevatorSim.setSimEncoder(Math.toIntExact(Math.round(getHeightInClicks(getElevatorPosition()))));
        }
    }

    public Pose3d getElevatorSimPose() {
        return elevatorSim.getElevatorSimPose();
    }

    public Encoder getElevatorEncoderObject() {
        return elevatorEncoder;
    }

    public int getElevatorEncoder() {
        if (!Robot.isSimulation()) {
            return elevatorEncoder.get();
        } else {
            return elevatorSim.getSimEncoder();
        }
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
            case L2_ALGAE:
                return Constants.ElevatorConstants.L2_ALGAE_ELEVATOR_POSITION;
            default:
                return Constants.ElevatorConstants.HOME_ELEVATOR_POSITION;
        }
    }
}