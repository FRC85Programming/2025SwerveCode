package frc.robot.commands.swervedrive.auto.actions;

import java.util.function.Supplier;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;

public class HoldPose extends Command {

    private final SwerveSubsystem swerve;
    private Supplier<Pose2d> poseFinal;
    private Pose2d currentPose;

    private final PIDController xTranslationPID, yTranslationPID;
    private final PIDController rotationPID;

    public HoldPose(SwerveSubsystem swerve, Supplier<Pose2d> finalPose) {
        this.swerve = swerve;
        this.poseFinal = finalPose;
        this.xTranslationPID = new PIDController(4.0, 
                                                0.0, 
                                                0.0);
        this.yTranslationPID = new PIDController(4.0, 
                                                0.0, 
                                                0.0);
        this.rotationPID = new PIDController(3.0, 
                                             0.0, 
                                             0.0);
        
        xTranslationPID.setTolerance(0.05);
        yTranslationPID.setTolerance(0.05);
        rotationPID.setTolerance(0.5);
        
        addRequirements(swerve);
    }

    @Override
    public void initialize() {
        currentPose = swerve.getPose();

        xTranslationPID.reset();
        yTranslationPID.reset();
        rotationPID.reset();

        xTranslationPID.setSetpoint(poseFinal.get().getX());
        yTranslationPID.setSetpoint(poseFinal.get().getY());
        rotationPID.setSetpoint(poseFinal.get().getRotation().getRadians());
    }

    @Override
    public void execute() {
        currentPose = swerve.getPose();
        
        double xSpeed = xTranslationPID.calculate(currentPose.getX());
        double ySpeed = yTranslationPID.calculate(currentPose.getY());
        double thetaSpeed = rotationPID.calculate(currentPose.getRotation().getRadians());
        
        ChassisSpeeds wheelSpeeds = new ChassisSpeeds(xSpeed, ySpeed, thetaSpeed);

        swerve.driveFieldOriented(wheelSpeeds);
    }

    @Override
    public boolean isFinished() {
        boolean xTranslationDone = xTranslationPID.atSetpoint();
        boolean yTranslationDone = yTranslationPID.atSetpoint();
        boolean rotationDone = rotationPID.atSetpoint();
        
        return xTranslationDone && yTranslationDone && rotationDone;
    }
    
    @Override
    public void end(boolean interrupted) {
        swerve.drive(new ChassisSpeeds(0, 0, 0));
    }
}