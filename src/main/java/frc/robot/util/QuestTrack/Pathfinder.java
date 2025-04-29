package frc.robot.util.QuestTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Pathfinder {
    PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.cost));
    private GridMap gridMap;

    public Pathfinder(GridMap gridMap) {
        this.gridMap = gridMap;
    }

    public List<Node> findPath(Node start, Node goal) {
        openSet.clear();
        Set<Node> closedSet = new HashSet<>();
        start.cost = 0;
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current == goal) return reconstructPath(goal);

            closedSet.add(current);
            for (Node neighbor : getNeighbors(current)) {
                if (neighbor.isObstacle || closedSet.contains(neighbor)) continue;

                double newCost = current.cost + 1;
                if (newCost < neighbor.cost) {
                    neighbor.cost = newCost;
                    neighbor.parent = current;
                    openSet.add(neighbor);
                }
            }
        }
        return new ArrayList<>();
    }

    List<Node> reconstructPath(Node goal) {
        List<Node> path = new ArrayList<>();
        for (Node node = goal; node != null; node = node.parent) {
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }

    void replanPath(Node start, Node goal) {
        // If an obstacle moved, clear only affected nodes
        for (Node[] row : gridMap.getGrid()) {
            for (Node node : row) {
                if (!node.isObstacle) node.cost = Double.MAX_VALUE;
            }
        }
        findPath(start, goal);
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}}; // 4-way movement

        for (int[] dir : directions) {
            Node neighbor = gridMap.getNode(node.x + dir[0], node.y + dir[1]);
            if (neighbor != null) neighbors.add(neighbor);
        }
        return neighbors;
    }
}
