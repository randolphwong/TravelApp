package com.example.liusu.travelapp.functionone;

import com.directions.route.Route;

public class NodePairInfo {
    private String source;
    private String destination;
    private TransportMode transport_mode;
    private RouteInfo route_info;

    public NodePairInfo() {}

    public NodePairInfo(String source, String destination, TransportMode transport_mode) {
        this.source = source;
        this.destination = destination;
        this.transport_mode = transport_mode;
        this.route_info = new RouteInfo();
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public TransportMode getTransportMode() {
        return transport_mode;
    }

    public RouteInfo getRouteInfo() {
        return route_info;
    }

    public void setRoute(Route route) {
        route_info.setRoute(route);
        route_info.setTransportMode(transport_mode);
    }
}
