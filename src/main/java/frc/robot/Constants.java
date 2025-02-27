// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import swervelib.math.Matter;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean constants. This
 * class should not be used for any other purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants
{
  public static final double ROBOT_MASS = 40 * 0.453592; // 32lbs * kg per pound
  public static final Matter CHASSIS    = new Matter(new Translation3d(0, 0, Units.inchesToMeters(8)), ROBOT_MASS);
  public static final double LOOP_TIME  = 0.13; //s, 20ms + 110ms sprk max velocity lag
  public static final double MAX_SPEED  = Units.feetToMeters(18.0);
  public static final double MAX_VOLTAGE = 12;
  // Maximum speed of the robot in meters per second, used to limit acceleration.

//  public static final class AutonConstants
//  {
//
//    public static final PIDConstants TRANSLATION_PID = new PIDConstants(0.7, 0, 0);
//    public static final PIDConstants ANGLE_PID       = new PIDConstants(0.4, 0, 0.01);
//  }
  public static final class IntakeConstants 
  {
    // Pysical arm properties
    public static final double ARM_LENGTH_METERS = 0.75;
    public static final double ARM_MASS_KG = 1.3;
    public static final double GEAR_RATIO = 9.0;

    // Angle constraints (sim)
    public static final double MIN_ANGLE_RAD = Math.toRadians(-180);
    public static final double MAX_ANGLE_RAD = Math.toRadians(180);

    // Motor IDs
    public static final int INTAKE_HOME_LIMIT_ID = -1;
    public static final int INTAKE_ARM_MOTOR_ID = -1;
    public static final int INTAKE_ROLLER_MOTOR_ID = -1;

    // Origin of pivot point
    public static final double INTAKE_ROOT_X = 0.32;
    public static final double INTAKE_ROOT_Z = 0.292;

    // Positions
    public static final double L1_INTAKE_POSITION = Math.PI/6;
    public static final double L2_INTAKE_POSITION = 0.0;
    public static final double L3_INTAKE_POSITION = 0.0;
    public static final double L4_INTAKE_POSITION = 0.0;
    public static final double HOME_INTAKE_POSITION = 0.0;
    public static final double INTAKE_FLOOR_INTAKE_POSITION = (3*Math.PI)/4;
    public static final double INTAKE_STATION_INTAKE_POSITION = 0.0;
  }

  public static final class ElevatorConstants {
    // Root positions (sim)
    public static final double ELEVATOR_ROOT_X = -0.153;
    public static final double ELEVATOR_ROOT_Y = -0.178;
    public static final double ELEVATOR_ROOT_Z = 0.09;

    // Physical properties
    public static final double GEAR_RATIO = 5.0;
    public static final double DRUM_RADIUS = Units.inchesToMeters(1.0);
    public static final double CARRIAGE_MASS = 0.01; // kg

    // Height constraints
    public static final double MIN_HEIGHT = 0;
    public static final double MAX_HEIGHT = 0.8;

    // Positions
    public static final double L1_ELEVATOR_POSITION = 0.0;
    public static final double L2_ELEVATOR_POSITION = 0.0001;
    public static final double L3_ELEVATOR_POSITION = 0.3;
    public static final double L4_ELEVATOR_POSITION = 0.79;
    public static final double L2_ALGAE_ELEVATOR_POSITION = .41;
    public static final double HOME_ELEVATOR_POSITION = 0.0;
    public static final double INTAKE_FLOOR_ELEVATOR_POSITION = 0.0;
    public static final double INTAKE_STATION_ELEVATOR_POSITION = 0.0;

    public static final int ELEVATOR_LOW_LIMIT = 0;
    public static final int ELEVATOR_HIGH_LIMIT = 1;

    public static final int ELEVATOR_ENCODER_1 = 3;
    public static final int ELEVATOR_ENCODER_2 = 4;
  }

  public static final class EndEffectorConstants 
  {
    // Pysical pivot properties
    public static final double ARM_LENGTH_METERS = 0.35;
    public static final double ARM_MASS_KG = Units.lbsToKilograms(4.172);
    public static final double GEAR_RATIO = 9.0;

    // Angle constraints (sim)
    public static final double MIN_ANGLE_RAD = Math.toRadians(-180);
    public static final double MAX_ANGLE_RAD = Math.toRadians(180);

    // Motor IDs
    public static final int PIVOT_MOTOR_ID = -1;
    public static final int ROLLER_MOTOR_ID = -1;

    // Origin of pivot point
    public static final double PIVOT_ROOT_X = -0.153;
    public static final double PIVOT_ROOT_Y = -0.303;
    public static final double PIVOT_ROOT_Z = 0.98;

    public static final double L1_PIVOT_POSITION = 0;
    public static final double L2_PIVOT_POSITION = 4.2;
    public static final double L3_PIVOT_POSITION = 3.9; 
    public static final double L4_PIVOT_POSITION = 3.60;
    public static final double L2_ALGAE_PIVOT_POSITION = 4.6;
    public static final double HOME_PIVOT_POSITION = 0;
    public static final double INTAKE_FLOOR_PIVOT_POSITION = 0;
    public static final double INTAKE_STATION_PIVOT_POSITION = 0;

    public static final int CORAL_LIMIT_SWITCH = 2;
    public static final int PIVOT_ENCODER = 5;

    public static final double CENTER_OF_MASS = Units.inchesToMeters(12.9);
  }

  public static final class DrivebaseConstants
  {

    // Hold time on motor brakes when disabled
    public static final double WHEEL_LOCK_TIME = 10; // seconds
  }

  public static class OperatorConstants
  {

    // Joystick Deadband
    public static final double DEADBAND        = 0.1;
    public static final double LEFT_Y_DEADBAND = 0.1;
    public static final double RIGHT_X_DEADBAND = 0.1;
    public static final double TURN_CONSTANT    = 6;
  }

  public static class PositionConstants
  {
    // Top right side of the reef is position 1
    // Positions are labled clockwise from position 1

    // Reef positions
    public static final Pose2d reefPositionA = new Pose2d(3.075, 4.2, new Rotation2d(Math.toRadians(0)));
    public static final Pose2d reefPositionB = new Pose2d(3.075, 3.85, new Rotation2d(Math.toRadians(0)));
    public static final Pose2d reefPositionC = new Pose2d(3.56, 2.87, new Rotation2d(Math.toRadians(60)));
    public static final Pose2d reefPositionD = new Pose2d(3.89, 2.67, new Rotation2d(Math.toRadians(60)));
    public static final Pose2d reefPositionE = new Pose2d(5.00, 2.72, new Rotation2d(Math.toRadians(120)));
    public static final Pose2d reefPositionF = new Pose2d(5.300, 2.86, new Rotation2d(Math.toRadians(120)));
    public static final Pose2d reefPositionG = new Pose2d(5.9, 3.855, new Rotation2d(Math.toRadians(180)));
    public static final Pose2d reefPositionH = new Pose2d(5.9, 4.2, new Rotation2d(Math.toRadians(180)));
    public static final Pose2d reefPositionI = new Pose2d(5.300, 5.1, new Rotation2d(Math.toRadians(-120)));
    public static final Pose2d reefPositionJ = new Pose2d(5.0, 5.26, new Rotation2d(Math.toRadians(-120)));
    public static final Pose2d reefPositionK = new Pose2d(3.97, 5.26, new Rotation2d(Math.toRadians(-60)));
    public static final Pose2d reefPositionL = new Pose2d(3.68, 5.1, new Rotation2d(Math.toRadians(-60)));

    public static final Pose2d pathPlanningTestPose = new Pose2d(1.8, 4, new Rotation2d(Math.toRadians(0)));




    // Source positions
    public static final Pose2d sourcePositionLeft = new Pose2d(.9, 6.8, new Rotation2d(Math.toRadians(-50)));
    public static final Pose2d sourcePositionRight = new Pose2d(1.625, 0.825, new Rotation2d(Math.toRadians(50)));


  }
}
