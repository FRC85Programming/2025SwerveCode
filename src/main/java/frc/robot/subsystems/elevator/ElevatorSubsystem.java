package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.Logger;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.util.Positions;

public class ElevatorSubsystem extends SubsystemBase {

    private SparkFlex elevatorMotorLeft = new SparkFlex(16, MotorType.kBrushless);
    private SparkFlex elevatorMotorRight = new SparkFlex(15, MotorType.kBrushless);

    private DutyCycleEncoder elevatorAbsoluteEncoder = new DutyCycleEncoder(30);

    private PIDController elevatorController = new PIDController(5.0, 0, 0.1);

    private ElevatorSimulation elevatorSim = new ElevatorSimulation();

    double setPoint = 0.0;

    public ElevatorSubsystem() {

    }
    
    @Override
    public void periodic() {
        runToPosition(setPoint);
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
        double output = elevatorController.calculate(currentHeight, setPoint);

        // Clamp output if necessary
        output = Math.max(-1, Math.min(1, output));

        setElevatorSpeed(output);

        if (RobotBase.isSimulation()) {
            elevatorSim.setInputVoltage(output * 12.0);
        }
    }

    public double getElevatorPosition() {
        if (!Robot.isSimulation()) {
            return elevatorAbsoluteEncoder.get();
        } else {
            return elevatorSim.getElevatorSimPosition();
        }
    }

    public void setElevatorSpeed(double speed) {
      elevatorMotorLeft.set(speed);
      elevatorMotorRight.set(-speed);
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
