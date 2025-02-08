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
public class DriveToIntakePosition extends Command
{

  private final SwerveSubsystem swerveSubsystem;
  Supplier<Pose2d> selectedPose;
  Command driveCommand;
  boolean started = false;

  public DriveToIntakePosition(SwerveSubsystem swerveSubsystem)
  {
    this.swerveSubsystem = swerveSubsystem;
    started = false;
  }

  @Override
  public void execute()
  {
    selectedPose = swerveSubsystem.getSelectedIntakePositionPose();
    
    if (!started) {
      driveCommand = swerveSubsystem.driveToPose(selectedPose).andThen(new HoldPose(selectedPose, swerveSubsystem));
      driveCommand.schedule();
      started = true;
    }
  }

  @Override
  public boolean isFinished()
  {
    return false;
  }

  @Override
  public void end(boolean interrupted)
  {
    driveCommand.cancel();
    started = false;
  }
}
