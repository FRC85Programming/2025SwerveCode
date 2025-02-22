package frc.robot.subsystems.endeffector;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Robot;

public class EndEffectorSubsystem extends SubsystemBase {
    
    // Sparkmax declaration
    private SparkMax pivotMotor = new SparkMax(24, MotorType.kBrushless);
    private SparkMax rollerMotor = new SparkMax(25, MotorType.kBrushless);

    private DutyCycleEncoder pivotAbsoluteEncoder = new DutyCycleEncoder(29);

    private final PIDController angleController = new PIDController(0.4, 0, 0.0);

    // Sim for intake arm
    private final EndEffectorSimulation endeffectorSim = new EndEffectorSimulation();
    
    private double targetAngleRadians = 0.0;

    public EndEffectorSubsystem() {
        // Zero the arm
        pivotMotor.set(0);
        setTargetAngle((3*Math.PI)/2);

        // Tell PID to wrap between -180 and 180 degrees
        angleController.enableContinuousInput(-Math.PI, Math.PI);
    }

    @Override
    public void periodic() {
        driveToTargetAngle();
    }
    

    @Override
    public void simulationPeriodic() {
        // Advances the sim by 0.02 seconds
        if (RobotBase.isSimulation()) {
            endeffectorSim.updateSim();
        }
    }

    public void driveToTargetAngle() {
        double currentAngle = getPivotAngle();

        // PID calculates required motor speed (-1 to 1)
        double output = angleController.calculate(currentAngle, targetAngleRadians);

        // Clamp output if necessary
        output = Math.max(-1, Math.min(1, output));

        SmartDashboard.putNumber("Current Angle", currentAngle);
        SmartDashboard.putNumber("Current Output", currentAngle);
        
        pivotMotor.set(output);

        if (RobotBase.isSimulation()) {
            endeffectorSim.setInputVoltage(output * 12.0);
        }
    }

    public void setTargetAngle(double targetAngle) {
        targetAngleRadians = targetAngle;
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
}
