package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class IntakeSubsytem extends SubsystemBase {
    
    private DigitalInput _homeLimit;
    private SparkMax _armMotor;
    private SparkMax _rollerMotor;

    public IntakeSubsytem() {
        _homeLimit = new DigitalInput(Constants.INTAKE_HOME_LIMIT_ID);
        _armMotor = new SparkMax(-1, MotorType.kBrushless);
        _rollerMotor = new SparkMax(-1, MotorType.kBrushless);


        
    }
}
