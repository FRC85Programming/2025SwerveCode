package frc.robot.commands.autos;

import java.util.List;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.actions.swerve.DriveAndHoldPose;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.subsystems.webserver.WebServer;

public class GenerateAuto extends Command {
    SwerveSubsystem swerveSubsystem;
    String[] autoScorePositions;
    List<Command> autoCommands;
    WebServer webServer;
    SequentialCommandGroup autoRoutine = new SequentialCommandGroup();
    boolean scheduled = false;
    Pose2d selectedReefPose;

    public GenerateAuto(SwerveSubsystem swerveSubsystem) {
        this.swerveSubsystem = swerveSubsystem;
        webServer = swerveSubsystem.getWebServer();
    }  
    
    @Override
    public void initialize() {
        autoScorePositions = webServer.getSelectedAuto();
        for (int i = 0; i < autoScorePositions.length; i++) {
            Pose2d scorePose = swerveSubsystem.getSelectedScorePositionPose(autoScorePositions[i]);
            autoRoutine.addCommands(
                new DriveAndHoldPose(swerveSubsystem, () -> scorePose), 
                new DriveAndHoldPose(swerveSubsystem, () -> swerveSubsystem.getSelectedIntakePositionPose(webServer.getSelectedIntakePosition()))
            );
        }
        autoRoutine.schedule();
    }
}
