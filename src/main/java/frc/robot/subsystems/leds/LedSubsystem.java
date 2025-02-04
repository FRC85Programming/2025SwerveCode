package frc.robot.subsystems.leds;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;

import com.ctre.phoenix.led.RainbowAnimation;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.commands.led.BlankAnimation;

public class LedSubsystem extends SubsystemBase {

    AddressableLED m_led;
    AddressableLEDBuffer m_ledBuffer;
    

    public LedSubsystem() {
        // Create new led object with a port of 0
        m_led = new AddressableLED(Constants.LEDConstants.ledPWMPort);

        // Create a buffer to process the signals the leds are being set to
        // Set with a length of 12
        m_ledBuffer = new AddressableLEDBuffer(12);
        m_led.setLength(m_ledBuffer.getLength());

        // Start the leds
        m_led.setData(m_ledBuffer);
        m_led.start();
    }

    public void setLedData() {
        m_led.setData(m_ledBuffer);
    }

    public AddressableLEDBuffer getLedBuffer() {
        return m_ledBuffer;
    }

    public void setRainbowAnimation() {
        new RainbowAnimation();
    }

    public void setBlankAnimation() {
        new BlankAnimation();
    }

    public void turnOffLeds() {
        for (int i = 0; i < m_ledBuffer.getLength(); i++) {
            m_ledBuffer.setRGB(i, 0, 0, 0);
        }
        m_led.setData(m_ledBuffer);
    }
}
