package main.java.com.oli.routing;

import aima.core.search.framework.Node;
import aima.core.search.framework.problem.Problem;
import aima.core.search.framework.problem.StepCostFunction;
import aimax.osm.data.MapWayFilter;
import aimax.osm.data.OsmMap;
import aimax.osm.data.Position;
import aimax.osm.data.entities.MapNode;
import aimax.osm.routing.OsmMoveAction;
import aimax.osm.routing.RouteCalculator;
import aimax.osm.routing.RouteFindingProblem;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

public class ExtendedRouteCalculator extends RouteCalculator {
    public static final int DISTANCE = 0;
    public static final int DISTANCE_CAR = 1;
    public static final int DISTANCE_BIKE = 2;
    public static final int TIME_CAR = 3;
    public static final int FUN_CYCLIST = 4;

    @Override
    public String[] getTaskSelectionOptions() {
        String[] fromSuper = super.getTaskSelectionOptions();
        String[] extension = new String[]{"Time (Car)", "Fun (Cyclist)"};
        return Stream.concat(Arrays.stream(fromSuper), Arrays.stream(extension)).toArray(String[]::new);
    }

    public ExtendedRouteCalculator() {
        super();
    }

    @Override
    protected Problem<MapNode, OsmMoveAction> createProblem(MapNode[] pNodes, OsmMap map, MapWayFilter wayFilter, boolean ignoreOneways, int taskSelection) {
        if (taskSelection < TIME_CAR) {
            return super.createProblem(pNodes, map, wayFilter, ignoreOneways, taskSelection);
        } else if (taskSelection == TIME_CAR) {
            return new RouteFindingProblem(pNodes[0], pNodes[1], wayFilter, ignoreOneways, new TimeStepCostFunction());
        } else {
            if (taskSelection != FUN_CYCLIST) throw new AssertionError("Unexpected taskSelection " + taskSelection);
            return new RouteFindingProblem(pNodes[0], pNodes[1], wayFilter, ignoreOneways, new FunStepCostFunction());
        }
    }

    @Override
    protected ToDoubleFunction<Node<MapNode, OsmMoveAction>> createHeuristicFunction(MapNode[] pNodes, int taskSelection) {
        if (taskSelection < TIME_CAR) {
            return super.createHeuristicFunction(pNodes, taskSelection);
        } else {
            return new TimeHeuristicFunction(pNodes[1]);
        }
    }

    private static class TimeHeuristicFunction implements ToDoubleFunction<Node<MapNode, OsmMoveAction>> {
        private final MapNode goalState;

        private TimeHeuristicFunction(MapNode goalState) {
            this.goalState = goalState;
        }

        @Override
        public double applyAsDouble(Node<MapNode, OsmMoveAction> node) {
            Position newPos = new Position(node.getState());
            double distance = newPos.getDistKM(goalState);
            return distance;
        }
    }

    private static class TimeStepCostFunction implements StepCostFunction<MapNode, OsmMoveAction> {
        private Map<RoadType, Double> speedModifierMap = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(RoadType.MOTORWAY, 100.0),
                new AbstractMap.SimpleEntry<>(RoadType.MOTORWAY_LINK, 80.0),
                new AbstractMap.SimpleEntry<>(RoadType.TRUNK, 70.0),
                new AbstractMap.SimpleEntry<>(RoadType.TRUNK_LINK, 70.0),
                new AbstractMap.SimpleEntry<>(RoadType.PRIMARY, 50.0),
                new AbstractMap.SimpleEntry<>(RoadType.PRIMARY_LINK, 50.0),
                new AbstractMap.SimpleEntry<>(RoadType.SECONDARY, 50.0),
                new AbstractMap.SimpleEntry<>(RoadType.TERTIARY, 50.0),
                new AbstractMap.SimpleEntry<>(RoadType.ROAD, 50.0),
                new AbstractMap.SimpleEntry<>(RoadType.RESIDENTIAL, 30.0),
                new AbstractMap.SimpleEntry<>(RoadType.LIVING_STREET, 30.0),
                new AbstractMap.SimpleEntry<>(RoadType.PEDESTRIAN, 5.0),
                new AbstractMap.SimpleEntry<>(RoadType.SERVICE, 5.0),
                new AbstractMap.SimpleEntry<>(RoadType.TRACK, 5.0),
                new AbstractMap.SimpleEntry<>(RoadType.CYCLEWAY, 15.0),
                new AbstractMap.SimpleEntry<>(RoadType.PATH, 7.0),
                new AbstractMap.SimpleEntry<>(RoadType.FOOTWAY, 5.0),
                new AbstractMap.SimpleEntry<>(RoadType.STEPS, 2.0),
                new AbstractMap.SimpleEntry<>(RoadType.WATERWAY, 1.0)
        );

        @Override
        public double applyAsDouble(MapNode mapNode, OsmMoveAction osmMoveAction, MapNode sDelta) {
            double speedModifier =  speedModifierMap.getOrDefault(RouteSearchUtility.determineRoadType(osmMoveAction.getWay().getAttributes()), 1.0);
            double distance = osmMoveAction.getTravelDistance();
            return distance / speedModifier;
        }
    }

    private static class FunStepCostFunction implements StepCostFunction<MapNode, OsmMoveAction> {
        private Map<RoadType, Double> funModifierMap = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(RoadType.MOTORWAY, 100.0),
                new AbstractMap.SimpleEntry<>(RoadType.MOTORWAY_LINK, 80.0),
                new AbstractMap.SimpleEntry<>(RoadType.TRUNK, 70.0),
                new AbstractMap.SimpleEntry<>(RoadType.TRUNK_LINK, 70.0),
                new AbstractMap.SimpleEntry<>(RoadType.PRIMARY, 50.0),
                new AbstractMap.SimpleEntry<>(RoadType.PRIMARY_LINK, 50.0),
                new AbstractMap.SimpleEntry<>(RoadType.SECONDARY, 50.0),
                new AbstractMap.SimpleEntry<>(RoadType.TERTIARY, 50.0),
                new AbstractMap.SimpleEntry<>(RoadType.ROAD, 50.0),
                new AbstractMap.SimpleEntry<>(RoadType.RESIDENTIAL, 30.0),
                new AbstractMap.SimpleEntry<>(RoadType.LIVING_STREET, 30.0),
                new AbstractMap.SimpleEntry<>(RoadType.PEDESTRIAN, 5.0),
                new AbstractMap.SimpleEntry<>(RoadType.SERVICE, 5.0),
                new AbstractMap.SimpleEntry<>(RoadType.TRACK, 5.0),
                new AbstractMap.SimpleEntry<>(RoadType.CYCLEWAY, 15.0),
                new AbstractMap.SimpleEntry<>(RoadType.PATH, 7.0),
                new AbstractMap.SimpleEntry<>(RoadType.FOOTWAY, 5.0),
                new AbstractMap.SimpleEntry<>(RoadType.STEPS, 2.0),
                new AbstractMap.SimpleEntry<>(RoadType.WATERWAY, 1.0)
        );

        @Override
        public double applyAsDouble(MapNode mapNode, OsmMoveAction osmMoveAction, MapNode sDelta) {
            return funModifierMap.getOrDefault(RouteSearchUtility.determineRoadType(osmMoveAction.getWay().getAttributes()), 1.0);
        }
    }

}

