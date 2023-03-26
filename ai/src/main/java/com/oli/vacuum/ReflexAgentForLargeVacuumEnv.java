package main.java.com.oli.vacuum;

import aima.core.agent.Action;
import aima.core.agent.impl.DynamicState;
import aima.core.agent.impl.SimpleAgent;
import aima.core.agent.impl.aprog.SimpleReflexAgentProgram;
import aima.core.agent.impl.aprog.simplerule.ANDCondition;
import aima.core.agent.impl.aprog.simplerule.EQUALCondition;
import aima.core.agent.impl.aprog.simplerule.Rule;
import aima.core.environment.vacuum.VacuumPercept;
import static aima.core.environment.vacuum.VacuumEnvironment.*;
import static aima.core.environment.vacuum.VacuumEnvironment.LocationState.Dirty;
import static main.java.com.oli.vacuum.LargeVacuumEnv.CAN_MOVE_LEFT;
import static main.java.com.oli.vacuum.LargeVacuumEnv.CAN_MOVE_RIGHT;
import java.util.LinkedHashSet;
import java.util.Set;


public class ReflexAgentForLargeVacuumEnv extends SimpleAgent<VacuumPercept, Action> {
    /*
    Agent is allowed to know:
    - Local cleanliness status
    - Whether he can move to right or left

     Agent is not allowed to know:
     - Total number of squares
     - Where it starts
     */

    static final String CURR_LOCATION = "current location";
    static final String CURR_CLEAN_STATE = "current clean state";

    public ReflexAgentForLargeVacuumEnv() {
        super(new SimpleReflexAgentProgram<VacuumPercept, Action>(getRuleSet()) {
            @Override
            protected DynamicState interpretInput(VacuumPercept vacuumPercept) {
                DynamicState state = new DynamicState();
                state.setAttribute(CURR_LOCATION, vacuumPercept.getCurrLocation());
                state.setAttribute(CURR_CLEAN_STATE, vacuumPercept.getCurrState());
                state.setAttribute(CAN_MOVE_RIGHT, vacuumPercept.getAttribute(CAN_MOVE_RIGHT));
                state.setAttribute(CAN_MOVE_LEFT, vacuumPercept.getAttribute(CAN_MOVE_LEFT));
                return state;
            }
        });
    }

    private static Set<Rule<Action>> getRuleSet() {
        Set<Rule<Action>> rules = new LinkedHashSet<>();

        rules.add(new Rule<>(new EQUALCondition(CURR_LOCATION, Dirty), ACTION_SUCK));
        rules.add(new Rule<>(new EQUALCondition(CAN_MOVE_LEFT, false), ACTION_MOVE_RIGHT));
        rules.add(new Rule<>(new EQUALCondition(CAN_MOVE_RIGHT, false), ACTION_MOVE_LEFT));
        // Moves random if it can move in both directions
        ANDCondition trueCond = new ANDCondition(
                new EQUALCondition(CAN_MOVE_LEFT, true),
                new EQUALCondition(CAN_MOVE_RIGHT, true)
        );
        if (Math.random() > 0.5) {
            rules.add(new Rule<>(trueCond, ACTION_MOVE_LEFT));
        } else {
            rules.add(new Rule<>(trueCond, ACTION_MOVE_RIGHT));
        }
        return rules;
    }


}
