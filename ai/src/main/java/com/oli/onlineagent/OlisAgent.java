package main.java.com.oli.onlineagent;

import aima.core.agent.impl.SimpleAgent;
import aima.core.search.framework.problem.OnlineSearchProblem;
import aima.core.util.datastructure.TwoKeyHashMap;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public class OlisAgent<P, S, A> extends SimpleAgent<P, A> {
    private OnlineSearchProblem<S, A> onlineSearchProblem;
    private Function<P, S> perceptToStateFunction;
    /**
     * Heuristic function h.
     */
    private ToDoubleFunction<S> h;
    /**
     * Learned heuristic function H which is distinct from h.
     */
    private final HashMap<S, Double> H = new HashMap<>();
    /**
     * Results.
     */
    private final TwoKeyHashMap<S, A, S> result = new TwoKeyHashMap<>();
    /**
     * Curren state s.
     */
    private S state = null;
    /**
     * Next action a.
     */
    private A action = null;

    /**
     * Constructs my agent.
     *
     * @param onlineSearchProblem    Problem the agent is supposed to solve.
     * @param perceptToStateFunction Function returning the problem state associated with the given percept.
     * @param heuristicFunction      Heuristic function h(s) estimating the cost of the cheapest path from state s to goal state.
     */
    public OlisAgent(OnlineSearchProblem<S, A> onlineSearchProblem, Function<P, S> perceptToStateFunction, ToDoubleFunction<S> heuristicFunction) {
        this.perceptToStateFunction = perceptToStateFunction;
        this.h = heuristicFunction;
        this.onlineSearchProblem = onlineSearchProblem;
        reset();
    }

    /**
     * Actions of the agent based on the percepts p and its knowledge.
     *
     * @param perceptIdentifyingCurrentState The current percept of a sequence perceived by the Agent s'.
     * @return The next action.
     */
    @Override
    public Optional<A> act(P perceptIdentifyingCurrentState) {
        S primedState = perceptToStateFunction.apply(perceptIdentifyingCurrentState);

        if (onlineSearchProblem.testGoal(primedState)) return reachedGoalState(primedState);
        else return search(primedState);
    }


    /**
     * Function to handle when the primed state corresponds to the goal state.
     *
     * @param primedState Primed state s'.
     * @return No action.
     */
    private Optional<A> reachedGoalState(S primedState) {
        // Goal state reached --> no action
        action = null;
        setAlive(false);
        state = primedState;
        return Optional.ofNullable(action);
    }

    /**
     * Search for the next action.
     *
     * @param primedState Primed state s'.
     * @return The next action a.
     */
    private Optional<A> search(S primedState) {
        // Add new states to history: H[s'] <- h(s')
        if (!H.containsKey(primedState)) H.put(primedState, h.applyAsDouble(primedState));
        if (state != null) saveCostToCurrentState(primedState);
        action = onlineSearchProblem.getActions(primedState)
                .stream()
                .min(Comparator.comparingDouble(currentAction -> costFunction(primedState, currentAction, result.get(primedState, currentAction))))
                .orElse(null);

        if (action == null) setAlive(false);
        // s <- s'
        state = primedState;
        return Optional.ofNullable(action);
    }

    /**
     * Save the cost to the current state.
     * @param primedState Primed state s'.
     */
    private void saveCostToCurrentState(S primedState) {
        // Result [s, a] <- s'
        result.put(state, action, primedState);

        // Add cheapest cost to history for the state H[s] min cost(s, currentAction, result[a, currentAction], H)
        double minCost = onlineSearchProblem.getActions(state)
                .stream()
                .mapToDouble(currentAction -> costFunction(state, currentAction, result.get(state, currentAction)))
                .min()
                .orElse(Double.MAX_VALUE);
        H.put(state, minCost);
    }

    /**
     * Wrapper around the cost function of the search problem.
     *
     * @param state  Current state s.
     * @param action Action a.
     * @param sNew   New state s'.
     * @return heuristic function applied to the curren state h(s) if the s' is undefined else the cost function c(s, a, s') + H[s']
     */
    private double costFunction(S state, A action, S sNew) {
        if (sNew == null) {
            // Use the estimated cost to the goal state if the cost to the next state is unknown.
            return h.applyAsDouble(state) * 1.2;
        } else {
            double cost = onlineSearchProblem.getStepCosts(state, action, sNew) + H.getOrDefault(sNew, Double.MAX_VALUE);
            // Punish moving in the wrong direction
            if (h.applyAsDouble(state) < h.applyAsDouble(sNew)) {
                cost *= 1.4;
            }
            return cost;
        }
    }

    /**
     * Reset the agents' knowledge.
     */
    private void reset() {
        result.clear();
        H.clear();
        state = null;
        action = null;
    }

}
