package com.example.liusu.travelapp.functionone;

import java.util.ArrayList;

public class PathPlanner {

    private PathPlanner() {}

    public static ArrayList<RouteInfo> getPath(String hotel, double budget, AttractionDatabase attraction_database) {
        ArrayList<RouteInfo> path = new ArrayList<RouteInfo>();
        String[][] string_path = getStringPath(hotel, budget, attraction_database);

        for (int i = 0; i != string_path.length; ++i) {
            String source = string_path[i][0];
            String destination = string_path[i][1];
            String transport_mode = string_path[i][2];
            RouteInfo route_info = attraction_database.routeInfoBetween(source, destination, transport_mode);
            path.add(route_info);
        }
        return path;
    }

    public static String[][] getStringPath(String hotel, double budget, AttractionDatabase attraction_database) {
        IncidentArray[] path_as_alias = PathOptimiser.getBestPath(hotel, budget, attraction_database);

        String[][] path_as_real_name = new String[path_as_alias.length][3];
        setPathInOrder(path_as_real_name, path_as_alias, attraction_database.indexOf(hotel), 0, attraction_database);

        return path_as_real_name;
    }

    private static void setPathInOrder(String[][] path, IncidentArray[] path_as_alias, int source, int index, AttractionDatabase attraction_database) {
        if (index != path.length) {
            path[index][0] = attraction_database.nameOf(source);
            path[index][1] = attraction_database.nameOf(path_as_alias[source].getDestinationNode());
            path[index][2] = TransportMode.toString(path_as_alias[source].getTransportMode());
            setPathInOrder(path, path_as_alias, path_as_alias[source].getDestinationNode(), index + 1, attraction_database);
        }
    }

    // to change
/*
 *    public int timeTaken(String[][] path) {
 *        return attraction_data.travelTimeOf(path);
 *    }
 *
 *    public double cost(String[][] path) {
 *        return attraction_data.travelCostOf(path);
 *    }
 */
}
