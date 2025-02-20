package frc.robot.subsystems.intake;


import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.Mechanism2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismLigament2d;
import edu.wpi.first.wpilibj.smartdashboard.MechanismRoot2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.robot.Constants;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;

public class  IntakeSim {

    private final SingleJointedArmSim armSim;

    public IntakeSim() {
        // Create the arm simulation object
        armSim =
            new SingleJointedArmSim(
                DCMotor.getNEO(1),
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
        Logger.recordOutput("Arm", new Pose3d(Constants.IntakeConstants.INTAKE_ROOT_X, Constants.IntakeConstants.INTAKE_ROOT_Y, Constants.IntakeConstants.INTAKE_ROOT_Z, new Rotation3d(getSimAngle(), 0, 0.0)));
    }

    /** Get current simulated arm angle */
    public double getSimAngle() {
        return armSim.getAngleRads();
    }
}
