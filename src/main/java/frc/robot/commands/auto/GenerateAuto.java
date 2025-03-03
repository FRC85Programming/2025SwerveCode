package frc.robot.commands.auto;

import java.rmi.server.Skeleton;
import java.util.List;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotContainer;
import frc.robot.commands.actions.scoring.EndEffectorIntake;
import frc.robot.commands.actions.swerve.DriveAndHoldPose;
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
            Pose2d selectedReefPose = swerve.getSelectedScorePositionPose(autoScorePositions[i].substring(0, 1));
            Pose2d selectedSourcePose = swerve.getSelectedIntakePositionPose(autoScorePositions[i].substring(2, 3));
            Positions level = swerve.getLevelFromString(autoScorePositions[i].substring(1, 2));

            autoRoutine.addCommands(new ScoreSequence(swerve, elevator, endeffector, intake, () -> selectedReefPose, level),
                new DriveAndHoldPose(swerve, () -> selectedSourcePose));
        }
        autoRoutine.schedule();
    }
}
