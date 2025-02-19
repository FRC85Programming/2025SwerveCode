package frc.robot.commands.swervedrive.auto.actions;

import java.util.function.Supplier;

import org.photonvision.PhotonUtils;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;


/**
 * Auto Balance command using a simple PID controller. Created by Team 3512
 * <a href="https://github.com/frc3512/Robot-2023/blob/main/src/main/java/frc3512/robot/commands/AutoBalance.java">...</a>
 */
public class GoToPose extends Command
{

  private final SwerveSubsystem swerveSubsystem;
  Supplier<Pose2d> targetPose;
  Command driveToPoseCommand;
  int initCount = 0;
  int exCount = 0;
  Boolean endEarly = false;


  public GoToPose(SwerveSubsystem swerveSubsystem, Supplier<Pose2d> targetPose)
  {
    this.swerveSubsystem = swerveSubsystem;
    this.targetPose = targetPose;

  }

  @Override
  public void initialize()
  {
    driveToPoseCommand = swerveSubsystem.driveToPose(targetPose);
    if (PhotonUtils.getDistanceToPose(swerveSubsystem.getPose(), targetPose.get()) < 1) {
      endEarly = true;
    } else {
      endEarly = false;
    }
      
    driveToPoseCommand.initialize();
  }

  @Override
  public void execute() {
    driveToPoseCommand.execute();
    SmartDashboard.putNumber("Excecute Count", exCount);
    exCount++;
  }

  @Override
  public boolean isFinished() {
    return driveToPoseCommand.isFinished() || endEarly;
  }

  @Override
  public void end(boolean interrupted) {
    driveToPoseCommand.end(interrupted);
    endEarly = false;
  }
}
