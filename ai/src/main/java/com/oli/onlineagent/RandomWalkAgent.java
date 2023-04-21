package main.java.com.oli.onlineagent;

import aima.core.agent.impl.SimpleAgent;
import aima.core.search.framework.problem.OnlineSearchProblem;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
public class RandomWalkAgent<P, S, A> extends SimpleAgent<P, A> {
    private OnlineSearchProblem<S, A> onlineSearchProblem;
    private Function<P, S> perceptToStateFunction;
    /**
     * Next action a.
     */
    private A action = null;

    /**
     * Constructs my agent.
     * @param onlineSearchProblem Problem the agent is supposed to solve.
     * @param perceptToStateFunction Function returning the problem state associated with the given percept.
     */
    public RandomWalkAgent(OnlineSearchProblem<S, A> onlineSearchProblem, Function<P, S> perceptToStateFunction) {
        this.perceptToStateFunction = perceptToStateFunction;
        this.onlineSearchProblem = onlineSearchProblem;
        reset();
    }

    /**
     * Actions of the agent based on the percepts p and its knowledge.
     * @param perceptIdentifyingCurrentState
     *      	The current percept of a sequence perceived by the Agent s'.
     * @return The next action.
     */
    @Override
    public Optional<A> act(P perceptIdentifyingCurrentState) {
        S primedState = perceptToStateFunction.apply(perceptIdentifyingCurrentState);

        if (onlineSearchProblem.testGoal(primedState)) return reachedGoalState(primedState);
        else return chooseRandomAction(primedState);
    }

    private Optional<A> reachedGoalState(S primedState){
        // Goal state reached --> no action
        action = null;
        setAlive(false);
        return Optional.ofNullable(action);
    }

    private Optional<A> chooseRandomAction(S primedState){
        List<A> actions = onlineSearchProblem.getActions(primedState);
        action = actions.get((int) (Math.random() * actions.size()));
        if(action == null) setAlive(false);
        return Optional.ofNullable(action);
    }

    /**
     * Reset the agents' knowledge.
     */
    private void reset() {
        action = null;
    }

}
