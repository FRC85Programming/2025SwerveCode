package frc.robot.util;

import java.util.Map;

public class FieldData {
    public static Map<String, Positions> algaeMap = Map.ofEntries(
    Map.entry("A", Positions.L3_ALGAE), Map.entry("B", Positions.L3_ALGAE), Map.entry("C", Positions.L2_ALGAE), Map.entry("D", Positions.L2_ALGAE),
    Map.entry("E", Positions.L3_ALGAE), Map.entry("F", Positions.L3_ALGAE), Map.entry("G", Positions.L2_ALGAE), Map.entry("H", Positions.L2_ALGAE),
    Map.entry("I", Positions.L3_ALGAE), Map.entry("J", Positions.L3_ALGAE), Map.entry("K", Positions.L2_ALGAE), Map.entry("L", Positions.L2_ALGAE));

    public static Map<String, ReefPositions> positionMap = Map.ofEntries(
    Map.entry("A", ReefPositions.Left), Map.entry("B", ReefPositions.Right), Map.entry("C", ReefPositions.Left), Map.entry("D", ReefPositions.Right),
    Map.entry("E", ReefPositions.Left), Map.entry("F", ReefPositions.Right), Map.entry("G", ReefPositions.Left), Map.entry("H", ReefPositions.Right),
    Map.entry("I", ReefPositions.Left), Map.entry("J", ReefPositions.Right), Map.entry("K", ReefPositions.Left), Map.entry("L", ReefPositions.Right));

    public static Map<String, Integer> apriltagIdMapBlue = Map.ofEntries(
    Map.entry("A", 18), Map.entry("B", 18), Map.entry("C", 17), Map.entry("D", 17),
    Map.entry("E", 22), Map.entry("F", 22), Map.entry("G", 21), Map.entry("H", 21),
    Map.entry("I", 20), Map.entry("J", 20), Map.entry("K", 19), Map.entry("L", 19));

    public static Map<String, Integer> apriltagIdMapRed = Map.ofEntries(
    Map.entry("A", 7), Map.entry("B", 7), Map.entry("C", 8), Map.entry("D", 8),
    Map.entry("E", 9), Map.entry("F", 9), Map.entry("G", 10), Map.entry("H", 10),
    Map.entry("I", 11), Map.entry("J", 11), Map.entry("K", 6), Map.entry("L", 6));
}
