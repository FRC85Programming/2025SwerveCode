package frc.robot.commands.actions.scoring;

import java.security.Timestamp;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.endeffector.EndEffectorSubsystem;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.util.Positions;

public class EndEffectorIntake extends Command {

    EndEffectorSubsystem endeffector;
    boolean shouldIntake;
    ElevatorSubsystem elevator;
    IntakeSubsystem intake;
    boolean endable;
    double startsecs = 0;


    public EndEffectorIntake (EndEffectorSubsystem endeffector, ElevatorSubsystem elevator, IntakeSubsystem intake, boolean shouldIntake, boolean endable) {
        this.endeffector = endeffector;
        this.shouldIntake = shouldIntake;
        this.elevator = elevator;
        this.intake = intake;
        this.endable = endable;
    }

    @Override
    public void execute() {
        if (!shouldIntake) {
            endeffector.runRollers(-0.6);
        } else {
            endeffector.runRollers(0.6);
        }
    }

    @Override
    public void initialize() {
        startsecs = Timer.getFPGATimestamp();
    }

    @Override
    public boolean isFinished() {
        if (Timer.getFPGATimestamp() - startsecs > 0.3) {
            if (shouldIntake == false) {
                return endable && !endeffector.getCoralSwitch();
            } else {
                return endable && endeffector.getCoralSwitch();
            }
        } else {
            return false;
        }
    }
    
    @Override
    public void end(boolean interrupted) {
        endeffector.runRollers(0);
        DriverStation.reportWarning("Intake end", false);
        if (!shouldIntake && !endable) {
            endeffector.setSetpoint(0);
            elevator.setSetpoint(0);
        }
    }
}