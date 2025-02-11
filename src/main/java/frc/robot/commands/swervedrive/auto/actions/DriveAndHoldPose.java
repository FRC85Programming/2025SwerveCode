package frc.robot.commands.swervedrive.auto.actions;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import edu.wpi.first.math.geometry.Pose2d;
import java.util.function.Supplier;

public class DriveAndHoldPose extends SequentialCommandGroup {

    public DriveAndHoldPose(SwerveSubsystem swerveSubsystem, Supplier<Pose2d> position) {
        addCommands(
            swerveSubsystem.driveToPose(position),
            new HoldPose(position, swerveSubsystem)
        );
    }
}
