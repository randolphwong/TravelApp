package com.example.liusu.travelapp.functionone;

import com.directions.route.Route;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RouteInfo {
    private Route route;
    private TransportMode transport_mode;
    private ArrayList<LatLng> latlngs;

    public RouteInfo() {
        latlngs = new ArrayList<>();
    }

    public RouteInfo(Route route, TransportMode transport_mode) {
        this.route = route;
        this.transport_mode = transport_mode;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setTransportMode(TransportMode transport_mode) {
        this.transport_mode = transport_mode;
    }

    public Route getRoute() {
        return route;
    }

    public TransportMode getTransportMode() {
        return transport_mode;
    }

    public int getDuration() {
        return route.getDurationValue() / 60;
    }

    public double getCost() {
        double cost = 0;
        double distance = (double) route.getDistanceValue() / 1000;
        switch (transport_mode) {
            case TAXI:
                cost = 3 + 0.66 * distance;
                break;
            case BUS:
                cost = 0.7 + 0.1 * distance;
                break;
            case FOOT:
                cost = 0;
                break;
        }
        return cost;
    }

    public void addLatLng(LatLng latlng) {
        latlngs.add(latlng);
    }

    public ArrayList<LatLng> getEndPoints() {
        return latlngs;
    }
}
