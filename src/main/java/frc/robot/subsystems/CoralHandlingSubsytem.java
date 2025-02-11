package frc.robot.subsystems;

import com.revrobotics.spark.SparkFlex;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class CoralHandlingSubsytem extends SubsystemBase {
    
    private SparkFlex _pivotMotor;

    private SparkFlex _elevatorMotorLeft;
    private SparkFlex _elevatorMotorRight;

    private Encoder _pivotAbsoluteEncoder;

    private Encoder _elevatorAbsoluteEncoder;

    public CoralHandlingSubsytem() {

    }

    public void SetPivotSpeed(Double speed) {
      _pivotMotor.set(speed);
    }
    
    public void StopPivot() {
      _pivotMotor.stopMotor();
    }

    public void SetElevatorSpeed(Double speed) {
      _elevatorMotorLeft.set(speed);
      _elevatorMotorRight.set(speed);
    }
    
    public void StopElevator() {
      _elevatorMotorLeft.stopMotor();
      _elevatorMotorRight.stopMotor();
    }

}
