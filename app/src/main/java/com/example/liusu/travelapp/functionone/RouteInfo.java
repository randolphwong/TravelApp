package com.example.liusu.travelapp.functionone;

import com.directions.route.Route;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class RouteInfo {
    private TransportMode transport_mode;
    private ArrayList<LatLng> end_latlngs;
    private ArrayList<LatLng> all_latlngs;
    private double distance;
    private int duration;

    public RouteInfo() {
        end_latlngs = new ArrayList<>();
        all_latlngs = new ArrayList<>();
    }

    public RouteInfo(TransportMode transport_mode) {
        this();
        this.transport_mode = transport_mode;
    }

    public void setRoute(Route route) {
        distance = (double) route.getDistanceValue() / 1000;
        duration = route.getDurationValue() / 60;
        all_latlngs = (ArrayList<LatLng>) route.getPoints();
    }

    public void setTransportMode(TransportMode transport_mode) {
        this.transport_mode = transport_mode;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setLatLng(ArrayList<LatLng> latlngs) {
        all_latlngs = latlngs;

        end_latlngs.add(all_latlngs.get(0));
        end_latlngs.add(all_latlngs.get(all_latlngs.size() - 1));
    }

    public TransportMode getTransportMode() {
        return transport_mode;
    }

    public int getDuration() {
        return duration;
    }

    public double getDistance() {
        return distance;
    }

    public double getCost() {
        double cost = 0;
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

    public void addEndLatLng(LatLng latlng) {
        end_latlngs.add(latlng);
    }

    public ArrayList<LatLng> getEndPoints() {
        return end_latlngs;
    }

    public ArrayList<LatLng> getPoints() {
        return all_latlngs;
    }
}
