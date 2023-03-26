package main.java.com.oli.vacuum;

import aima.core.agent.Action;
import aima.core.agent.Model;
import aima.core.agent.impl.DynamicState;
import aima.core.agent.impl.SimpleAgent;
import aima.core.agent.impl.aprog.ModelBasedReflexAgentProgram;
import aima.core.agent.impl.aprog.simplerule.ANDCondition;
import aima.core.agent.impl.aprog.simplerule.EQUALCondition;
import aima.core.agent.impl.aprog.simplerule.NOTCondition;
import aima.core.agent.impl.aprog.simplerule.Rule;
import aima.core.environment.vacuum.VacuumPercept;

import java.util.LinkedHashSet;
import java.util.Set;

import static aima.core.environment.vacuum.VacuumEnvironment.*;
import static aima.core.environment.vacuum.VacuumEnvironment.LocationState.Dirty;
import static main.java.com.oli.vacuum.LargeVacuumEnv.*;

public class ModelBasedReflexAgentForLargeVaccumEnv extends SimpleAgent<VacuumPercept, Action> {
    /*
    Agent is allowed to know:
    - Local cleanliness status
    - Whether he can move to right or left

     Agent is not allowed to know:
     - Total number of squares
     - Where it starts
     */

    private static final String CURR_LOCATION = "current location";
    private static final String CURR_CLEAN_STATE = "current clean state";
    private static final String VISITED_TO_LEFT = "visited to left";
    private static final String VISITED_TO_RIGHT = "visited to right";
    private static final String REACHED_RIGHT_BOUND = "reached right bound";
    private static final String REACHED_LEFT_BOUND = "reached left bound";

    public ModelBasedReflexAgentForLargeVaccumEnv() {
        super(new ModelBasedReflexAgentProgram<VacuumPercept, Action>() {

            @Override
            protected void init() {
                super.setState(new DynamicState());
                super.setRules(getRuleSet());
            }

            @Override
            protected DynamicState updateState(DynamicState state, Action action, VacuumPercept percept, Model model) {

                updateLocationAndMovement(state, percept);
                updateBoundsInformation(state, percept);

                state.setAttribute(CURR_CLEAN_STATE, percept.getCurrState());
                return state;
            }

            private void updateLocationAndMovement(DynamicState state, VacuumPercept percept){
                String lastLocation = (String) state.getAttribute(CURR_LOCATION);
                String currentLocation = percept.getCurrLocation();
                Integer movedRight = (Integer) state.getAttribute(VISITED_TO_RIGHT);
                Integer movedLeft = (Integer) state.getAttribute(VISITED_TO_LEFT);
                if (lastLocation != null) {
                    state.setAttribute(CURR_LOCATION, currentLocation);
                    int lexComparisonResult = lastLocation.compareTo(currentLocation);

                    if (lexComparisonResult > 0) {
                        // Moved right
                        if (movedRight == null) state.setAttribute(VISITED_TO_RIGHT, 1);
                        else state.setAttribute(VISITED_TO_RIGHT, ++movedRight);
                    } else if (lexComparisonResult < 0) {
                        // Moved left
                        if (movedLeft == null) state.setAttribute(VISITED_TO_LEFT, 1);
                        else state.setAttribute(VISITED_TO_LEFT, ++movedLeft);
                    }
                }
            }

            private void updateBoundsInformation(DynamicState state, VacuumPercept percept){
                boolean canMoveRight = (boolean) percept.getAttribute(CAN_MOVE_RIGHT);
                state.setAttribute(CAN_MOVE_RIGHT, canMoveRight);
                if (!canMoveRight) state.setAttribute(REACHED_RIGHT_BOUND, true);
                state.setAttribute(CAN_MOVE_LEFT, percept.getAttribute(CAN_MOVE_LEFT));

                boolean canMoveLeft = (boolean) percept.getAttribute(CAN_MOVE_LEFT);
                state.setAttribute(CAN_MOVE_LEFT, canMoveLeft);
                if (!canMoveLeft) state.setAttribute(REACHED_LEFT_BOUND, true);
            }

        });
    }

    private static Set<Rule<Action>> getRuleSet() {
        Set<Rule<Action>> rules = new LinkedHashSet<>();

        rules.add(new Rule<>(new EQUALCondition(CURR_LOCATION, Dirty), ACTION_SUCK));

        EQUALCondition reachedRightBoundCondition = new EQUALCondition(REACHED_RIGHT_BOUND, true);
        EQUALCondition reachedLeftBoundCondition = new EQUALCondition(REACHED_LEFT_BOUND, true);
        // Didn't reach left bound but right bound -> move left
        rules.add(new Rule<>(new ANDCondition(new NOTCondition(reachedLeftBoundCondition), reachedRightBoundCondition), ACTION_MOVE_LEFT));
        // Didn't reach right bound but left bound -> move right
        rules.add(new Rule<>(new ANDCondition(new NOTCondition(reachedRightBoundCondition), reachedLeftBoundCondition), ACTION_MOVE_RIGHT));

        // Didn't reach any bounds -> move left
        rules.add(new Rule<>(new NOTCondition(new ANDCondition(reachedLeftBoundCondition, reachedRightBoundCondition)), ACTION_MOVE_LEFT));
        return rules;
    }

}

