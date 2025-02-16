package frc.robot.commands.swervedrive.auto.autos;

import java.awt.Robot;
import java.util.List;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants;
import frc.robot.commands.swervedrive.auto.actions.DriveAndHoldPose;
import frc.robot.commands.swervedrive.auto.actions.HoldPose;
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
        autoScorePositions = webServer.getSelectedAuto();
    }  
    
    @Override
    public void initialize() {
        for (int i = 0; i < autoScorePositions.length; i++) {
            selectedReefPose = swerveSubsystem.getSelectedScorePositionPose(autoScorePositions[i]);
            autoRoutine.addCommands(new DriveAndHoldPose(swerveSubsystem, () -> selectedReefPose), 
                new DriveAndHoldPose(swerveSubsystem, () -> swerveSubsystem.getSelectedIntakePositionPose(webServer.getSelectedIntakePosition())));
        }
        autoRoutine.schedule();
    }
}
