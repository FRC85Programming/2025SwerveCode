package frc.robot.subsystems.climb;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ClimbSubsystem extends SubsystemBase {

    private SparkFlex climbMotor = new SparkFlex(57, MotorType.kBrushless);
    private DigitalInput climbLimit = new DigitalInput(Constants.ClimbConstants.CLIMB_LIMIT_ID);
    private DigitalInput latchLimit = new DigitalInput(Constants.ClimbConstants.LATCH_LIMIT_ID);

    double position = 0;
    boolean atLimit = false;
    String selectedCage = "N";

    boolean homed = false;

    public ClimbSubsystem() {
        SparkFlexConfig brakemode = new SparkFlexConfig();
        brakemode.idleMode(IdleMode.kBrake);
        climbMotor.configure(brakemode, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

        climbMotor.set(0);
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Deep climb enc", climbMotor.getEncoder().getPosition());
        SmartDashboard.putBoolean("Deep climb latch limit", latchLimit.get());
        checkClimbMode();

    }

    public void checkClimbMode() {
    
    }

    public void setClimbSpeed(double speed) {

        //-95.083427

        if (climbLimit.get()) {
            atLimit = true;
        } else {
            atLimit = false;
            if (!homed) {
                climbMotor.getEncoder().setPosition(0);
                homed = true;
            }
        }

        if ((climbMotor.getEncoder().getPosition() < -87 && speed < 0) || (climbMotor.getEncoder().getPosition() > 33 && speed > 0) || (climbMotor.getEncoder().getPosition() > 0 && latchLimit.get())) {
            climbMotor.set(0);
        } else {
            climbMotor.set(speed);
        }
    }

    public Pose2d getCagePoseFromString(String cage) {
        if (DriverStation.getAlliance().get() == Alliance.Blue) {
            switch (cage) {
                case "CA" :
                    return Constants.PositionConstants.cagePosition1Blue;
                case "CB" :
                    return Constants.PositionConstants.cagePosition2Blue;
                case "CC" :
                    return Constants.PositionConstants.cagePosition3Blue;
                default:
                    return Constants.PositionConstants.cagePosition1Blue;
            }
        } else {
            switch (cage) {
                case "CA" :
                    return Constants.PositionConstants.cagePosition1Red;
                case "CB" :
                    return Constants.PositionConstants.cagePosition2Red;
                case "CC" :
                    return Constants.PositionConstants.cagePosition3Red;
                default:
                    return Constants.PositionConstants.cagePosition1Red;
            }
        }
    }
}
