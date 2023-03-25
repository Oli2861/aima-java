package main.java.com.oli.vacuum;

import aima.core.agent.Action;
import aima.core.agent.impl.DynamicState;
import aima.core.agent.impl.SimpleAgent;
import aima.core.agent.impl.aprog.SimpleReflexAgentProgram;
import aima.core.agent.impl.aprog.simplerule.Condition;
import aima.core.agent.impl.aprog.simplerule.EQUALCondition;
import aima.core.agent.impl.aprog.simplerule.ORCondition;
import aima.core.agent.impl.aprog.simplerule.Rule;
import aima.core.environment.vacuum.VacuumEnvironment;
import aima.core.environment.vacuum.VacuumPercept;

import java.util.LinkedHashSet;
import java.util.Set;

import static main.java.com.oli.vacuum.LargeVacuumEnv.CAN_MOVE_RIGHT;

public class ReflexAgentForLargeVacuumEnv extends SimpleAgent<VacuumPercept, Action> {
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

    public ReflexAgentForLargeVacuumEnv() {
        super(new SimpleReflexAgentProgram<VacuumPercept, Action>(getRuleSet()) {
            @Override
            protected DynamicState interpretInput(VacuumPercept vacuumPercept) {
                DynamicState state = new DynamicState();
                state.setAttribute(CURR_LOCATION, vacuumPercept.getCurrLocation());
                state.setAttribute(CURR_CLEAN_STATE, vacuumPercept.getCurrState());
                state.setAttribute(LargeVacuumEnv.CAN_MOVE_RIGHT, vacuumPercept.getAttribute(LargeVacuumEnv.CAN_MOVE_RIGHT));
                state.setAttribute(LargeVacuumEnv.CAN_MOVE_LEFT, vacuumPercept.getAttribute(LargeVacuumEnv.CAN_MOVE_LEFT));
                return state;
            }
        });
    }

    private static Set<Rule<Action>> getRuleSet() {
        Set<Rule<Action>> rules = new LinkedHashSet<>();

        rules.add(new Rule<>(new EQUALCondition(CURR_LOCATION, VacuumEnvironment.LocationState.Dirty), VacuumEnvironment.ACTION_SUCK));
        rules.add(new Rule<>(new EQUALCondition(LargeVacuumEnv.CAN_MOVE_LEFT, false), VacuumEnvironment.ACTION_MOVE_RIGHT));
        rules.add(new Rule<>(new EQUALCondition(LargeVacuumEnv.CAN_MOVE_RIGHT, false), VacuumEnvironment.ACTION_MOVE_LEFT));
        // Moves random
        ORCondition trueCond = new ORCondition(
                new EQUALCondition(LargeVacuumEnv.CAN_MOVE_LEFT, true),
                new EQUALCondition(CAN_MOVE_RIGHT, true)
        );
        if (Math.random() > 0.5) {
            rules.add(new Rule<>(trueCond, VacuumEnvironment.ACTION_MOVE_LEFT));
        } else {
            rules.add(new Rule<>(trueCond, VacuumEnvironment.ACTION_MOVE_RIGHT));
        }
        return rules;
    }


}
