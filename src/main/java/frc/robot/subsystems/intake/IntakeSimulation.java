package frc.robot.subsystems.intake;


import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;

public class IntakeSimulation extends SubsystemBase {

    private final SingleJointedArmSim armSim;

    public IntakeSimulation() {
        // Create the arm simulation object
        armSim =
            new SingleJointedArmSim(
                DCMotor.getNeoVortex(1),
                Constants.IntakeConstants.GEAR_RATIO,
                Constants.IntakeConstants.ARM_MASS_KG,
                Constants.IntakeConstants.ARM_LENGTH_METERS,
                Constants.IntakeConstants.MIN_ANGLE_RAD,
                Constants.IntakeConstants.MAX_ANGLE_RAD,
                false,
                0);
    }

    /** Apply motor voltage */
    public void setInputVoltage(double voltage) {
        armSim.setInputVoltage(voltage);
    }

    public void updateSim() {
        // Advance the sim 0.02 seconds
        armSim.update(0.02);

        // Simulate battery under load
        RoboRioSim.setVInVoltage(BatterySim.calculateDefaultBatteryLoadedVoltage(armSim.getCurrentDrawAmps()));

        // Log poses 
        SmartDashboard.putNumber("Arm Angle", armSim.getAngleRads());
    }

    /** Get current simulated arm angle */
    public double getSimAngle() {
        return armSim.getAngleRads();
    }

    public Pose3d getIntakeSimPose() {
        return new Pose3d(Constants.IntakeConstants.INTAKE_ROOT_X, 0, Constants.IntakeConstants.INTAKE_ROOT_Z, new Rotation3d(0, getSimAngle(), 0.0));

    }
}
