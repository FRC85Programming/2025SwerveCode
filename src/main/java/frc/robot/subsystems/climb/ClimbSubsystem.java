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
    boolean atLimit = false;

    public ClimbSubsystem() {
        SparkFlexConfig brakemode = new SparkFlexConfig();
        brakemode.idleMode(IdleMode.kBrake);
        climbMotor.configure(brakemode, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        climbMotor.set(0);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Deep climb enc", climbMotor.getEncoder().getPosition());

    }

    public void setClimbSpeed(double speed) {
        climbMotor.set(speed);

        //-95.083427


        if (climbLimit.get()) {
            if (!atLimit) {
                climbMotor.getEncoder().setPosition(0);
            }
            atLimit = true;
        } else {
            atLimit = false;
        }

        if ((climbMotor.getEncoder().getPosition() < -95 && speed < 0) || (climbMotor.getEncoder().getPosition() > 11.88 && atLimit && speed > 0)) {
            climbMotor.set(0);
        } else {
            climbMotor.set(speed);
        }
        
  
    }
}
