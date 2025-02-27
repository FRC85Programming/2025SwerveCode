package frc.robot.util;

public class MotorUtil {
    private static final double K_T = 0.02475; // Nm/A (Torque Constant)
    private static final double R = 0.0275; // Ohms (Motor Resistance)
    private static final double K_V = 59.2; // rad/s per Volt (Back-EMF Constant)
    private static final double GRAVITY = 9.81; // m/s²

    /**
     * Converts required torque and angular velocity to voltage for a NEO Vortex motor.
     * 
     * @param torque The required torque in Nm.
     * @param angularVelocity The current angular velocity in rad/s.
     * @return The required voltage to achieve the given torque and velocity.
     */
    public static double torqueToVoltage(double torque, double angularVelocity) {
        double current = torque / K_T; // Convert torque to current
        double voltage = (current * R) + (angularVelocity / K_V); // Compute voltage
        return voltage;
    }

    /**
     * Calculates the required torque to hold a pivoting arm at a given angle.
     *
     * @param mass Mass of the arm in kg.
     * @param centerOfMass Distance from pivot to center of mass in meters.
     * @param angleRadians Angle of the arm from the downward vertical in radians.
     * @return The required torque in Nm.
     */
    public static double calculateTorque(double mass, double centerOfMass, double angleRadians) {
        return mass * GRAVITY * centerOfMass * Math.cos(angleRadians);
    }
}
