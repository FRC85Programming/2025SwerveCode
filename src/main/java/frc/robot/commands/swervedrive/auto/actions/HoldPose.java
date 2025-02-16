package frc.robot.commands.swervedrive.auto.actions;

import java.util.function.Supplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.subsystems.swervedrive.Vision;


/**
 * Auto Balance command using a simple PID controller. Created by Team 3512
 * <a href="https://github.com/frc3512/Robot-2023/blob/main/src/main/java/frc3512/robot/commands/AutoBalance.java">...</a>
 */
public class HoldPose extends Command
{

  private final SwerveSubsystem swerveSubsystem;
  Supplier<Pose2d> targetPose;
  HolonomicDriveController controller;
  ChassisSpeeds adjustedSpeeds;

  public HoldPose(Supplier<Pose2d> targetPose, SwerveSubsystem swerveSubsystem)
  {
    this.swerveSubsystem = swerveSubsystem;
    this.targetPose = targetPose;

    controller = new HolonomicDriveController(
      new PIDController(1, 0, 0), new PIDController(1, 0, 0),
      new ProfiledPIDController(1, 0, 0,
      new TrapezoidProfile.Constraints(6.28, 3.14)));
    controller.setTolerance(new Pose2d(0.05, 0.05, Rotation2d.fromDegrees(10)));
  }

  @Override
  public void execute()
  {
    adjustedSpeeds = controller.calculate(
      swerveSubsystem.getPose(), targetPose.get(), Constants.MAX_SPEED/2, swerveSubsystem.getHeading());
    swerveSubsystem.drive(ChassisSpeeds.fromFieldRelativeSpeeds(adjustedSpeeds, swerveSubsystem.getPose().getRotation()));
  }

  @Override
  public boolean isFinished()
  {
    return controller.atReference();
  }

  @Override
  public void end(boolean interrupted)
  {
    swerveSubsystem.drive(new ChassisSpeeds(0, 0, 0));
  }
}
