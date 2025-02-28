package frc.robot.subsystems.climb;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ClimbSubsystem extends SubsystemBase {

    private SparkMax climbMotor = new SparkMax(57, MotorType.kBrushless);
    private DigitalInput climbLimit = new DigitalInput(Constants.ClimbConstants.CLIMB_LIMIT_ID);

    double position = 0;

    public ClimbSubsystem() {
        climbMotor.set(0);
    }

    public void setClimbSpeed(double speed) {
        if (climbLimit.get()) {
            climbMotor.set(Math.abs(speed));
        } else {
            climbMotor.set(speed);

        }
        
    }
}
