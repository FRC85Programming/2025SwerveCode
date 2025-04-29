package frc.robot.subsystems.swervedrive;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.util.QuestNav.QuestNav;

public class QuestOdometry extends SubsystemBase {

    QuestNav questNav = new QuestNav();

    // Quest position relative to robot
    Transform2d QUEST_TO_ROBOT = new Transform2d(0, 0, new Rotation2d(0));

    // Position of quest (no robot accounted for)
    Pose2d questPose = new Pose2d(); 

    // Pose of the robot switched
    Pose2d robotPose = new Pose2d();


    @Override
    public void periodic() {
        // Housekeeping
        questNav.cleanupResponses();
        questNav.processHeartbeat();
  
        // Set and interpret the quest pose
        questPose = questNav.getPose();
        robotPose = questPose.transformBy(QUEST_TO_ROBOT.inverse());
    }

    /**Resets the quest to a robot relative position (accounts for quest-to-robot offset)*/
    public void resetPose(Pose2d resetPose) {
        // Send the reset operation 
        questNav.setPose(resetPose.transformBy(QUEST_TO_ROBOT));
    }

    public Pose2d getRobotPose() {
        return robotPose;
    }
}
