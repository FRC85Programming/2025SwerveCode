package frc.robot.commands.actions.swerve;

import java.util.function.Supplier;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.leds.LedSubsystem;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;

public class HoldPose extends Command {

    SwerveSubsystem swerve;
    Supplier<Pose2d> poseFinal;
    Pose2d currentPose;

    PIDController xTranslationPID, yTranslationPID;
    PIDController rotationPID;

    boolean endable;

    public HoldPose(SwerveSubsystem swerve, Supplier<Pose2d> finalPose, boolean endable) {
        this.swerve = swerve;
        this.poseFinal = finalPose;
        this.xTranslationPID = new PIDController(4.0, 
                                                0.0, 
                                                0.0);
        this.yTranslationPID = new PIDController(4.0, 
                                                0.0, 
                                                0.0);
        this.rotationPID = new PIDController(0.4
        , 
                                             0.0, 
                                             0.0);
        
        xTranslationPID.setTolerance(0.05);
        yTranslationPID.setTolerance(0.05);

        rotationPID.setTolerance(0.01);

        this.endable = endable;
        
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

        LedSubsystem.startSlowBlinkingGreen();
    }

    @Override
    public void execute() {
        rotationPID.setP(SmartDashboard.getNumber("Rot P", 0.4));
        xTranslationPID.setP(SmartDashboard.getNumber("X P", 5));
        yTranslationPID.setP(SmartDashboard.getNumber("Y P", 5));
        rotationPID.setTolerance(SmartDashboard.getNumber("Rot Tolerance", 0.01));


        currentPose = swerve.getPose();
        
        double xSpeed = xTranslationPID.calculate(currentPose.getX());
        double ySpeed = yTranslationPID.calculate(currentPose.getY());
        double thetaSpeed = rotationPID.calculate(currentPose.getRotation().getRadians());
        
        ChassisSpeeds wheelSpeeds = new ChassisSpeeds(xSpeed, ySpeed, thetaSpeed);

        if (xTranslationPID.atSetpoint() && yTranslationPID.atSetpoint() && rotationPID.atSetpoint()) {
            LedSubsystem.startFastBlinkingGreen();
        }

        swerve.driveFieldOriented(wheelSpeeds);
    }

    @Override
    public boolean isFinished() {
        boolean xTranslationDone = xTranslationPID.atSetpoint();
        boolean yTranslationDone = yTranslationPID.atSetpoint();
        boolean rotationDone = rotationPID.atSetpoint();
        
        return xTranslationDone && yTranslationDone && rotationDone && endable;
    }
    
    @Override
    public void end(boolean interrupted) {
        SmartDashboard.putBoolean("Ended Holdpose", true);
        LedSubsystem.stopPattern();
        swerve.drive(new ChassisSpeeds(0, 0, 0));
    }
}