package frc.robot.commands.actions.scoring;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.intake.IntakeSubsystem;


/**
 * Auto Balance command using a simple PID controller. Created by Team 3512
 * <a href="https://github.com/frc3512/Robot-2023/blob/main/src/main/java/frc3512/robot/commands/AutoBalance.java">...</a>
 */
public class Intake extends Command
{

  private final IntakeSubsystem intakeSubsystem;
  double power;
  int piece;

  public Intake(IntakeSubsystem intakeSubsystem, double power, int piece)
  {
    this.intakeSubsystem = intakeSubsystem;
    this.power = power;
    this.piece = piece;
  }

  @Override
  public void execute()
  {
    intakeSubsystem.runRollers(power);
  }

  @Override
  public boolean isFinished()
  {
    return false;
  }

  @Override
  public void end(boolean interrupted)
  {
    if (piece == 0) {
      intakeSubsystem.setSetpoint(0);
    }
    intakeSubsystem.runRollers(0);
  }
}
