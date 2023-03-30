package main.java.com.oli.vacuum;

import aima.core.environment.vacuum.VacuumEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class LargeVacuumEnvBuilder {
    public static LargeVacuumEnv buildLargeVacuumEnv(int fieldSize) {
        List<String> locations = new ArrayList<>(fieldSize);
        VacuumEnvironment.LocationState[] states = new VacuumEnvironment.LocationState[fieldSize];

        for(int i = 0; i < fieldSize; i++){
            locations.add(i, String.valueOf(i));
            if (Math.random() < 0.5)
                states[i] = VacuumEnvironment.LocationState.Dirty;
            else
                states[i] = VacuumEnvironment.LocationState.Clean;
        }
        System.out.println("Fields: " + locations);
        System.out.println("Initial states: " + Arrays.toString(states));
        return new LargeVacuumEnv(locations, states);
    }
}
