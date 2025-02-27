package frc.robot.commands.swervedrive.auto.actions;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.endeffector.EndEffectorSubsystem;
import frc.robot.subsystems.intake.IntakeSubsystem;

public class EndEffectorWheels extends Command {

    EndEffectorSubsystem endeffector;
    boolean shouldIntake;

    public EndEffectorWheels (EndEffectorSubsystem endeffector, boolean shouldIntake) {
        this.endeffector = endeffector;
        this.shouldIntake = shouldIntake;
    }

    @Override
    public void execute() {
        if (shouldIntake) {
            endeffector.runRollers(-0.7);
        } else {
            endeffector.runRollers(0.7);
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }
    
    @Override
    public void end(boolean interrupted) {
        endeffector.runRollers(0);
    }
}