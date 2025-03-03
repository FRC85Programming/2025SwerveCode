package frc.robot.commands.auto;

import java.util.function.Supplier;

import org.photonvision.PhotonUtils;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.actions.scoring.GoToPosition;
import frc.robot.commands.actions.swerve.DriveToPose;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.endeffector.EndEffectorSubsystem;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.util.Positions;

public class PreGoToPosition extends Command {

    SwerveSubsystem swerve;
    ElevatorSubsystem elevator;
    EndEffectorSubsystem endeffector;
    IntakeSubsystem intake;
    Positions position;
    Supplier<Pose2d> scorePose;
    double goToPositionRange = 1;
    Command moveSubsystemsCommand;
    
    public PreGoToPosition(SwerveSubsystem swerve, ElevatorSubsystem elevator, EndEffectorSubsystem endeffector, IntakeSubsystem intake, Supplier<Pose2d> scorePose, Positions position) {
        this.swerve = swerve;
        this.elevator = elevator;
        this.endeffector = endeffector;
        this.intake = intake;
        this.position = position;
        this.scorePose = scorePose;

        SmartDashboard.putNumber("Go To Position Range", 1);

        addRequirements(elevator, endeffector);
    }

    @Override
    public void initialize() {
        moveSubsystemsCommand = new GoToPosition(elevator, endeffector, intake, position, true);

    }

    @Override
    public void execute() {
        goToPositionRange = SmartDashboard.getNumber("Go To Position Range", 1);

        // If the robot is within a certain range of the selected scoring position, automatically move the subsystems to the scoring positions 
        if (PhotonUtils.getDistanceToPose(swerve.getPose(), scorePose.get()) < goToPositionRange) {
            elevator.setSetpoint(elevator.getSetpoint(position));
            endeffector.setSetpoint(endeffector.getSetpoint(position));
        }
    
    }

    @Override
    public boolean isFinished() {
        return elevator.atSetpoint();

    }

    @Override
    public void end(boolean interrupted) {
        elevator.setSetpoint(elevator.getSetpoint(Positions.HOME));
        endeffector.setSetpoint(endeffector.getSetpoint(Positions.HOME));
    }
}