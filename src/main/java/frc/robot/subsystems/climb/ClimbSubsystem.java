package frc.robot.subsystems.climb;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ClimbSubsystem extends SubsystemBase {

    private SparkFlex climbMotor = new SparkFlex(57, MotorType.kBrushless);
    private DigitalInput climbLimit = new DigitalInput(Constants.ClimbConstants.CLIMB_LIMIT_ID);

    double position = 0;

    public ClimbSubsystem() {
        SparkFlexConfig brakemode = new SparkFlexConfig();
        brakemode.idleMode(IdleMode.kBrake);
        climbMotor.configure(brakemode, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        climbMotor.set(0);
    }

    public void setClimbSpeed(double speed) {
        climbMotor.set(speed);
        SmartDashboard.putNumber("Deep climb enc", climbMotor.getEncoder().getPosition());

        // -50 rot to be flat
        if (climbLimit.get() ) {
            climbMotor.getEncoder().setPosition(0);
        } 
        climbMotor.set(speed);
    }
}
