package frc.robot.commands.swervedrive.auto.actions;

import java.util.function.Supplier;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import swervelib.SwerveDrive;

public class DriveStraightToPose extends Command {
    private final SwerveSubsystem drivetrain;
    private final Supplier<Pose2d> targetPose;
    private final PIDController xController = new PIDController(1.0, 0, 0);
    private final PIDController yController = new PIDController(1.0, 0, 0);
    private final PIDController thetaController = new PIDController(2.0, 0, 0);

    public DriveStraightToPose(SwerveSubsystem drivetrain, Supplier<Pose2d> targetPose) {
        this.drivetrain = drivetrain;
        this.targetPose = targetPose;
        addRequirements(drivetrain);
    }

    @Override
    public void execute() {
        Pose2d currentPose = drivetrain.getPose();

        // Calculate position errors
        double xError = targetPose.get().getX() - currentPose.getX();
        double yError = targetPose.get().getY() - currentPose.getY();
        double thetaError = targetPose.get().getRotation().getRadians() - currentPose.getRotation().getRadians();

        // Apply PID control for straight-line movement
        double xSpeed = xController.calculate(currentPose.getX(), targetPose.get().getX());
        double ySpeed = yController.calculate(currentPose.getY(), targetPose.get().getY());
        double turnSpeed = thetaController.calculate(currentPose.getRotation().getRadians(), targetPose.get().getRotation().getRadians());

        // Normalize to go straight toward the target
        double distance = Math.hypot(xError, yError);
        double headingToTarget = Math.atan2(yError, xError);

        double forwardSpeed = distance * Math.cos(headingToTarget - currentPose.getRotation().getRadians());
        double strafeSpeed = distance * Math.sin(headingToTarget - currentPose.getRotation().getRadians());

        drivetrain.drive(new ChassisSpeeds(forwardSpeed, strafeSpeed, turnSpeed));
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.drive(new ChassisSpeeds());;
    }
}

