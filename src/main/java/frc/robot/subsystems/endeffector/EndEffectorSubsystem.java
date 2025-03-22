package frc.robot.subsystems.endeffector;

import org.eclipse.jetty.util.MathUtils;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.util.MotorUtil;
import frc.robot.util.Positions;

public class EndEffectorSubsystem extends SubsystemBase {
    
    // Sparkmax declaration
    private SparkFlex pivotMotor = new SparkFlex(55, MotorType.kBrushless);
    private SparkFlex rollerMotor = new SparkFlex(56, MotorType.kBrushless);

    private DigitalInput coralLimit = new DigitalInput(Constants.EndEffectorConstants.CORAL_LIMIT_SWITCH);

    private DutyCycleEncoder pivotAbsoluteEncoder = new DutyCycleEncoder(Constants.EndEffectorConstants.PIVOT_ENCODER);

    private final PIDController angleController = new PIDController(0.17, 0, 0.0);

    // Sim for intake arm
    private final EndEffectorSimulation endeffectorSim = new EndEffectorSimulation(this);
    
    private double setPoint = 0.0;
    private double angleConversionFactor = (2*Math.PI)/9;
    boolean safe = true;
    boolean hasCoral = true;

    public EndEffectorSubsystem() {
        // Zero the arm
        pivotMotor.set(0);
        endeffectorSim.setPivotEncoderSim(0.945);
        setSetpoint(0);

        SmartDashboard.putNumber("Pivot P", 0.15);
        SmartDashboard.putNumber("Chatter Speed", 0.13);
        SmartDashboard.putNumber("L4 Angle", Constants.EndEffectorConstants.L4_PIVOT_POSITION);

        // Tell PID to wrap between 0 and 360 degrees
        //angleController.enableContinuousInput(Math.toRadians(0), Math.toRadians(1));
        angleController.setP(0.17);

    }

    @Override
    public void periodic() {
        driveToSetPoint();
        if (hasCoral) {
            holdCoral();
        }
        if (coralLimit.get() && rollerMotor.get() > 0) {
            hasCoral = true;
        }
        SmartDashboard.putNumber("Arm Radians", getEncoderValueAsRadians());
        SmartDashboard.putNumber("Pivot Rotation", pivotAbsoluteEncoder.get());
        //SmartDashboard.putNumber("Simencoder", endeffectorSim.getPivotEncoderSim());
        SmartDashboard.putBoolean("Safe", safe);
        SmartDashboard.putBoolean("Coral Switch", coralLimit.get());

    }
    

    @Override
    public void simulationPeriodic() {
        // Advances the sim by 0.02 seconds
        if (RobotBase.isSimulation()) {
            endeffectorSim.updateSim();
        }
    }

    public void driveToSetPoint() {
        double currentAngle = getPivotAngle();
        double pidOutput;
        
        // PID output (range: -1 to 1), needs to be scaled to voltage
        pidOutput = angleController.calculate(currentAngle, setPoint);
        double pidVoltage = pidOutput * Constants.MAX_VOLTAGE; // Scale to motor voltage


        // Clamp to prevent exceeding 12V
        pidVoltage = Math.max(-Constants.MAX_VOLTAGE, Math.min(Constants.MAX_VOLTAGE, pidVoltage));

        SmartDashboard.putNumber("currentAngle", currentAngle);
        setPivotVoltage(pidVoltage);

    }

    public boolean getCoralSwitch() {
        return coralLimit.get();
    }

    public void holdCoral() {
        if (!coralLimit.get()) {
            rollerMotor.set(SmartDashboard.getNumber("Chatter Speed", 0.13));
        } else {
            rollerMotor.set(0.0);
        }
    }

    public void setPivotVoltage(double voltage) {
        if (pivotAbsoluteEncoder.get() < 0.97 && pivotAbsoluteEncoder.get() > 0.262) {
            pivotMotor.setVoltage(voltage);
            safe = true;
            if (Robot.isSimulation()) {
                endeffectorSim.setInputVoltage(-voltage);
                //endeffectorSim.setPivotEncoderSim(getRadiansAsEncoderValue(getPivotAngle()));
            }
        } else {
            pivotMotor.setVoltage(0);
            safe = false;
        }
    }

    public void setSetpoint(double setPoint) {
        this.setPoint = setPoint;
    }

    public DutyCycleEncoder getPivotEncoderObject() {
        return pivotAbsoluteEncoder;
    }

    /**Get the value of the intake arm angle - or the sim angle if the sim is active
     */
    public double getPivotAngle() {
        // Return real angle if not in the sim, otherwise return the sim arm angle
        if (!Robot.isSimulation()) {
            return getEncoderValueAsRadians();
        } else {
            return endeffectorSim.getSimAngle();
        }
    }

    public double getEncoderValueAsRadians() {
        return (0.956 - pivotAbsoluteEncoder.get()) * 2*Math.PI;
    } 
    
    public double getRadiansAsEncoderValue(double angle) {
        return 0.956 - (angle / (2 * Math.PI));
    }

    public double getPivotEncoder() {
        if (!Robot.isSimulation()) {
            return pivotAbsoluteEncoder.get();
        } else {
            return endeffectorSim.getPivotEncoderSim();
        }
    }

    /** Set the speed of the intake rollers 
     * 
     * TODO: Figure out what direction in and out is
     */
    public void runRollers(double speed) {
        if (speed < 0) {
            hasCoral = false;
        }
        rollerMotor.set(speed);
    }

    public Pose3d getPivotSimPose() {
        return endeffectorSim.getPivotSimPose();
    }

    public boolean atSetpoint() {
        return Math.abs(getPivotAngle() - setPoint) < 0.05;
    }

    public double getSetpoint(Positions position) {
        switch (position) {
            case L1:
                return Constants.EndEffectorConstants.L1_PIVOT_POSITION;
            case L2:
                return Constants.EndEffectorConstants.L2_PIVOT_POSITION;
            case L3:
                return Constants.EndEffectorConstants.L3_PIVOT_POSITION;
            case L4:
                return Constants.EndEffectorConstants.L4_PIVOT_POSITION;
            case HOME:
                return Constants.EndEffectorConstants.HOME_PIVOT_POSITION;
            case INTAKE_FLOOR:
                return Constants.EndEffectorConstants.INTAKE_FLOOR_PIVOT_POSITION;
            case INTAKE_STATION:
                return Constants.EndEffectorConstants.INTAKE_STATION_PIVOT_POSITION;
            case L2_ALGAE:
                return Constants.EndEffectorConstants.L2_ALGAE_PIVOT_POSITION;
            case L3_ALGAE:
                return Constants.EndEffectorConstants.L3_ALGAE_PIVOT_POSITION;
            default:
                return Constants.ElevatorConstants.HOME_ELEVATOR_POSITION;
        }
    }
}
