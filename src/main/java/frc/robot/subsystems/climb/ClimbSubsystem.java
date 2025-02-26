package frc.robot.subsystems.climb;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.intake.IntakeSimulation;
import frc.robot.util.Positions;
import frc.robot.Constants;
import frc.robot.Robot;

public class ClimbSubsystem extends SubsystemBase {

    private SparkMax climbMotor = new SparkMax(57, MotorType.kBrushless);

    public ClimbSubsystem() {
        climbMotor.set(0);
    }

    public void setClimbSpeed(double speed) {
        climbMotor.set(speed);
    }
}
