package frc.robot.util.QuestTrack;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.NetworkTable;

public class GridMap {
    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 10;
    Node[][] grid = new Node[GRID_WIDTH][GRID_HEIGHT];

    public GridMap() {
        initializeGrid();
    }

    public void initializeGrid() {
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                grid[x][y] = new Node(x, y);
            }
        }
    }

    public void updateObstacles(NetworkTable table) {
        int gridX = (int) table.getEntry("ObjX").getDouble(-1);;
        int gridY = (int) table.getEntry("ObjX").getDouble(-1);;
    
        if (gridX >= 0 && gridX < GRID_WIDTH && gridY >= 0 && gridY < GRID_HEIGHT) {
            grid[gridX][gridY].isObstacle = true;
        }
    }

    public Node getNode(int x, int y) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return grid[0][0];
        }
        return grid[x][y];
    }
    

    public Node[][] getGrid() {
        return grid;
    }

    public Node convertFieldPoseToGridPose(Pose2d pose) {
        int x = (int)(pose.getX() / (17.5 / GRID_WIDTH));
        int y = (int)(pose.getY() / (8.0 / GRID_HEIGHT));
        return grid[x][y];
    }

    public Pose2d convertGridPoseToFieldPose(Node node) {
        double cellWidth = 17.5 / GRID_WIDTH;
        double cellHeight = 8.0 / GRID_HEIGHT;
        
        double fieldX = node.x * cellWidth + cellWidth / 2;
        double fieldY = node.y * cellHeight + cellHeight / 2;

        return new Pose2d(fieldX, fieldY, new Rotation2d());
    }
}
