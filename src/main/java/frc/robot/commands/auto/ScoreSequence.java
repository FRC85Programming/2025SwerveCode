package frc.robot.commands.auto;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.actions.swerve.DriveAndHoldPose;
import frc.robot.commands.actions.swerve.DriveToPose;
import frc.robot.commands.actions.swerve.HoldPose;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.endeffector.EndEffectorSubsystem;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.util.Positions;

public class ScoreSequence extends ParallelCommandGroup {
    public ScoreSequence(SwerveSubsystem swerve, ElevatorSubsystem elevator, EndEffectorSubsystem endeffector, IntakeSubsystem intake, Supplier<Pose2d> scorePose, Supplier<Pose2d> scorePosePushback, Positions level) {
        addCommands(new DriveToPose(swerve, scorePose), new PreGoToPosition(swerve, elevator, endeffector, intake, scorePose, level));
    }
}
