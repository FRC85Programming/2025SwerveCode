package frc.robot.subsystems.leds;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.Seconds;

import java.sql.Driver;

import edu.wpi.first.units.Units;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class LedSubsystem extends SubsystemBase {

    private final AddressableLED led;
    private final AddressableLEDBuffer ledBuffer;

    private static boolean inUse = false;

    private static final Distance ledSpacing = Meters.of(1 / 120.0);

    private static final LEDPattern blank = LEDPattern.solid(Color.kBlack);
    private static final LEDPattern red = LEDPattern.solid(Color.kRed).breathe(Units.Seconds.of(4));
    private static final LEDPattern blue = LEDPattern.solid(Color.kBlue).breathe(Units.Seconds.of(4));
    private static final LEDPattern blinkingorange = LEDPattern.solid(Color.kOrange).blink(Seconds.of(0.1));
    private static final LEDPattern slowblinkinggreen = LEDPattern.solid(Color.kGreen).blink(Seconds.of(0.5));
    private static final LEDPattern fastblinkinggreen = LEDPattern.solid(Color.kGreen).blink(Seconds.of(0.1));
    private static final LEDPattern iceGradient = LEDPattern.gradient(LEDPattern.GradientType.kContinuous, Color.kAqua, Color.kLightBlue);
    private static final LEDPattern ambientIce =
        iceGradient
            .scrollAtAbsoluteSpeed(MetersPerSecond.of(0.03), ledSpacing)  // Slow Scroll                              
            .blink(Seconds.of(0.07));       
    private static final LEDPattern scrollingRainbow = LEDPattern.rainbow(255, 255).scrollAtAbsoluteSpeed(MetersPerSecond.of(0.1), ledSpacing);

    private static LEDPattern currentPattern;

    public LedSubsystem() {
        led = new AddressableLED(Constants.LEDConstants.ledPWMPort);
        ledBuffer = new AddressableLEDBuffer(12);
        led.setLength(ledBuffer.getLength());
        led.setData(ledBuffer);
        led.start();
    }

    public static void setPattern(LEDPattern pattern) {
        inUse = true;
        currentPattern = pattern;
    }

    public static void stopPattern() {
        inUse = false;
    }

    @Override
    public void periodic() {
        if (!inUse) {
            currentPattern = getFallbackPattern();
        }
        currentPattern.applyTo(ledBuffer);
        led.setData(ledBuffer);
    }

    public static void startRainbow() {
        setPattern(scrollingRainbow);
    }

    public static void startBlank() {
        setPattern(blank);
    }

    public static void runBlank() {
        setPattern(blank);
    }

    public static void startSlowBlinkingGreen() {
        setPattern(slowblinkinggreen);
    }

    public static void startFastBlinkingGreen() {
        setPattern(fastblinkinggreen);
    }

    public static void startIceAnimation() {
        setPattern(ambientIce);
    }

    public static LEDPattern getFallbackPattern() {
        if (DriverStation.isDisabled()) {
            return ambientIce;
        } else if (DriverStation.isAutonomous()) {
            return blinkingorange;
        } else if (DriverStation.isTeleop()) {
            if (DriverStation.getAlliance().get() == DriverStation.Alliance.Red) {
                return blue;
            } else {
                return red;
            }
        } else {
            return blank;
        }
    }
}
