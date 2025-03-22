package frc.robot.commands.actions.scoring;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.elevator.ElevatorSubsystem;
import frc.robot.subsystems.endeffector.EndEffectorSubsystem;
import frc.robot.subsystems.intake.IntakeSubsystem;
import frc.robot.util.Positions;

public class GoToPosition extends Command
{

  ElevatorSubsystem elevator;
  EndEffectorSubsystem endeffector;
  IntakeSubsystem intake;
  Positions position;
  Boolean endable;


  public GoToPosition(ElevatorSubsystem elevator, EndEffectorSubsystem endeffector, IntakeSubsystem intake, Positions position, boolean endable)
  {
    this.elevator = elevator;
    this.endeffector = endeffector;
    this.intake = intake;
    this.position = position;
    this.endable = endable;

    addRequirements(elevator, endeffector, intake);
  }

  @Override
  public void execute()
  {
    endeffector.setSetpoint(endeffector.getSetpoint(position));
    elevator.setSetpoint(elevator.getSetpoint(position));
    intake.setSetpoint(intake.getSetpoint(position));
    
  }

  @Override
  public boolean isFinished()
  {
    if ((position == Positions.L1 || position == Positions.ALGAE_SCORE) && intake.atTolerance() ) {
      return true;
    } else if (endable && elevator.atSetpoint() && endeffector.atSetpoint()){
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void end(boolean interrupted)
  {
    if ((endable || position == Positions.INTAKE_FLOOR) && position != Positions.INTAKE_FLOOR_ALGAE && position != Positions.ALGAE_SCORE) {
      elevator.setSetpoint(elevator.getSetpoint(Positions.HOME));
      intake.setSetpoint(intake.getSetpoint(Positions.HOME));
      endeffector.setSetpoint(endeffector.getSetpoint(Positions.HOME));
    }
  
    SmartDashboard.putBoolean("Ended GoToPosition", true);
  }
}

