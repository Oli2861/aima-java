package main.java.com.oli.vacuum.maze;

@FunctionalInterface
interface Heuristic {
    int getDistance(Coordinate a, Coordinate b);
}

abstract class Heuristics {
    public static Heuristic manhattanDistance = (a, b) -> Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    public static Heuristic euclideanDistance = (a, b) -> (int) Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2));
    public static Heuristic chebyshevDistance = (a, b) -> Math.max(Math.abs(a.getX() - b.getX()), Math.abs(a.getY() - b.getY()));
}