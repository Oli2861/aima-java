package main.java.com.oli.routing;

import aimax.osm.data.DataResource;
import aimax.osm.gui.swing.applications.RoutePlannerApp;
import aimax.osm.routing.RouteCalculator;

import java.util.Locale;

public class ExtendedRoutePlannerApp extends RoutePlannerApp {

    public ExtendedRoutePlannerApp(String[] args) {
        super(args);
        frame.setTitle("OSM Route Planner (Extended)");
    }

    @Override
    protected RouteCalculator createRouteCalculator() {
        return new ExtendedRouteCalculator();
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        RoutePlannerApp demo = new ExtendedRoutePlannerApp(args);
        demo.getFrame().readMap(DataResource.getUlmFileResource());
        demo.showFrame();
    }

}
