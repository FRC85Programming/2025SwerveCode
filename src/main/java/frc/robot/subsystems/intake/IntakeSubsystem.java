package frc.robot.subsystems.intake;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class IntakeSubsystem extends SubsystemBase {
    
    private DigitalInput _homeLimit = new DigitalInput(Constants.INTAKE_HOME_LIMIT_ID);

    private SparkMax _armMotor = new SparkMax(-1, MotorType.kBrushless);
    private SparkMax _rollerMotor = new SparkMax(0, MotorType.kBrushless);

    private final PIDController angleController = new PIDController(2.0, 0, 0);

    private final IntakeSim intakeSim = new IntakeSim();

    public IntakeSubsystem() {
        _armMotor.set(0);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Arm Angle", Math.toDegrees(getArmAngle()));
    }

    @Override
    public void simulationPeriodic() {
        if (RobotBase.isSimulation()) {
            intakeSim.updateSim();
        }
    }

    /**Set the angle of the intake arm (radians) */
    public void setArmAngle(double targetAngle) {
        // Calculate speed value based on target angle
        double currentAngle = getArmAngle();
        double pidOutput = angleController.calculate(currentAngle, targetAngle);

        // Normalize the output to be between -1 and 1
        double percentOutput = Math.max(-1, Math.min(1, pidOutput));

        // Apply the calculated speed
        _armMotor.set(percentOutput);

        // In the case of the sim, convert the speed to voltage and set the voltage
        if (RobotBase.isSimulation()) {
            intakeSim.setInputVoltage(percentOutput * 12.0);
        }
    }

    /**Get the value of the intake arm angle - or the sim angle if the sim is active
     */
    public double getArmAngle() {
        // Return real angle if not in the sim, otherwise return the sim arm angle
        if (!RobotBase.isSimulation()) {
            return _armMotor.getEncoder().getPosition();
        } else {
            return intakeSim.getSimAngle();
        }
    }
}
