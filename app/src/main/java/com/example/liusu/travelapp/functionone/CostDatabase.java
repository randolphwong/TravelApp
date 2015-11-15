package com.example.liusu.travelapp.functionone;

import java.util.HashMap;

public class CostDatabase {
    private HashMap<Integer, HashMap<Integer, TravelCost>> database;

    public CostDatabase() {
        database = new HashMap<>();
    }

    public void add(Integer source, Integer destination, RouteInfo route_info) {
        if (!database.containsKey(source))
            database.put(source, new HashMap<Integer, TravelCost>());
        if (!database.get(source).containsKey(destination))
            database.get(source).put(destination, new TravelCost());
        database.get(source).get(destination).addRouteInfo(route_info);
    }

    public int timeBetween(Integer source, Integer destination, TransportMode transport_mode) {
        return database.get(source).get(destination).getDuration(transport_mode);
    }

    public double costBetween(Integer source, Integer destination, TransportMode transport_mode) {
        return database.get(source).get(destination).getCost(transport_mode);
    }

    public RouteInfo getRouteInfo(Integer source, Integer destination, TransportMode transport_mode) {
        return database.get(source).get(destination).getRouteInfo(transport_mode);
    }
}
