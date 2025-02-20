package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Robot;

public class IntakeSubsystem extends SubsystemBase {

    // TODO: Find a ratio to calculate the real arm angle from the gear ratio and ticks per location
    // TODO: Find the direction of the intake wheels
    
    // Limit on INSIDE of pivot (zero with this?)
    private DigitalInput _homeLimit = new DigitalInput(1/*Constants.IntakeConstants.INTAKE_HOME_LIMIT_ID*/);

    // Sparkmax declaration
    private SparkMax _armMotor = new SparkMax(2, MotorType.kBrushless);
    private SparkMax _rollerMotor = new SparkMax(0, MotorType.kBrushless);

    private final PIDController angleController = new PIDController(0.2, 0, 0.1);

    // Sim for intake arm
    private final IntakeSim intakeSim = new IntakeSim();

    private double targetAngleRadians = 0;

    public IntakeSubsystem() {
        // Zero the arm
        _armMotor.set(0);
        setTargetAngle(0);

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
            intakeSim.updateSim();
        }
    }

    public void driveToTargetAngle() {
        double currentAngle = getArmAngle();

        // PID calculates required motor speed (-1 to 1)
        double output = angleController.calculate(currentAngle, targetAngleRadians);

        // Clamp output if necessary
        output = Math.max(-1, Math.min(1, output));

        SmartDashboard.putNumber("Used Angle", currentAngle);
        SmartDashboard.putNumber("Output Speed", output);
        SmartDashboard.putNumber("Normalized Speed", output);

        _armMotor.set(output);

        if (RobotBase.isSimulation()) {
            intakeSim.setInputVoltage(output * 12.0);
        }
    }

    public void setTargetAngle(double targetAngle) {
        targetAngleRadians = targetAngle;
    }
    
    /**Get the value of the intake arm angle - or the sim angle if the sim is active
     */
    public double getArmAngle() {
        // Return real angle if not in the sim, otherwise return the sim arm angle
        if (!Robot.isSimulation()) {
            return _armMotor.getEncoder().getPosition();
        } else {
            return intakeSim.getSimAngle();
        }
    }

    /** Set the speed of the intake rollers 
     * 
     * TODO: Figure out what direction in and out is
     */
    public void runRollers(double speed) {
        _rollerMotor.set(speed);
    }
}
