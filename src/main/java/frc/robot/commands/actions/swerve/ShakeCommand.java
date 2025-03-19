package frc.robot.commands.actions.swerve;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;

public class ShakeCommand extends Command {
    SwerveSubsystem swerve;
    Timer timer = new Timer();
    double shakespeed = 0.3;

    public ShakeCommand(SwerveSubsystem swerve) {
        this.swerve = swerve;
    }

    @Override
    public void initialize() {
        timer.start();
    }

    @Override
    public void execute() {
        if (timer.get() > 0.1) {
            shakespeed = -shakespeed;
        } else {
            shakespeed = Math.abs(shakespeed);
            timer.reset();
        }

        swerve.drive(new ChassisSpeeds(0, 0, shakespeed));
    }
}
