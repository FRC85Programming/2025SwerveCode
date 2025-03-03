package frc.robot.subsystems.leds;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;

import com.ctre.phoenix.led.RainbowAnimation;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class LedSubsystem extends SubsystemBase {

    AddressableLED led;
    AddressableLEDBuffer ledBuffer;

    private final LEDPattern rainbow = LEDPattern.rainbow(255, 255);

    private final LEDPattern blank = LEDPattern.solid(Color.kBlack);


    private static final Distance ledSpacing = Meters.of(1 / 120.0);

    private final LEDPattern scrollingRainbow =
        rainbow.scrollAtAbsoluteSpeed(MetersPerSecond.of(0.1), ledSpacing);
    

    public LedSubsystem() {
        // Create new led object with a port of 0
        led = new AddressableLED(Constants.LEDConstants.ledPWMPort);

        // Create a buffer to process the signals the leds are being set to
        // Set with a length of 12
        ledBuffer = new AddressableLEDBuffer(12);
        led.setLength(ledBuffer.getLength());

        // Start the leds
        led.setData(ledBuffer);
        led.start();
    }

    public void periodic() {
        runRainbow();
    }

    public void runRainbow() {
        scrollingRainbow.applyTo(ledBuffer);
        led.setData(ledBuffer);
    }

    public void runBlank() {
        blank.applyTo(ledBuffer);
        led.setData(ledBuffer);
    }

}
