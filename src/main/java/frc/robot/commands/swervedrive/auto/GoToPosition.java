package frc.robot.commands.swervedrive.auto;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.endeffector.EndEffectorSubsystem;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.util.Positions;


/**
 * Auto Balance command using a simple PID controller. Created by Team 3512
 * <a href="https://github.com/frc3512/Robot-2023/blob/main/src/main/java/frc3512/robot/commands/AutoBalance.java">...</a>
 */
public class GoToPosition extends Command
{

  private final ElevatorSubsystem elevator;
  private final EndEffectorSubsystem endeffector;
  private final IntakeSubsystem intake;
  private Positions position;


  public GoToPosition(ElevatorSubsystem elevator, EndEffectorSubsystem endeffector, IntakeSubsystem intake, Positions position)
  {
    this.elevator = elevator;
    this.endeffector = endeffector;
    this.intake = intake;
    this.position = position;

    addRequirements(elevator, endeffector, intake);
  }

  @Override
  public void execute()
  {
    /*if (elevator.isInTolerance() || position.equals(Positions.INTAKE_FLOOR) || position.equals(Positions.INTAKE_STATION)) {
      endeffector.setSetpoint(endeffector.getSetpoint(position));
    }*/
    endeffector.setSetpoint(endeffector.getSetpoint(position));
    elevator.setSetpoint(elevator.getSetpoint(position));
    //intake.setSetpoint(intake.getSetpoint(position));
    
  }

  @Override
  public boolean isFinished()
  {
    return false;
  }

  @Override
  public void end(boolean interrupted)
  {
    elevator.setSetpoint(elevator.getSetpoint(Positions.HOME));
    //intake.setSetpoint(intake.getSetpoint(Positions.HOME));
    endeffector.setSetpoint(endeffector.getSetpoint(Positions.HOME));
  }
}

