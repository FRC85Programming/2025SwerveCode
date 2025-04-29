package frc.robot.util.QuestTrack;

public class Node {
    int x;
    int y;
    double cost;
    boolean isObstacle;
    Node parent;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.cost = Double.MAX_VALUE;
        this.isObstacle = false;
        this.parent = null;
    }
}
