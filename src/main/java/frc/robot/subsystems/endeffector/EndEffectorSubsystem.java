package frc.robot.subsystems.endeffector;

import org.eclipse.jetty.util.MathUtils;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose3d;
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
    private SparkMax rollerMotor = new SparkMax(56, MotorType.kBrushless);

    private DutyCycleEncoder pivotAbsoluteEncoder = new DutyCycleEncoder(Constants.EndEffectorConstants.PIVOT_ENCODER);

    private final PIDController angleController = new PIDController(0.7, 0, 0.0);

    // Sim for intake arm
    private final EndEffectorSimulation endeffectorSim = new EndEffectorSimulation();
    
    private double setPoint = 0.0;
    private double angleConversionFactor = (2*Math.PI)/9;

    public EndEffectorSubsystem() {
        // Zero the arm
        pivotMotor.set(0);
        setSetpoint(0);

        // Tell PID to wrap between 0 and 360 degrees
        //angleController.enableContinuousInput(Math.toRadians(0), Math.toRadians(1));
    }

    @Override
    public void periodic() {
        driveToSetPoint();
        SmartDashboard.putNumber("Suspected Arm Radians", getPivotAngleAsRadians());
        SmartDashboard.putNumber("Pivot Rotation", pivotAbsoluteEncoder.get());
    }
    

    @Override
    public void simulationPeriodic() {
        // Advances the sim by 0.02 seconds
        if (RobotBase.isSimulation()) {
            endeffectorSim.updateSim();
        }
    }

    public void driveToSetPoint() {
        double currentAngle = getPivotAngleAsRadians();
        
        // PID output (range: -1 to 1), needs to be scaled to voltage
        double pidOutput = angleController.calculate(currentAngle, setPoint);
        double pidVoltage = pidOutput * Constants.MAX_VOLTAGE; // Scale to motor voltage

        // Gravity feedforward
        double angleRadians = Math.toRadians(setPoint);
        double torqueRequired = MotorUtil.calculateTorque(Constants.EndEffectorConstants.ARM_MASS_KG, Constants.EndEffectorConstants.CENTER_OF_MASS, angleRadians);
        double feedforwardVoltage = (setPoint > currentAngle) ? 
            MotorUtil.torqueToVoltage(torqueRequired / 9, 0) : 
            -MotorUtil.torqueToVoltage(torqueRequired / 9, 0);
        // Total voltage applied to the motor
        double finalVoltage = pidVoltage /*+ feedforwardVoltage*/;

        // Clamp to prevent exceeding 12V
        finalVoltage = Math.max(-Constants.MAX_VOLTAGE, Math.min(Constants.MAX_VOLTAGE, finalVoltage));

        // Apply voltage to motor
        SmartDashboard.putNumber("Volts Unclamped", finalVoltage);

        finalVoltage = MathUtil.clamp(finalVoltage, -2, 2);
        pivotMotor.setVoltage(finalVoltage);

        SmartDashboard.putNumber("Volts Clamped", finalVoltage);
        SmartDashboard.putNumber("Gravity Comp", torqueRequired);
        SmartDashboard.putNumber("Gravity Comp Volts", feedforwardVoltage);
    }

    public void setPivotSpeed(double speed) {
        speed = MathUtil.clamp(speed, -0.2, 0.2);
        if (pivotAbsoluteEncoder.get() > 0.97 || pivotAbsoluteEncoder.get() < 0.279) {
            pivotMotor.set(0);
        } else {
            pivotMotor.set(speed);
        }
    }

    public void setSetpoint(double setPoint) {
        this.setPoint = setPoint;
    }

    /**Get the value of the intake arm angle - or the sim angle if the sim is active
     */
    public double getPivotAngle() {
        // Return real angle if not in the sim, otherwise return the sim arm angle
        if (!Robot.isSimulation()) {
            return pivotAbsoluteEncoder.get();
        } else {
            return endeffectorSim.getSimAngle();
        }
    }

    public double getPivotAngleAsRadians() {
        return (0.956 - pivotAbsoluteEncoder.get()) * 2*Math.PI;
    } 
    
    public double pivotAngleRadians() {
        return pivotAbsoluteEncoder.get() * angleConversionFactor;
    }

    /** Set the speed of the intake rollers 
     * 
     * TODO: Figure out what direction in and out is
     */
    public void runRollers(double speed) {
        rollerMotor.set(speed);
    }

    public Pose3d getPivotSimPose() {
        return endeffectorSim.getPivotSimPose();
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
            default:
                return Constants.ElevatorConstants.HOME_ELEVATOR_POSITION;
        }
    }
}
