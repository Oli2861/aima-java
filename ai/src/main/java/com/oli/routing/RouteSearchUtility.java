package main.java.com.oli.routing;

import aimax.osm.data.entities.EntityAttribute;

abstract class RouteSearchUtility {
    public static RoadType determineRoadType(EntityAttribute[] attributes) {
        for (EntityAttribute attribute : attributes) {
            if (attribute.getKey().equals("highway")) {
                return RoadType.valueOf(attribute.getValue().toUpperCase());
            }
        }
        return null;
    }
}
