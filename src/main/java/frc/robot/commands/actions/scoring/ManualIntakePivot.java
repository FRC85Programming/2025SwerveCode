package frc.robot.commands.actions.scoring;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeSubsystem;


/**
 * Auto Balance command using a simple PID controller. Created by Team 3512
 * <a href="https://github.com/frc3512/Robot-2023/blob/main/src/main/java/frc3512/robot/commands/AutoBalance.java">...</a>
 */
public class ManualIntakePivot extends Command
{

  private final IntakeSubsystem intakeSubsystem;
  double power;

  public ManualIntakePivot(IntakeSubsystem intakeSubsystem, double power)
  {
    this.intakeSubsystem = intakeSubsystem;
    this.power = power;
  }

  @Override
  public void execute()
  {
    intakeSubsystem.driveIntakePivotRaw(power);
  }

  @Override
  public boolean isFinished()
  {
    return false;
  }

  @Override
  public void end(boolean interrupted)
  {
    intakeSubsystem.driveIntakePivotRaw(0);
  }
}
