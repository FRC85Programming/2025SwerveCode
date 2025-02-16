package frc.robot.commands.swervedrive.auto.actions;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.swervedrive.auto.actions.HoldPose;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import edu.wpi.first.math.geometry.Pose2d;
import java.util.function.Supplier;

public class DriveAndHoldPose extends SequentialCommandGroup {

    public DriveAndHoldPose(SwerveSubsystem swerveSubsystem, Supplier<Pose2d> position) {
        addCommands(
            new GoToPose(position, swerveSubsystem),
            new HoldPose(position, swerveSubsystem)
        );
    }
}
