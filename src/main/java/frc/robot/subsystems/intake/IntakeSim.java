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
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;

public class  IntakeSim {

    // Arm constants
    private static final double ARM_LENGTH_METERS = 0.75;
    private static final double ARM_MASS_KG = 1.3;
    private static final double GEAR_RATIO = 100.0;
    private static final double MIN_ANGLE_RAD = Math.toRadians(100);
    private static final double MAX_ANGLE_RAD = Math.toRadians(-130);

    double rootX = 0;
    double rootY = 0.32;
    double rootZ = 0.292;

    private final SingleJointedArmSim armSim;

    private final Mechanism2d mech2d;
    private final MechanismRoot2d armRoot;
    private final MechanismLigament2d armLigament;

    public IntakeSim() {
        armSim =
            new SingleJointedArmSim(
                DCMotor.getNEO(1),
                GEAR_RATIO,
                ARM_MASS_KG,
                ARM_LENGTH_METERS,
                MIN_ANGLE_RAD,
                MAX_ANGLE_RAD,
                false,
                Units.degreesToRadians(95));

        mech2d = new Mechanism2d(2, 2); 

        armRoot = mech2d.getRoot("ArmPivot", rootX, rootY); 

        armLigament = new MechanismLigament2d("Arm", ARM_LENGTH_METERS, 0, 6, new Color8Bit(255, 0, 0));
        armRoot.append(armLigament);

        SmartDashboard.putData("Arm Sim", mech2d);
        //SmartDashboard.putData("Arm Field", field);
    }

    /** Apply motor voltage */
    public void setInputVoltage(double voltage) {
        armSim.setInputVoltage(voltage);
    }

    public void updateSim() {
        armSim.update(0.02);

        // Simulate battery under load
        RoboRioSim.setVInVoltage(BatterySim.calculateDefaultBatteryLoadedVoltage(armSim.getCurrentDrawAmps()));

        armLigament.setAngle(Math.toDegrees(armSim.getAngleRads()));

        SmartDashboard.putData("Arm Sim", mech2d);
        Logger.recordOutput("Arm", new Pose3d(rootX, rootY, rootZ, new Rotation3d(Units.radiansToDegrees(getSimAngle()), 0, 0.0)));
    }

    /** Get current simulated arm angle */
    public double getSimAngle() {
        return armSim.getAngleRads() - Units.degreesToRadians(95);
    }
}
