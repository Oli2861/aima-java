package main.java.com.oli.vacuum;

import aima.core.agent.Agent;
import aima.core.environment.vacuum.VacuumEnvironment;
import aima.core.environment.vacuum.VacuumPercept;
import aima.core.search.agent.NondeterministicSearchAgent;

import java.util.List;

class LargeVacuumEnv extends VacuumEnvironment {
    public static final String CAN_MOVE_RIGHT = "can move right";
    public static final String CAN_MOVE_LEFT = "can move left";

    LargeVacuumEnv(List<String> locations, LocationState... locationStates) {
        super(locations, locationStates);
    }

    @Override
    public VacuumPercept getPerceptSeenBy(Agent<?, ?> agent) {
        String loc = envState.getAgentLocation(agent);
        VacuumPercept percept = new VacuumPercept(loc, envState.getLocationState(loc));
        List<String> locations = getLocations();
        if (loc.equals(locations.get(0))){
            percept.setAttribute(CAN_MOVE_LEFT, false);
            percept.setAttribute(CAN_MOVE_RIGHT, true);
        } else if(loc.equals(locations.get(locations.size() - 1))){
            percept.setAttribute(CAN_MOVE_LEFT, true);
            percept.setAttribute(CAN_MOVE_RIGHT, false);
        }else {
            percept.setAttribute(CAN_MOVE_LEFT, true);
            percept.setAttribute(CAN_MOVE_RIGHT, true);
        }

        if (agent instanceof NondeterministicSearchAgent) {
            super.getLocations().forEach(location -> {
                        percept.setAttribute(location, envState.getLocationState(location));
                    }
            );
        }
        return percept;
    }

}
