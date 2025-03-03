package frc.robot.subsystems.elevator;

import org.littletonrobotics.junction.Logger;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ElevatorSimulation extends SubsystemBase {

    private final ElevatorSim elevatorSim;
    EncoderSim elevatorEncoderSim;

    public ElevatorSimulation(ElevatorSubsystem elevator) {
        
      elevatorSim = new ElevatorSim(
          DCMotor.getNeoVortex(2),
          Constants.ElevatorConstants.GEAR_RATIO,
          Constants.ElevatorConstants.CARRIAGE_MASS,
          Constants.ElevatorConstants.DRUM_RADIUS,
          Constants.ElevatorConstants.MIN_HEIGHT,
          Constants.ElevatorConstants.MAX_HEIGHT,
          false,
          0,
          0.0, 
          0.000001);

       elevatorEncoderSim = new EncoderSim(elevator.getElevatorEncoderObject());
    }

     public void updateSim() {
        // Advance the sim 0.02 seconds
        elevatorSim.update(0.02);

        // Simulate battery under load
        RoboRioSim.setVInVoltage(BatterySim.calculateDefaultBatteryLoadedVoltage(elevatorSim.getCurrentDrawAmps()));
    }

    public void setInputVoltage(double volts) {
        elevatorSim.setInputVoltage(volts);
    }

    public void setSimEncoder(int counts) {
        elevatorEncoderSim.setCount(counts);
    }

    public int getSimEncoder() {
        return elevatorEncoderSim.getCount();
    }

    public double getElevatorSimPosition() {
        return elevatorSim.getPositionMeters();
    }

    public Pose3d getElevatorSimPose() {
        return new Pose3d(Constants.ElevatorConstants.ELEVATOR_ROOT_X, Constants.ElevatorConstants.ELEVATOR_ROOT_Y, Constants.ElevatorConstants.ELEVATOR_ROOT_Z + elevatorSim.getPositionMeters(), new Rotation3d());
    }
}
