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
        /*
        Reflex agent:
        Problem of the simple version: As it does not know where it was or where it came from it is hard to decide where
        to move. As the agent does not maintain state about where it came from, I decided to just choose a random
        direction if it could move in both directions which is obviously not the best idea.
         */
        //Agent<VacuumPercept, Action> agent = new ReflexAgentForLargeVacuumEnv();

        /*
        Model-based reflex agent:

         */
        Agent<VacuumPercept, Action> agent = new ModelBasedReflexAgentForLargeVaccumEnv();
        runExperiment(agent, 8);
    }

    private static void runExperiment(Agent<? super VacuumPercept, ? extends Action> agent, int fieldSize) {
        Environment<VacuumPercept, Action> env = LargeVacuumEnvBuilder.buildLargeVacuumEnv(fieldSize);
        env.addEnvironmentListener(new SimpleEnvironmentView());
        env.addAgent(agent);

        env.step(16);
        env.notify("Performance: " + env.getPerformanceMeasure(agent));
    }
}

