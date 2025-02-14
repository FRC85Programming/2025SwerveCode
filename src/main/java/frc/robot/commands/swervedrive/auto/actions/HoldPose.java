package frc.robot.commands.swervedrive.auto.actions;

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
  Pose2d targetPose;
  double xSpeed;
  double ySpeed;
  double rotationSpeed;
  ChassisSpeeds driveSpeed = new ChassisSpeeds(0, 0, 0);
  int counter = 0;



  public HoldPose(Supplier<Pose2d> targetPose, SwerveSubsystem swerveSubsystem)
  {
    this.swerveSubsystem = swerveSubsystem;
    this.targetPose = targetPose.get();
    xController = new PIDController(2, 0, 0);
    yController = new PIDController(2, 0, 0);
    angleController = new PIDController(0.1, 0, 0);

    xController.setTolerance(1);
    yController.setTolerance(1);
    angleController.setTolerance(10);

  }

  @Override
  public void execute()
  {
    xSpeed = xController.calculate(swerveSubsystem.getPose().getX(), targetPose.getX());
    ySpeed = yController.calculate(swerveSubsystem.getPose().getY(), targetPose.getY());
    rotationSpeed = angleController.calculate(swerveSubsystem.getPose().getRotation().getDegrees(), targetPose.getRotation().getDegrees());
    driveSpeed = new ChassisSpeeds(xSpeed, ySpeed, rotationSpeed);
    swerveSubsystem.drive(driveSpeed);
  }

  @Override
  public boolean isFinished()
  {
    return xController.atSetpoint() && yController.atSetpoint() && angleController.atSetpoint();
  }

  @Override
  public void end(boolean interrupted)
  {
    swerveSubsystem.drive(new ChassisSpeeds(0, 0, 0));
  }
}
