package frc.robot.commands.actions.climb;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.climb.ClimbSubsystem;
import frc.robot.subsystems.leds.LedSubsystem;


/**
 * Auto Balance command using a simple PID controller. Created by Team 3512
 * <a href="https://github.com/frc3512/Robot-2023/blob/main/src/main/java/frc3512/robot/commands/AutoBalance.java">...</a>
 */
public class DeployClimb extends Command
{

  private final ClimbSubsystem climb;
  double power;

  public DeployClimb(ClimbSubsystem climb)
  {
    this.climb = climb;
  }

  @Override
  public void execute()
  {
    LedSubsystem.startRainbow();
    climb.setClimbSpeed(-0.75);
  }

  @Override
  public boolean isFinished()
  {
    return climb.isDeployed();
  }

  @Override
  public void end(boolean interrupted)
  {
    climb.setClimbSpeed(0);
  }
}
