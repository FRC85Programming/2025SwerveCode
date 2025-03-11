package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
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
    private DigitalInput homeLimit = new DigitalInput(Constants.IntakeConstants.INTAKE_HOME_LIMIT_ID);

    // Sparkmax declaration
    private SparkFlex armMotor = new SparkFlex(51, MotorType.kBrushless);
    private SparkFlex rollerMotor = new SparkFlex(52, MotorType.kBrushless);

    private final PIDController angleController = new PIDController(0.1, 0, 0.0);

    // Sim for intake arm
    private final IntakeSimulation intakeSim = new IntakeSimulation();
    
    private double setPoint = 0;
    private double pivotAngleConversionFactor = Math.PI/9;

    private boolean homed = false;

    public IntakeSubsystem() {
        SparkFlexConfig brakemode = new SparkFlexConfig();
        brakemode.idleMode(IdleMode.kBrake);
        armMotor.configure(brakemode, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        // Zero the arm
        armMotor.set(0);
        armMotor.getEncoder().setPosition(0);
        //armMotor.getAlternateEncoder().setPosition(0);

        //setSetpoint(0);

        SmartDashboard.putNumber("Intake P", 0.1);
        // Tell PID to wrap between -180 and 180 degrees
        angleController.enableContinuousInput(-Math.PI, Math.PI);
    }

    @Override
    public void periodic() {
        if (!homed) {
            driveIntakePivot(-0.3);
        } else {
            runToPosition();
        }
        angleController.setP(SmartDashboard.getNumber("Intake P", 0.1));
        SmartDashboard.putNumber("Intake Encoder", armMotor.getEncoder().getPosition());
        SmartDashboard.putBoolean("Homed", homed);
    }
    
    

    @Override
    public void simulationPeriodic() {
        // Advances the sim by 0.02 seconds
        if (RobotBase.isSimulation()) {
            intakeSim.updateSim();
        }
    }

    public void runToPosition() {
        double currentAngle = armMotor.getEncoder().getPosition();

        // PID calculates required motor speed (-1 to 1)
        double output = angleController.calculate(currentAngle, setPoint);

        // Clamp output if necessary
        output = Math.max(-1, Math.min(1, output));


        driveIntakePivot(output);

        if (RobotBase.isSimulation()) {
            intakeSim.setInputVoltage(output * 12.0);
        }
    }

    public void driveIntakePivot(double speed) {
        if (homeLimit.get()) {
            homed = true;
            armMotor.set(Math.abs(speed));
            armMotor.getEncoder().setPosition(0);
        } else {
            armMotor.set(speed);
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
                //return Constants.IntakeConstants.INTAKE_FLOOR_INTAKE_POSITION;
                return SmartDashboard.getNumber("Floor Coral Pos", 22.4);
            case INTAKE_STATION:
                return Constants.IntakeConstants.INTAKE_STATION_INTAKE_POSITION;
            case INTAKE_FLOOR_ALGAE:
                return SmartDashboard.getNumber("Floor Alg Pos", 10.5);
            default:
                return Constants.ElevatorConstants.HOME_ELEVATOR_POSITION;
        }
    }

    public Pose2d getCurrentProcessor() {
        if (DriverStation.getAlliance().get() == Alliance.Blue) {
            return Constants.PositionConstants.processorPositionBlue;
        } else {
            return Constants.PositionConstants.processorPositionRed;
        }
    }
}
