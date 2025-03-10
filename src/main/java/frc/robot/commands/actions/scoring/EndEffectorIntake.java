package frc.robot.commands.actions.scoring;

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
            endeffector.runRollers(-0.7);
        } else {
            endeffector.runRollers(0.7);
        }
    }

    @Override
    public boolean isFinished() {
        return endable && !endeffector.getCoralSwitch();
    }
    
    @Override
    public void end(boolean interrupted) {
        endeffector.runRollers(0);
        if (!shouldIntake) {
            endeffector.setSetpoint(0);
            elevator.setSetpoint(0);
        }
    }
}