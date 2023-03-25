package main.java.com.oli.vacuum;

import aima.core.agent.Action;
import aima.core.agent.Agent;
import aima.core.agent.Environment;
import aima.core.agent.impl.SimpleEnvironmentView;
import aima.core.environment.vacuum.ModelBasedReflexVacuumAgent;
import aima.core.environment.vacuum.ReflexVacuumAgent;
import aima.core.environment.vacuum.VacuumEnvironment;
import aima.core.environment.vacuum.VacuumPercept;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LargeVacuumEnvExperiments {

    public static void main(String[] args) {
        Agent<VacuumPercept, Action> agent = new ReflexAgentForLargeVacuumEnv();
        runExperiment(agent, 8);
    }

    private static void runExperiment(Agent<? super VacuumPercept, ? extends Action> agent, int fieldSize) {
        List<String> fields = new ArrayList<>(fieldSize);
        VacuumEnvironment.LocationState[] states = new VacuumEnvironment.LocationState[fieldSize];

        for(int i = 0; i < fieldSize; i++){
            fields.add(i, String.valueOf(i));
            if (Math.random() < 0.5)
                states[i] = VacuumEnvironment.LocationState.Dirty;
            else
                states[i] = VacuumEnvironment.LocationState.Clean;
        }
        System.out.println("Fields: " + fields);
        System.out.println("Initial states: " + Arrays.toString(states));
        Environment<VacuumPercept, Action> env = new LargeVacuumEnv(fields, states);
        env.addEnvironmentListener(new SimpleEnvironmentView());
        env.addAgent(agent);

        env.step(16);
        env.notify("Performance: " + env.getPerformanceMeasure(agent));
    }
}

