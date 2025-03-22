package frc.robot.commands.actions.scoring;

import java.security.Timestamp;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
    double coraltime = 0;


    public EndEffectorIntake (EndEffectorSubsystem endeffector, ElevatorSubsystem elevator, IntakeSubsystem intake, boolean shouldIntake, boolean endable) {
        this.endeffector = endeffector;
        this.shouldIntake = shouldIntake;
        this.elevator = elevator;
        this.intake = intake;
        this.endable = endable;
    }

    @Override
    public void execute() {
        checkSwitch();
        if (!shouldIntake) {
            endeffector.runRollers(-0.4);
        } else {
            endeffector.runRollers(0.6);
        }
    }

    @Override
    public void initialize() {
        startsecs = 0;
    }

    @Override
    public boolean isFinished() {
        if (shouldIntake == true) {
            return endable && endeffector.getCoralSwitch() && Timer.getFPGATimestamp() - coraltime > 0.2 && coraltime > 0;
        } else {
            return endable && !endeffector.getCoralSwitch() && Timer.getFPGATimestamp() - startsecs > 0.3 && startsecs != 0;
        }
    }

    private void checkSwitch() {
        if (endeffector.getCoralSwitch()) {
            if (coraltime == 0) {
                coraltime = Timer.getFPGATimestamp();
            }
        } else {
            if (startsecs == 0) {
                startsecs = Timer.getFPGATimestamp();
            }
            coraltime = 0;
        }
        SmartDashboard.putNumber("Coral Time", coraltime);
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