package main.java.com.oli.vacuum.maze;

import aima.core.agent.Action;
import aima.core.agent.Model;
import aima.core.agent.impl.DynamicAction;
import aima.core.agent.impl.DynamicState;
import aima.core.agent.impl.SimpleAgent;
import aima.core.environment.vacuum.VacuumPercept;
import main.java.com.oli.utility.Logger;

import static aima.core.environment.vacuum.MazeVacuumEnvironment.*;
import static main.java.com.oli.utility.UtilityFunctions.decodeString;

public class Vacuum2dAgent extends SimpleAgent<VacuumPercept, Action> {
    private static final Logger logger = new Logger(Vacuum2dAgent.class.getName());
    private static final String LAST_ACTION = "LAST_ACTION";
    public static final String MAP = "MAP";
    public static final String CURR_LOCATION = "CURRENT_LOCATION";
    public static final String CURR_CLEAN_STATE = "CURRENT_CLEAN_STATE";
    private static final int INITIAL_DIMENSION = 4;
    private static int extensionSize = INITIAL_DIMENSION;

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

                if (map == null || currLocation == null) {
                    state.setAttribute(MAP, new FieldState[INITIAL_DIMENSION][INITIAL_DIMENSION]);
                    map = (FieldState[][]) state.getAttribute(MAP);
                    currLocation = new Coordinate(INITIAL_DIMENSION / 2, INITIAL_DIMENSION / 2);
                }

                map[currLocation.getY()][currLocation.getX()] = FieldState.Visited;
                checkDirection(percept, map, ATT_CAN_MOVE_LEFT, new Coordinate(currLocation.getX() - 1, currLocation.getY()));
                checkDirection(percept, map, ATT_CAN_MOVE_RIGHT, new Coordinate(currLocation.getX() + 1, currLocation.getY()));
                checkDirection(percept, map, ATT_CAN_MOVE_UP, new Coordinate(currLocation.getX(), currLocation.getY() - 1));
                checkDirection(percept, map, ATT_CAN_MOVE_DOWN, new Coordinate(currLocation.getX(), currLocation.getY() + 1));

                if (currLocation.getX() == 1 || currLocation.getX() == map[0].length - 2 || currLocation.getY() == 1 || currLocation.getY() == map.length - 2) {
                    map = extendMap(map, extensionSize);
                    currLocation = new Coordinate(currLocation.getX() + extensionSize, currLocation.getY() + extensionSize);
                    extensionSize += extensionSize;
                }

                state.setAttribute(CURR_CLEAN_STATE, percept.getCurrState());
                state.setAttribute(LAST_ACTION, action);
                state.setAttribute(MAP, map);
                state.setAttribute(CURR_LOCATION, currLocation);
                return state;
            }

            /**
             * Extends the map by n-fields in each direction.
             * @param map The map to extend.
             * @param n The number of fields to extend the map in each direction.
             * @return The extended map.
             */
            private FieldState[][] extendMap(FieldState[][] map, int n) {
                FieldState[][] extendedMap = new FieldState[map.length + 2 * n][map[0].length + 2 * n];
                for (int y = n; y < extendedMap.length - n; y++) {
                    // System.arraycopy(map[y], 0, extendedMap[y], n, map[0].length);
                    for (int x = n; x < extendedMap[0].length - n; x++) {
                        extendedMap[y][x] = map[y - n][x - n];
                    }
                }
                return extendedMap;
            }

            /**
             * Checks if the field in the given direction is blocked or not.
             * @param percept The percept used to check the direction.
             * @param map The map to update.
             * @param key The key of the direction to check.
             * @param location The location of the field to check.
             */
            private void checkDirection(VacuumPercept percept, FieldState[][] map, String key, Coordinate location) {
                boolean movable = decodeString((String) percept.getAttribute(key));
                if (movable) {
                    if (map[location.getY()][location.getX()] == null)
                        map[location.getY()][location.getX()] = FieldState.Unblocked;
                } else {
                    map[location.getY()][location.getX()] = FieldState.Blocked;
                }
            }

            /**
             * Updates the current location based on the last action.
             * @param lastAction The last action.
             * @param currLocation The current (outdated) location.
             * @return The updated location.
             */
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
