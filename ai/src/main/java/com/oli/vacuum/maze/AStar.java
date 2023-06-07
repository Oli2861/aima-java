package main.java.com.oli.vacuum.maze;

import main.java.com.oli.utility.Logger;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


class AStar {
    private static final Logger logger = new Logger(AStar.class.getName());
    private FieldState[][] map;
    private Coordinate start;
    private Coordinate goal;
    private Heuristic heuristic;

    public AStar(FieldState[][] map, Coordinate start, Coordinate goal, Heuristic heuristic) {
        this.map = map;
        this.start = start;
        this.goal = goal;
        this.heuristic = heuristic;
    }

    public List<Coordinate> findPath() {
        logger.debug("Starting A* search from " + start + " to " + goal);
        // 1. Initialize open.
        PriorityQueue<AStarNode> open = new PriorityQueue<>();
        // 2. Initialize closed, add start to open.
        // Contains the nodes that have already been visited and their shortest path (via successors property).
        AStarNode closed[][] = new AStarNode[map.length][map[0].length];

        open.add(new AStarNode(start, 0, 0, null));

        // 3. While open is not empty:
        while (!open.isEmpty()) {

            // 3.1. Get the node with the lowest f from open, call it q.
            // 3.2. Pop q off open.
            AStarNode q = open.poll();
            if (q.getCoordinate().equals(goal)) return q.getPath().stream().map(AStarNode::getCoordinate).toList();
            closed[q.getCoordinate().getY()][q.getCoordinate().getX()] = q;

            // 3.3. Generate q's successors and set their parents to q.
            Set<Coordinate> neighbors = q.getNeighbors(map);
            logger.info("State: " + q.getCoordinate() + ", neighbors: " + neighbors + ", g: " + q.getgCost() + ", h: " + q.gethCost() + ", f: " + q.getfCost());

            // 3.4. For each neighbor:
            for (Coordinate neighbor : neighbors) {
                // 3.4.2. Compute g, h and f costs:
                // g cost is the movement cost from start to this neighbor.
                // h cost is the heuristic cost from this neighbor to goal.
                // f cost is the sum of g cost and h cost (calculated in constructor of AStarNode.
                AStarNode successorNode = new AStarNode(neighbor, q.getgCost() + 1, heuristic.getDistance(goal, neighbor), q);

                // 3.4.1. If neighbor is the goal, stop the search.
                if (neighbor.equals(goal)) {
                    List<Coordinate> path = successorNode.getPath().stream().map(AStarNode::getCoordinate).toList();
                    logger.debug("Path: " + path);
                    return path;
                }

                // 3.4.3. If a node with the same position as neighbor is in the OPEN lisT
                // which has a lower f than neighbor, skip this neighbor.
                if (open.stream().anyMatch(node -> node.getCoordinate().equals(successorNode.getCoordinate()))) {
                    continue;
                }

                // 3.4.4. If a node with the same position as neighbor is in the CLOSED list
                // which has a lower f than neighbor, skip this neighbor.
                // Otherwise, add the node to the open list.
                AStarNode other = closed[successorNode.getCoordinate().getY()][successorNode.getCoordinate().getX()];
                if (other != null && other.getfCost() < successorNode.getfCost())
                    continue;
                else
                    open.add(successorNode);
            }

            // 3.5. Push q on the closed list.
            closed[q.getCoordinate().getY()][q.getCoordinate().getX()] = q;

        }
        return null;
    }

}