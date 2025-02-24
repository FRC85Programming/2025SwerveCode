package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.intake.IntakeSimulation;
import frc.robot.util.Positions;
import frc.robot.Constants;
import frc.robot.Robot;

public class IntakeSubsystem extends SubsystemBase {

    // TODO: Find a ratio to calculate the real arm angle from the gear ratio and ticks per location
    // TODO: Find the direction of the intake wheels
    
    // Limit on INSIDE of pivot (zero with this?)
    private DigitalInput homeLimit = new DigitalInput(6/*Constants.IntakeConstants.INTAKE_HOME_LIMIT_ID*/);

    // Sparkmax declaration
    private SparkMax armMotor = new SparkMax(51, MotorType.kBrushless);
    private SparkMax rollerMotor = new SparkMax(52, MotorType.kBrushless);

    private final PIDController angleController = new PIDController(2, 0, 0.5);

    // Sim for intake arm
    private final IntakeSimulation intakeSim = new IntakeSimulation();
    
    private double setPoint = 0;
    private double pivotAngleConversionFactor = Math.PI/9;

    public IntakeSubsystem() {
        // Zero the arm
        armMotor.set(0);
        //setSetpoint(0);

        // Tell PID to wrap between -180 and 180 degrees
        angleController.enableContinuousInput(-Math.PI, Math.PI);
    }

    @Override
    public void periodic() {
        runToPosition();
    }
    

    @Override
    public void simulationPeriodic() {
        // Advances the sim by 0.02 seconds
        if (RobotBase.isSimulation()) {
            intakeSim.updateSim();
        }
    }

    public void runToPosition() {
        double currentAngle = getArmAngle();

        // PID calculates required motor speed (-1 to 1)
        double output = angleController.calculate(currentAngle, setPoint);

        // Clamp output if necessary
        output = Math.max(-1, Math.min(1, output));

        SmartDashboard.putNumber("Used Angle", currentAngle);
        SmartDashboard.putNumber("Output Speed", output);
        SmartDashboard.putNumber("Normalized Speed", output);

        armMotor.set(output);

        if (RobotBase.isSimulation()) {
            intakeSim.setInputVoltage(output * 12.0);
        }
    }

    public void setSetpoint(double setPoint) {
        this.setPoint = setPoint;
    }
    
    /**Get the value of the intake arm angle - or the sim angle if the sim is active
     */
    public double getArmAngle() {
        // Return real angle if not in the sim, otherwise return the sim arm angle
        if (!Robot.isSimulation()) {
            return getIntakeAngle();
        } else {
            return intakeSim.getSimAngle();
        }
    }

    public double getIntakeAngle() {
        return armMotor.getEncoder().getPosition() * pivotAngleConversionFactor;
    }

    /** Set the speed of the intake rollers 
     * 
     * TODO: Figure out what direction in and out is
     */
    public void runRollers(double speed) {
        rollerMotor.set(speed);
    }

    public Pose3d getIntakeSimPose() {
        return intakeSim.getIntakeSimPose();
    }

    public double getSetpoint(Positions position) {
        switch (position) {
            case L1:
                return Constants.IntakeConstants.L1_INTAKE_POSITION;
            case L2:
                return Constants.IntakeConstants.L2_INTAKE_POSITION;
            case L3:
                return Constants.IntakeConstants.L3_INTAKE_POSITION;
            case L4:
                return Constants.IntakeConstants.L4_INTAKE_POSITION;
            case HOME:
                return Constants.IntakeConstants.HOME_INTAKE_POSITION;
            case INTAKE_FLOOR:
                return Constants.IntakeConstants.INTAKE_FLOOR_INTAKE_POSITION;
            case INTAKE_STATION:
                return Constants.IntakeConstants.INTAKE_STATION_INTAKE_POSITION;
            default:
                return Constants.ElevatorConstants.HOME_ELEVATOR_POSITION;
        }
    }
}
