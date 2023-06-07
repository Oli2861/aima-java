package main.java.com.oli.vacuum.maze;

import aima.core.agent.Action;
import aima.core.agent.Model;
import aima.core.agent.impl.DynamicAction;
import aima.core.agent.impl.DynamicState;
import aima.core.agent.impl.SimpleAgent;
import aima.core.environment.vacuum.VacuumPercept;
import main.java.com.oli.utility.Logger;
import main.java.com.oli.utility.UtilityFunctions;

import static aima.core.environment.vacuum.MazeVacuumEnvironment.*;
import static main.java.com.oli.utility.UtilityFunctions.decodeString;

public class Vacuum2dAgent extends SimpleAgent<VacuumPercept, Action> {
    private static final Logger logger = new Logger(Vacuum2dAgent.class.getName());
    private static final String LAST_ACTION = "LAST_ACTION";
    public static final String MAP = "MAP";
    public static final String CURR_LOCATION = "CURRENT_LOCATION";
    public static final String CURR_CLEAN_STATE = "CURRENT_CLEAN_STATE";

    public Vacuum2dAgent() {
        super(new Vacuum2dAgentProgram<VacuumPercept, Action>() {

            @Override
            protected void init() {
                super.setState(new DynamicState());
            }

            @Override
            protected DynamicState updateState(DynamicState state, Action action, VacuumPercept percept, Model model) {
                FieldState[][] map = (FieldState[][]) state.getAttribute(MAP);
                Coordinate currLocation = (Coordinate) state.getAttribute(CURR_LOCATION);
                currLocation = updateLocation((DynamicAction) action, currLocation);
                state.setAttribute(CURR_LOCATION, currLocation);
                // If the map is not initialized, initialize it with the current location in the middle.
                if (map == null || currLocation == null) {
                    state.setAttribute(MAP, new FieldState[16][16]);
                    map = (FieldState[][]) state.getAttribute(MAP);
                    state.setAttribute(CURR_LOCATION, new Coordinate(8, 8));
                    currLocation = (Coordinate) state.getAttribute(CURR_LOCATION);
                    logger.debug("Initialized map with size " + map.length + "x" + map[0].length + " and current location " + currLocation);
                }

                state.setAttribute(CURR_CLEAN_STATE, percept.getCurrState());
                state.setAttribute(LAST_ACTION, action);

                // TODO: Extend map, if the agent reaches the bounds. Should already be done if there is only 1 field to the bounds as otherwise the fields towards the edge cannot be checked / mapped.
                map[currLocation.getY()][currLocation.getX()] = FieldState.Visited;
                logger.info("Percept: " +
                        "\tCurrent state: " + percept.getAttribute(CURR_CLEAN_STATE) + ".\n\t" +
                        "Can move up: " + percept.getAttribute(ATT_CAN_MOVE_UP) + ".\n\t" +
                        "Can move down: " + percept.getAttribute(ATT_CAN_MOVE_DOWN) + ".\n\t" +
                        "Can move left: " + percept.getAttribute(ATT_CAN_MOVE_LEFT) + ".\n\t" +
                        "Can move right: " + percept.getAttribute(ATT_CAN_MOVE_RIGHT) + ".\n\t" +
                        "Last action: " + action + ".\n\t" +
                        "Current location: " + currLocation + ".");
                checkDirection(percept, map, ATT_CAN_MOVE_LEFT, new Coordinate(currLocation.getX() - 1, currLocation.getY()));
                checkDirection(percept, map, ATT_CAN_MOVE_RIGHT, new Coordinate(currLocation.getX() + 1, currLocation.getY()));
                checkDirection(percept, map, ATT_CAN_MOVE_UP, new Coordinate(currLocation.getX(), currLocation.getY() - 1));
                checkDirection(percept, map, ATT_CAN_MOVE_DOWN, new Coordinate(currLocation.getX(), currLocation.getY() + 1));
                state.setAttribute(MAP, map);
                return state;
            }

            private void checkDirection(VacuumPercept percept, FieldState[][] map, String key, Coordinate location) {
                boolean movable = decodeString((String) percept.getAttribute(key));
                if (movable) {
                    if (map[location.getY()][location.getX()] == null){
                        map[location.getY()][location.getX()] = FieldState.Unblocked;
                    } else{
                        logger.info("Field " + location + " was already checked.");
                    }
                } else {
                    logger.info("Field " + location + " is blocked.");
                    map[location.getY()][location.getX()] = FieldState.Blocked;
                }
                logger.info("Checked direction " + key + " with location " + location + " and result " + map[location.getY()][location.getX()]);
            }

            private Coordinate updateLocation(DynamicAction lastAction, Coordinate currLocation) {
                if (lastAction == null || currLocation == null) return null;

                if (lastAction.equals(ACTION_MOVE_RIGHT)) {
                    return new Coordinate(currLocation.getX() + 1, currLocation.getY());
                } else if (lastAction.equals(ACTION_MOVE_LEFT)) {
                    return new Coordinate(currLocation.getX() - 1, currLocation.getY());
                } else if (lastAction.equals(ACTION_MOVE_DOWN)) {
                    return new Coordinate(currLocation.getX(), currLocation.getY() + 1);
                } else if (lastAction.equals(ACTION_MOVE_UP)) {
                    return new Coordinate(currLocation.getX(), currLocation.getY() - 1);
                } else if (lastAction.equals(ACTION_SUCK)) {
                    return currLocation;
                }
                throw new IllegalStateException("Unknown action: " + lastAction);
            }

        });
    }
}
