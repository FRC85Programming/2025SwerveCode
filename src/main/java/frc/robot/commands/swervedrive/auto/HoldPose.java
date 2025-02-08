package frc.robot.commands.swervedrive.auto;

import java.util.function.Supplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.subsystems.swervedrive.Vision;


/**
 * Auto Balance command using a simple PID controller. Created by Team 3512
 * <a href="https://github.com/frc3512/Robot-2023/blob/main/src/main/java/frc3512/robot/commands/AutoBalance.java">...</a>
 */
public class HoldPose extends Command
{

  private final SwerveSubsystem swerveSubsystem;
  private final PIDController   xController;
  private final PIDController   yController;
  private final PIDController   angleController;
  Supplier<Pose2d> targetPose;
  double xSpeed;
  double ySpeed;
  double rotationSpeed;
  ChassisSpeeds driveSpeed = new ChassisSpeeds(0, 0, 0);
  int counter = 0;



  public HoldPose(Supplier<Pose2d> targetPose, SwerveSubsystem swerveSubsystem)
  {
    this.swerveSubsystem = swerveSubsystem;
    this.targetPose = targetPose;
    xController = new PIDController(5, 0, 0);
    yController = new PIDController(5, 0, 0);
    angleController = new PIDController(0.1, 0, 0);
  }

  @Override
  public void execute()
  {
    xSpeed = xController.calculate(swerveSubsystem.getPose().getX(), targetPose.get().getX());
    ySpeed = yController.calculate(swerveSubsystem.getPose().getY(), targetPose.get().getY());
    rotationSpeed = angleController.calculate(swerveSubsystem.getPose().getRotation().getDegrees(), targetPose.get().getRotation().getDegrees());
    driveSpeed = new ChassisSpeeds(xSpeed, ySpeed, rotationSpeed);
    swerveSubsystem.drive(driveSpeed);
  }

  @Override
  public boolean isFinished()
  {
    return false;
  }

  @Override
  public void end(boolean interrupted)
  {
    swerveSubsystem.drive(new ChassisSpeeds(0, 0, 0));
  }
}
