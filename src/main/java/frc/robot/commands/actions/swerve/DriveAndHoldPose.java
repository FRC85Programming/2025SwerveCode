package frc.robot.commands.actions.swerve;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import edu.wpi.first.math.geometry.Pose2d;
import java.util.function.Supplier;

public class DriveAndHoldPose extends SequentialCommandGroup {

    public DriveAndHoldPose(SwerveSubsystem swerveSubsystem, Supplier<Pose2d> position) {
        addCommands(
            new GoToPose(swerveSubsystem, position),
            new HoldPose(swerveSubsystem, position)
        );
    }
}
