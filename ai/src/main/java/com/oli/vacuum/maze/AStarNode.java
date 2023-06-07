package main.java.com.oli.vacuum.maze;

import java.util.*;

class AStarNode implements Comparable<AStarNode> {
    /**
     * Coordinate of this node.
     */
    private Coordinate coordinate;
    /**
     * Movement cost from start to this node.
     */
    private int gCost;
    /**
     * Heuristic cost from this node to goal.
     */
    private int hCost;
    /**
     * Sum of gCost and hCost.
     */
    private int fCost;
    /**
     * Parent node.
     */
    private AStarNode parent;

    AStarNode(Coordinate coordinate, int gCost, int hCost, AStarNode parent) {
        this.coordinate = coordinate;
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
        this.parent = parent;
    }

    /**
     * Get path from start to this node by tracing back to parent.
     *
     * @return List of nodes from start to this node.
     */
    List<AStarNode> getPath() {
        List<AStarNode> path = new ArrayList<>();
        AStarNode current = this;
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        Collections.reverse(path);
        return path.subList(1, path.size());
    }

    Set<Coordinate> getNeighbors(FieldState[][] map) {
        Set<Coordinate> successors = new HashSet<>();
        if (coordinate.getY() > 0 && map[coordinate.getY() - 1][coordinate.getX()] != FieldState.Blocked) {
            successors.add(new Coordinate(coordinate.getX(), coordinate.getY() - 1));
        }
        if (coordinate.getY() + 1 < map.length && map[coordinate.getY() + 1][coordinate.getX()] != FieldState.Blocked) {
            successors.add(new Coordinate(coordinate.getX(), coordinate.getY() + 1));
        }
        if (coordinate.getX() > 0 && map[coordinate.getY()][coordinate.getX() - 1] != FieldState.Blocked) {
            successors.add(new Coordinate(coordinate.getX() - 1, coordinate.getY()));
        }
        if (coordinate.getX() + 1 < map[coordinate.getY()].length && map[coordinate.getY()][coordinate.getX() + 1] != FieldState.Blocked) {
            successors.add(new Coordinate(coordinate.getX() + 1, coordinate.getY()));
        }
        return successors;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public int getgCost() {
        return gCost;
    }

    public int gethCost() {
        return hCost;
    }

    public int getfCost() {
        return fCost;
    }

    public AStarNode getParent() {
        return parent;
    }

    @Override
    public int compareTo(AStarNode o) {
        return Integer.compare(this.fCost, o.fCost);
    }
}