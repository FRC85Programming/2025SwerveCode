package frc.robot.commands.auto;

import java.rmi.server.Skeleton;
import java.util.List;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.RobotContainer;
import frc.robot.commands.actions.scoring.EndEffectorIntake;
import frc.robot.commands.actions.swerve.DriveAndHoldPose;
import frc.robot.commands.actions.swerve.DriveToPose;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.endeffector.EndEffectorSubsystem;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.subsystems.webserver.WebServer;
import frc.robot.util.Positions;

public class GenerateAuto extends Command {
    SwerveSubsystem swerve;
    ElevatorSubsystem elevator;
    EndEffectorSubsystem endeffector;
    IntakeSubsystem intake;
    WebServer webServer;

    String[] autoScorePositions;
    List<Command> autoCommands;
    SequentialCommandGroup autoRoutine = new SequentialCommandGroup();
    boolean scheduled = false;
    Pose2d selectedReefPose;

    public GenerateAuto(SwerveSubsystem swerve, ElevatorSubsystem elevator, EndEffectorSubsystem endeffector, IntakeSubsystem intake) {
        this.swerve = swerve;
        this.elevator = elevator;
        this.endeffector = endeffector;
        this.intake = intake;
        webServer = swerve.getWebServer();
    }  
    
    @Override
    public void initialize() {
        autoScorePositions = webServer.getSelectedAuto();

        for (int i = 0; i < autoScorePositions.length; i++) {
            Pose2d selectedReefPose = swerve.getScorePoseFromString(autoScorePositions[i].substring(0, 1));
            Pose2d selectedSourcePose = swerve.getSelectedIntakePositionPose(autoScorePositions[i].substring(2, 3));
            Positions level = swerve.getLevelFromString(autoScorePositions[i].substring(1, 2));

            if (selectedReefPose != null && selectedSourcePose != null && level != null) {
                autoRoutine.addCommands(
                    new ScoreSequence(swerve, elevator, endeffector, intake, () -> selectedReefPose, level),
                    new WaitCommand(0.2),
                    new EndEffectorIntake(endeffector, elevator, intake, false, true),
                    new WaitCommand(0.2),
                    new InstantCommand(() -> elevator.setSetpoint(0)),
                    new InstantCommand(() -> endeffector.setSetpoint(0)),
                    new ParallelCommandGroup(new DriveAndHoldPose(swerve, () -> selectedSourcePose, true), 
                            new EndEffectorIntake(endeffector, elevator, intake, true, true)));
            }
        }
        autoRoutine.schedule();
    }
}
