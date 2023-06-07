package main.java.com.oli.vacuum.maze;

import aima.core.agent.AgentProgram;
import aima.core.agent.Model;
import aima.core.agent.impl.DynamicState;
import aima.core.environment.vacuum.MazeVacuumEnvironment;
import aima.core.environment.vacuum.VacuumEnvironment;
import aima.core.util.datastructure.Pair;
import main.java.com.oli.utility.Logger;
import main.java.com.oli.utility.UtilityFunctions;

import java.util.*;

import static main.java.com.oli.vacuum.maze.Vacuum2dAgent.CURR_CLEAN_STATE;

public abstract class Vacuum2dAgentProgram<P, A> implements AgentProgram<P, A> {
    private static int actionNumber = 0;
    private static final Logger logger = new Logger(Vacuum2dAgentProgram.class.getName());
    /// persistent: state, the agent's current conception of the world state
    private DynamicState state = null;
    /// model, a description of how the next state depends on current state and action
    private Model model = null;
    /// action, the most recent action, initially none
    private A action = null;

    protected Vacuum2dAgentProgram() {
        init();
    }

    public final Optional<A> apply(P percept) {
        logger.debug("Step: " + actionNumber++);
        state = updateState(state, action, percept, model);
        action = determineAction(state);
        logger.debug("Action: " + action);
        return Optional.ofNullable(action);
    }

    /**
     * Determine the next action of the agent based on the current state.
     *
     * @param s the current state of the agent.
     * @return the next action of the agent.
     */
    private A determineAction(DynamicState s) {
        if (s.getAttribute(CURR_CLEAN_STATE) == VacuumEnvironment.LocationState.Dirty)
            return (A) VacuumEnvironment.ACTION_SUCK;

        FieldState[][] map = (FieldState[][]) s.getAttribute(Vacuum2dAgent.MAP);
        Coordinate currLocation = (Coordinate) s.getAttribute(Vacuum2dAgent.CURR_LOCATION);
        UtilityFunctions.printMap(map);

        Coordinate closestLocation = determineDistancesToUnexploredStates(map, currLocation).entrySet()
                .stream()
                .min((a, b) -> {
                    int comparison = a.getValue().compareTo(b.getValue());
                    if (comparison != 0) {
                        return comparison;
                    } else {
                        Coordinate coordinateA = a.getKey();
                        Coordinate coordinateB = b.getKey();
                        int xComparison = Integer.compare(coordinateA.getX(), coordinateB.getX());
                        if (xComparison != 0) return xComparison;
                        else return Integer.compare(coordinateA.getY(), coordinateB.getY());
                    }
                }).map(Map.Entry::getKey)
                .orElse(null);

        if (closestLocation == null)
            return null;
        else
            return moveToNeighbouringField(currLocation, new AStar(map, currLocation, closestLocation, Heuristics.euclideanDistance).findPath().get(0));
    }

    /**
     * Moves the agent to the specified neighbouring field.
     *
     * @param currLocation the current location of the agent.
     * @param nextLocation the location of the neighbouring field to move to.
     * @return the action to perform to move to the neighbouring field.
     */
    private A moveToNeighbouringField(Coordinate currLocation, Coordinate nextLocation) {
        if (currLocation.getX() < nextLocation.getX()) {
            return (A) VacuumEnvironment.ACTION_MOVE_RIGHT;
        } else if (currLocation.getX() > nextLocation.getX()) {
            return (A) VacuumEnvironment.ACTION_MOVE_LEFT;
        } else if (currLocation.getY() < nextLocation.getY()) {
            return (A) MazeVacuumEnvironment.ACTION_MOVE_DOWN;
        } else if (currLocation.getY() > nextLocation.getY()) {
            return (A) MazeVacuumEnvironment.ACTION_MOVE_UP;
        } else {
            throw new IllegalStateException("The agent is already at the target location.");
        }
    }

    /**
     * Determines the distances to all unexplored states.
     *
     * @param map          the map to search for unexplored states.
     * @param currLocation the current location of the agent.
     * @return a map containing the distances to all unexplored states.
     */
    private Map<Coordinate, Integer> determineDistancesToUnexploredStates(FieldState[][] map, Coordinate currLocation) {
        Map<Coordinate, Integer> distanceMap = new HashMap<>();
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                FieldState fieldState = map[y][x];
                if (fieldState == FieldState.Unblocked) {
                    distanceMap.put(new Coordinate(x, y), Math.abs(currLocation.getX() - x) + Math.abs(currLocation.getY() - y));
                }
            }
        }
        return distanceMap;
    }

    protected abstract void init();

    protected abstract DynamicState updateState(DynamicState state, A action, P percept, Model model);

    /**
     * Set the agent's current conception of the world state.
     *
     * @param state the agent's current conception of the world state.
     */
    public void setState(DynamicState state) {
        this.state = state;
    }

    /**
     * Set the program's description of how the next state depends on the state
     * and action.
     *
     * @param model a description of how the next state depends on the current
     *              state and action.
     */
    public void setModel(Model model) {
        this.model = model;
    }

}