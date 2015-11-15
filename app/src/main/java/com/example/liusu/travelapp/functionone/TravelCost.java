package com.example.liusu.travelapp.functionone;

import java.util.EnumMap;

public class TravelCost {
    private EnumMap<TransportMode, RouteInfo> route_infos;

    public TravelCost() {
        route_infos = new EnumMap<>(TransportMode.class);
    }

    public int getDuration(TransportMode transport_mode) {
        return route_infos.get(transport_mode).getDuration();
    }

    public double getCost(TransportMode transport_mode) {
        return route_infos.get(transport_mode).getCost();
    }

    public RouteInfo getRouteInfo(TransportMode transport_mode) {
        return route_infos.get(transport_mode);
    }

    public void addRouteInfo(RouteInfo route_info) {
        route_infos.put(route_info.getTransportMode(), route_info);
    }
}
