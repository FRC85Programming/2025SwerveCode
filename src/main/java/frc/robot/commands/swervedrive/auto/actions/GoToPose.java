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
public class GoToPose extends Command
{

  private final SwerveSubsystem swerveSubsystem;
  Supplier<Pose2d> targetPose;
  Command driveToPoseCommand;
  int initCount = 0;
  int exCount = 0;


  public GoToPose(Supplier<Pose2d> targetPose, SwerveSubsystem swerveSubsystem)
  {
    this.swerveSubsystem = swerveSubsystem;
    this.targetPose = targetPose;

  }

  @Override
  public void initialize()
  {
    driveToPoseCommand = swerveSubsystem.driveToPose(targetPose);
    driveToPoseCommand.initialize();
    SmartDashboard.putNumber("Init Count", initCount);
    initCount++;

  }

  @Override
  public void execute() {
    driveToPoseCommand.execute();
    SmartDashboard.putNumber("Excecute Count", exCount);
    exCount++;
  }

  @Override
  public boolean isFinished() {
    return driveToPoseCommand.isFinished();
  }

  @Override
  public void end(boolean interrupted) {
    driveToPoseCommand.end(interrupted);
  }
}
