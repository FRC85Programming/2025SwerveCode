package frc.robot.subsystems.endeffector;


import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;

public class EndEffectorSimulation extends SubsystemBase {

    private final SingleJointedArmSim pivotSim;

    public EndEffectorSimulation() {
        // Create the arm simulation object
        pivotSim =
            new SingleJointedArmSim(
                DCMotor.getNeoVortex(1),
                Constants.EndEffectorConstants.GEAR_RATIO,
                Constants.EndEffectorConstants.ARM_MASS_KG,
                Constants.EndEffectorConstants.ARM_LENGTH_METERS,
                Constants.EndEffectorConstants.MIN_ANGLE_RAD,
                Constants.EndEffectorConstants.MAX_ANGLE_RAD,
                false,
                0);
    }

    /** Apply motor voltage */
    public void setInputVoltage(double voltage) {
        pivotSim.setInputVoltage(voltage);
    }

    public void updateSim() {
        // Advance the sim 0.02 seconds
        pivotSim.update(0.02);

        // Simulate battery under load
        RoboRioSim.setVInVoltage(BatterySim.calculateDefaultBatteryLoadedVoltage(pivotSim.getCurrentDrawAmps()));
    }

    /** Get current simulated pivot angle */
    public double getSimAngle() {
        return pivotSim.getAngleRads();
    }

    public Pose3d getPivotSimPose() {
        return new Pose3d(Constants.EndEffectorConstants.PIVOT_ROOT_X, Constants.EndEffectorConstants.PIVOT_ROOT_Y, Constants.EndEffectorConstants.PIVOT_ROOT_Z, new Rotation3d(0.0, getSimAngle(), 0.0));

    }
}
