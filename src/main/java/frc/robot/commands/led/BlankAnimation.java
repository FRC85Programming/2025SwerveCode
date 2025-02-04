package frc.robot.commands.led;

import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.leds.LedSubsystem;


public class BlankAnimation extends Command
{
    public BlankAnimation()
    {
        addRequirements(RobotContainer.getLedSubsytem());
    }

    @Override
    public void initialize()
    {
    }

    @Override
    public void end(boolean interrupted)
    {
    }
}
