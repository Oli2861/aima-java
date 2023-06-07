package main.java.com.oli.onlineagent;

import aima.core.agent.Agent;
import aima.core.agent.impl.DynamicPercept;
import aima.core.environment.map.BidirectionalMapProblem;
import aima.core.environment.map.MapFunctions;
import aima.core.environment.map.MoveToAction;
import aima.core.search.framework.problem.GeneralProblem;
import aima.core.search.framework.problem.OnlineSearchProblem;
import aima.core.search.framework.problem.Problem;
import aima.core.search.online.LRTAStarAgent;
import aima.core.search.online.OnlineDFSAgent;
import aima.gui.fx.framework.Parameter;
import aimax.osm.gui.fx.applications.OnlineAgentOsmApp;

import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleFunction;

// VM options (Java>8): --module-path ${PATH_TO_FX} --add-modules javafx.controls,javafx.fxml
public class MyOnlineAgentOsmApp extends OnlineAgentOsmApp {

    public static void main(String[] args){
        launch();
    }

    @Override
    protected List<Parameter> createParameters() {
        Parameter p1 = new Parameter(PARAM_WAY_SELECTION, "Use any way", "Travel by car", "Travel by bicycle");
        Parameter p2 = new Parameter(PARAM_STRATEGY, "Online DFS agent", "LRTA* agent", "Oli's Agent", "Random walk agent");
        p2.setDefaultValueIndex(2);
        Parameter p3 = new Parameter(PARAM_HEURISTIC, "0", "SLD");
        p3.setDefaultValueIndex(1);
        p3.setDependency(PARAM_STRATEGY, "LRTA* agent");
        return Arrays.asList(p1, p2, p3);
    }

    @Override
    protected Agent<DynamicPercept, MoveToAction> createAgent(List<String> locations){
        Problem<String, MoveToAction> p = new BidirectionalMapProblem(map, null, locations.get(1));
        OnlineSearchProblem<String, MoveToAction> osp = new GeneralProblem<>
                (null, p::getActions, null, p::testGoal, p::getStepCosts);

        ToDoubleFunction<String> heuristic;
        if (simPaneCtrl.getParamValueIndex(PARAM_HEURISTIC) == 0)
            heuristic = state -> 0.0;
        else
            heuristic = state -> MapFunctions.getSLD(state, locations.get(1), map);

        Agent<DynamicPercept, MoveToAction> agent;
        int strategy = simPaneCtrl.getParamValueIndex(PARAM_STRATEGY);
        if (strategy == 0)
            agent = new OnlineDFSAgent<>(osp, MapFunctions.createPerceptToStateFunction());
        else if (strategy == 1)
            agent = new LRTAStarAgent<>(osp, MapFunctions.createPerceptToStateFunction(), heuristic);
        else if (strategy == 2)
            agent = new OnlineSearchAgentOli<>(osp, MapFunctions.createPerceptToStateFunction(), heuristic);
        else
            agent = new RandomWalkAgent<>(osp, MapFunctions.createPerceptToStateFunction());
        return agent;
    }

}
