package frc.robot.commands.led;

import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.subsystems.leds.LedSubsystem;


public class RainbowAnimation extends Command
{
    private LEDPattern m_scrollingRainbow;
    private LEDPattern m_rainbow;
    
    public RainbowAnimation()
    {
        // Configure rainbow animation
        m_rainbow = LEDPattern.rainbow(255, 255);

        m_scrollingRainbow =
            m_rainbow.scrollAtAbsoluteSpeed(MetersPerSecond.of(0.07), Constants.LEDConstants.kLedSpacing);
        addRequirements(RobotContainer.getLedSubsytem());
    }

    @Override
    public void initialize()
    {
        m_scrollingRainbow.applyTo(RobotContainer.getLedSubsytem().getLedBuffer());
    }

    @Override
    public void end(boolean interrupted)
    {
        RobotContainer.getLedSubsytem().turnOffLeds();
    }
}
