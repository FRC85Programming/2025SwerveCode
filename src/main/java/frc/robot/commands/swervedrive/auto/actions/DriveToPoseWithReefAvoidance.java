package frc.robot.commands.swervedrive.auto.actions;

import org.littletonrobotics.junction.Logger;

import com.pathplanner.lib.util.GeometryUtil;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.util.GeomUtil;

public class DriveToPoseWithReefAvoidance extends Command {
    private final SwerveSubsystem swerve;
    private final Pose2d targetPose;
    public static final Translation2d reefCenter =
        new Translation2d(Units.inchesToMeters(176.746), Units.feetToMeters(26.58) / 2.0);
    public static final double faceLength = Units.inchesToMeters(36.792600);
    private final double reefClearance = 0.3;
    private final double maxAvoidance = 2;
    private final double maxDistanceReefLineup = 1.5;

    public DriveToPoseWithReefAvoidance(SwerveSubsystem swerve, Pose2d targetPose) {
        this.swerve = swerve;
        this.targetPose = targetPose;
        addRequirements(swerve);
    }

    @Override
    public void execute() {
        var offset = swerve.getPose().relativeTo(targetPose);
        double yDistance = Math.abs(offset.getY());
        double xDistance = Math.abs(offset.getX());
        double shiftXT =
            MathUtil.clamp(
                (yDistance / (faceLength * 2)) + ((xDistance - 0.3) / (faceLength * 3)),
                0.0,
                1.0);
        double shiftYT =
            MathUtil.clamp(yDistance <= 0.2 ? 0.0 : offset.getX() / faceLength, 0.0, 1.0);
        Pose2d chasePose = targetPose.transformBy(
            GeomUtil.toTransform2d(
                -shiftXT * maxDistanceReefLineup,
                Math.copySign(shiftYT * maxDistanceReefLineup * 0.8, offset.getY())));

        Logger.recordOutput("Chased Pose", chasePose);

        // Drive towards chase pose
        swerve.driveStraightToPose(chasePose);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        
    }
}