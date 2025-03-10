// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.CANifier.LEDChannel;
import com.pathplanner.lib.auto.NamedCommands;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SelectCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.OperatorConstants;
import frc.robot.Constants.PositionConstants;
import frc.robot.commands.drivebase.AbsoluteDriveAdv;
import frc.robot.commands.actions.scoring.EndEffectorIntake;
import frc.robot.commands.actions.scoring.GoToPosition;
import frc.robot.commands.actions.scoring.Intake;
import frc.robot.commands.actions.swerve.Climb;
import frc.robot.commands.actions.swerve.DriveAndHoldPose;
import frc.robot.commands.actions.swerve.IntakeWheels;
import frc.robot.commands.auto.GenerateAuto;
import frc.robot.commands.drivebase.AbsoluteDriveAdv;
import frc.robot.subsystems.climb.ClimbSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.endeffector.EndEffectorSubsystem;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import frc.robot.subsystems.webserver.WebServer;
import frc.robot.util.Positions;
import frc.robot.util.RobotStates;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.subsystems.leds.LedSubsystem;
import frc.robot.subsystems.leds.LedSubsystem;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.endeffector.EndEffectorSubsystem;


import java.io.File;
import java.util.Map;

import org.littletonrobotics.junction.Logger;

import swervelib.SwerveInputStream;
import swervelib.telemetry.SwerveDriveTelemetry;
import swervelib.telemetry.SwerveDriveTelemetry.TelemetryVerbosity;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer
{

  // Replace with CommandPS4Controller or CommandJoystick if needed
  final         CommandXboxController driverXbox = new CommandXboxController(0);
  final         CommandXboxController opXbox = new CommandXboxController(1);

  // The robot's subsystems and commands are defined here...
  private final SwerveSubsystem       drivebase  = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(),
                                                                                "swerve/neo"));
  private final IntakeSubsystem intake = new IntakeSubsystem();
  private final ElevatorSubsystem elevator = new ElevatorSubsystem();
  private final EndEffectorSubsystem endeffector = new EndEffectorSubsystem();
  private final ClimbSubsystem climb = new ClimbSubsystem();
  private static final LedSubsystem leds = new LedSubsystem();

  // Applies deadbands and inverts controls because joysticks
  // are back-right positive while robot
  // controls are front-left positive
  // left stick controls translation
  // right stick controls the rotational velocity 
  // buttons are quick rotation positions to different ways to face
  // WARNING: default buttons are on the same buttons as the ones defined in configureBindings
  AbsoluteDriveAdv closedAbsoluteDriveAdv = new AbsoluteDriveAdv(drivebase,
                                                                 () -> -MathUtil.applyDeadband(driverXbox.getLeftY(),
                                                                                               OperatorConstants.LEFT_Y_DEADBAND),
                                                                 () -> -MathUtil.applyDeadband(driverXbox.getLeftX(),
                                                                                               OperatorConstants.DEADBAND),
                                                                 () -> -MathUtil.applyDeadband(-driverXbox.getRightX(),
                                                                                               OperatorConstants.RIGHT_X_DEADBAND),
                                                                 driverXbox.getHID()::getYButtonPressed,
                                                                 driverXbox.getHID()::getAButtonPressed,
                                                                 driverXbox.getHID()::getXButtonPressed,
                                                                 driverXbox.getHID()::getBButtonPressed);

  /**
   * Converts driver input into a field-relative ChassisSpeeds that is controlled by angular velocity.
   */
  SwerveInputStream driveAngularVelocity = SwerveInputStream.of(drivebase.getSwerveDrive(),
                                                                () -> driverXbox.getLeftY() * -1,
                                                                () -> driverXbox.getLeftX() * -1)
                                                            .withControllerRotationAxis(() -> driverXbox.getRightX())
                                                            .deadband(OperatorConstants.DEADBAND)
                                                            .scaleTranslation(0.8)
                                                            .allianceRelativeControl(true);

  /**
   * Clone's the angular velocity input stream and converts it to a fieldRelative input stream.
   */
  SwerveInputStream driveDirectAngle = driveAngularVelocity.copy().withControllerHeadingAxis(driverXbox::getRightX,
                                                                                             driverXbox::getRightY)
                                                           .headingWhile(true);


  // Applies deadbands and inverts controls because joysticks
  // are back-right positive while robot
  // controls are front-left positive
  // left stick controls translation
  // right stick controls the desired angle NOT angular rotation
  Command driveFieldOrientedDirectAngle = drivebase.driveFieldOriented(driveDirectAngle);

  // Applies deadbands and inverts controls because joysticks
  // are back-right positive while robot
  // controls are front-left positive
  // left stick controls translation
  // right stick controls the angular velocity of the robot
  Command driveFieldOrientedAnglularVelocity = drivebase.driveFieldOriented(driveAngularVelocity);

  Command driveSetpointGen = drivebase.driveWithSetpointGeneratorFieldRelative(driveDirectAngle);

  SwerveInputStream driveAngularVelocitySim = SwerveInputStream.of(drivebase.getSwerveDrive(),
                                                                   () -> -driverXbox.getLeftY(),
                                                                   () -> -driverXbox.getLeftX())
                                                               .withControllerRotationAxis(() -> -driverXbox.getRawAxis(2))
                                                               .deadband(OperatorConstants.DEADBAND)
                                                               .scaleTranslation(0.8)
                                                               .allianceRelativeControl(true);
  // Derive the heading axis with math!
  SwerveInputStream driveDirectAngleSim     = driveAngularVelocitySim.copy()
                                                                     .withControllerHeadingAxis(() -> Math.sin(
                                                                                                    driverXbox.getRawAxis(
                                                                                                        2) * Math.PI) * (Math.PI * 2),
                                                                                                () -> Math.cos(
                                                                                                  driverXbox.getRawAxis(
                                                                                                        2) * Math.PI) *
                                                                                                      (Math.PI * 2))
                                                                     .headingWhile(true);

  Command driveFieldOrientedDirectAngleSim = drivebase.driveFieldOriented(driveAngularVelocitySim);

  Command driveSetpointGenSim = drivebase.driveWithSetpointGeneratorFieldRelative(driveDirectAngleSim);

  SendableChooser<Command> autoChooser = new SendableChooser<>();

  static RobotStates currentMode = RobotStates.CORAL;


  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer()
  {
    // Configure the trigger bindings
    configureBindings();
    rebind();
    DriverStation.silenceJoystickConnectionWarning(true);
    SmartDashboard.putNumber("Intake Wheel Speed", 0.5);

    SmartDashboard.putNumber("Floor Coral Pos", 0.1);
    SmartDashboard.putNumber("Floor Alg Pos", 0.1);

    
    SmartDashboard.putNumber("Rot P", 5.0);
    SmartDashboard.putNumber("X P", 5);
    SmartDashboard.putNumber("Y P", 5);
    SmartDashboard.putNumber("Rot Tolerance", 0.01);

    SmartDashboard.putNumber("Rot D", 0.1);
    SmartDashboard.putNumber("Floor Alg Pos", 10.5);
    SmartDashboard.putNumber("Lineup Offset", 0.3);

    SmartDashboard.putNumber("Coral Offset", -0.3);

    SmartDashboard.putNumber("X Tolerance", 0.05);
    SmartDashboard.putNumber("X Tolerance", 0.05);

  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary predicate, or via the
   * named factories in {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight joysticks}.
   */
  private void configureBindings()
  {
    // (Condition) ? Return-On-True : Return-on-False
    drivebase.setDefaultCommand(!RobotBase.isSimulation() ?
                                driveFieldOrientedAnglularVelocity :
                                driveFieldOrientedDirectAngleSim);

    if (Robot.isSimulation())
    {
      driverXbox.start().onTrue(Commands.runOnce(() -> drivebase.resetOdometry(new Pose2d(0, 0, new Rotation2d()))));
    }
    if (DriverStation.isTest())
    {
      drivebase.setDefaultCommand(driveFieldOrientedAnglularVelocity); // Overrides drive command above!

      //driverXbox.b().whileTrue(new InstantCommand(() -> intake.setArmAngle(Math.toRadians(45)), intake));
      driverXbox.x().whileTrue(Commands.runOnce(drivebase::lock, drivebase).repeatedly());
      driverXbox.y().whileTrue(drivebase.driveToDistanceCommand(1.0, 0.2));
      driverXbox.start().onTrue((Commands.runOnce(drivebase::zeroGyro)));
      driverXbox.back().whileTrue(drivebase.centerModulesCommand());
      driverXbox.leftBumper().onTrue(Commands.none());
      driverXbox.rightBumper().onTrue(Commands.none());
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes" })
  private void rebind() {
        // TODO: Make positions a press instead of a hold
        // Coral: L2, Algae: None
        driverXbox.a().whileTrue(new SelectCommand(
            Map.ofEntries(
                Map.entry(1, new GoToPosition(elevator, endeffector, intake, Positions.L2, false)),
                Map.entry(2, new InstantCommand())
            ),
            () -> currentMode == RobotStates.CORAL ? 1 : 2
        ));
        // Coral: L3, Algae: None
        driverXbox.b().whileTrue(new SelectCommand(
            Map.ofEntries(
                Map.entry(1, new GoToPosition(elevator, endeffector, intake, Positions.L3, false)),
                Map.entry(2, new InstantCommand())
            ),
            () -> currentMode == RobotStates.CORAL ? 1 : 2
        ));
        // Coral: L4, Algae: None
        driverXbox.y().whileTrue(new SelectCommand(
            Map.ofEntries(
                Map.entry(1, new GoToPosition(elevator, endeffector, intake, Positions.L4, false)),
                Map.entry(2, new InstantCommand())
            ),
            () -> currentMode == RobotStates.CORAL ? 1 : 2
        ));
        // Coral: Switch modes, Algae: Switch modes, Climb: Switch modes
        driverXbox.x().onTrue(new SelectCommand(
            Map.ofEntries(
                Map.entry(1, new InstantCommand(() -> setMode(RobotStates.CLIMB))),
                Map.entry(2, new InstantCommand(() -> setMode(RobotStates.CORAL)))
            ),
            () -> currentMode == RobotStates.CORAL ? 1 : 2
        ));

        // Coral: Intake with elevator correction, Algae: Intake algae, Climb: Deploy climb
        driverXbox.leftTrigger().whileTrue(new SelectCommand(
            Map.ofEntries(
                Map.entry(1, new SequentialCommandGroup(
                  new InstantCommand(() -> endeffector.setSetpoint(0)), 
                  new InstantCommand(() -> elevator.setSetpoint(0)), 
                  new ParallelCommandGroup(
                    new EndEffectorIntake(endeffector, elevator, intake, false, false), 
                    new InstantCommand(() -> elevator.setElevatorSpeed(0.1))))),

                Map.entry(2, new ParallelCommandGroup(
                  new Intake(intake, -0.5),
                  new GoToPosition(elevator, endeffector, intake, Positions.INTAKE_FLOOR_ALGAE, false))),

                Map.entry(3, new Climb(climb, -0.6))
            ),
            () -> { 
                if (currentMode == RobotStates.CORAL) return 1;
                else if (currentMode == RobotStates.ALGAE) return 2;  
                else return 3;
            }
        ));

        // Coral: Outtake held piece, Algae: Outtake algae, Climb: Rectract climb
        driverXbox.rightTrigger().whileTrue(new SelectCommand(
            Map.ofEntries(
                Map.entry(1, new SequentialCommandGroup(
                  new EndEffectorIntake(endeffector, elevator, intake, true, false))),

                Map.entry(2, new ParallelCommandGroup(
                  new Intake(intake, 0.5),
                  new GoToPosition(elevator, endeffector, intake, Positions.INTAKE_FLOOR_ALGAE, false))),

                Map.entry(3, new Climb(climb, 0.6))
            ),
            () -> { 
                if (currentMode == RobotStates.CORAL) return 1;
                else if (currentMode == RobotStates.ALGAE) return 2;  
                else return 3;
            }
        ));

        // Coral: Intake ground, Algae: Nothing, Climb: Manual
        driverXbox.leftBumper().whileTrue(new SelectCommand(
            Map.ofEntries(
                Map.entry(1, new ParallelCommandGroup(
                  new Intake(intake, 0.5), 
                  new GoToPosition(elevator, endeffector, intake, Positions.INTAKE_FLOOR, false))),

                Map.entry(2, new InstantCommand()),

                Map.entry(3, new Climb(climb, -0.3))
            ),
            () -> { 
                if (currentMode == RobotStates.CORAL) return 1;
                else if (currentMode == RobotStates.ALGAE) return 2;  
                else return 3;
            }
        ));

        // Coral: Nothing, Algae: Nothing, Climb: Manual
        driverXbox.rightBumper().whileTrue(new SelectCommand(
            Map.ofEntries(
                Map.entry(1, new InstantCommand()),

                Map.entry(2, new InstantCommand()),

                Map.entry(3, new Climb(climb, 0.3))
            ),
            () -> { 
                if (currentMode == RobotStates.CORAL) return 1;
                else if (currentMode == RobotStates.ALGAE) return 2;  
                else return 3;
            }
        ));

        // Coral: Removal of algae, Else: Nothing
        driverXbox.pov(0).whileTrue(new SelectCommand(
            Map.ofEntries(
                Map.entry(1, new GoToPosition(elevator, endeffector, intake, Positions.L3_ALGAE, false)),
                Map.entry(2, new InstantCommand())
            ),
            () -> currentMode == RobotStates.CORAL ? 1 : 2
        ));

        // Coral: Removal of algae, Else: Nothing
        driverXbox.pov(180).whileTrue(new SelectCommand(
            Map.ofEntries(
                Map.entry(1, new GoToPosition(elevator, endeffector, intake, Positions.L2, false)),
                Map.entry(2, new InstantCommand())
            ),
            () -> currentMode == RobotStates.CORAL ? 1 : 2
        ));

        // All: Drive to relevant position
        driverXbox.pov(270).whileTrue(new SelectCommand(
            Map.ofEntries(
                Map.entry(1, new DriveAndHoldPose(drivebase, () -> drivebase.getScorePoseFromString(drivebase.getWebServer().getSelectedScorePosition()), false)),

                Map.entry(2, new DriveAndHoldPose(drivebase, () -> Constants.PositionConstants.processorPositionBlue, false)),

                Map.entry(3, new DriveAndHoldPose(drivebase, () -> Constants.PositionConstants.cagePosition1Blue, false))
            ),
            () -> { 
                if (currentMode == RobotStates.CORAL) return 1;
                else if (currentMode == RobotStates.ALGAE) return 2;  
                else return 3;
            }
        ));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand()
  {
    return new GenerateAuto(drivebase, elevator, endeffector, intake);
  }

  public void setDriveMode()
  {
    configureBindings();
  }

  public void setMotorBrake(boolean brake)
  {
    drivebase.setMotorBrake(brake);
  }

  public WebServer getWebServer() {
    return drivebase.getWebServer();
  }

  public static LedSubsystem getLedSubsystem() {
    return leds;
  }

  public void setMode(RobotStates mode) {
    currentMode = mode;
  }

  public static RobotStates getCurrentMode() {
    return currentMode;
  }

  public void updateSubsystems() {
    Logger.recordOutput("Subsystems", new Pose3d[]{intake.getIntakeSimPose(), new Pose3d(), elevator.getElevatorSimPose(), 
      new Pose3d(endeffector.getPivotSimPose().getX(), endeffector.getPivotSimPose().getY(), 
      endeffector.getPivotSimPose().getZ() + elevator.getElevatorPosition(), endeffector.getPivotSimPose().getRotation())});
    /*Logger.recordOutput("Subsystems", new Pose3d[]{new Pose3d(), new Pose3d(), new Pose3d(), 
      new Pose3d()});*/
        
  }
}