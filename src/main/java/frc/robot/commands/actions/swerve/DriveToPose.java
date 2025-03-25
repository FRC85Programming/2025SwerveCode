package frc.robot.commands.actions.swerve;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.leds.LedSubsystem;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;


/**
 * Auto Balance command using a simple PID controller. Created by Team 3512
 * <a href="https://github.com/frc3512/Robot-2023/blob/main/src/main/java/frc3512/robot/commands/AutoBalance.java">...</a>
 */
public class DriveToPose extends Command
{

  private final SwerveSubsystem swerveSubsystem;
  Supplier<Pose2d> targetPose;
  Command driveToPoseCommand;
  Boolean endEarly = false;


  public DriveToPose(SwerveSubsystem swerveSubsystem, Supplier<Pose2d> targetPose)
  {
    this.swerveSubsystem = swerveSubsystem;
    this.targetPose = targetPose;

  }

  @Override
  public void initialize()
  {
    driveToPoseCommand = swerveSubsystem.driveToPose(targetPose);
      
    driveToPoseCommand.initialize();
    LedSubsystem.startSlowBlinkingGreen();
  }

  @Override
  public void execute() {
    driveToPoseCommand.execute();
  }

  @Override
  public boolean isFinished() {
    return driveToPoseCommand.isFinished();
  }

  @Override
  public void end(boolean interrupted) {
    driveToPoseCommand.end(interrupted);
    DriverStation.reportWarning("Drive To Pose End", false);
    LedSubsystem.stopPattern();
  }
}
