package com.example.liusu.travelapp.functionone;

import com.directions.route.AbstractRouting;

public enum TransportMode {
    TAXI, BUS, FOOT;

    public AbstractRouting.TravelMode toAbstractRoutingTravelMode() {
        AbstractRouting.TravelMode travel_mode = null;
        switch(this) {
            case TAXI:
                travel_mode = AbstractRouting.TravelMode.DRIVING;
                break;
            case BUS:
                travel_mode = AbstractRouting.TravelMode.TRANSIT;
                break;
            case FOOT:
                travel_mode = AbstractRouting.TravelMode.WALKING;
                break;
        }
        return travel_mode;
    }

    public static TransportMode convertString(String transport_mode) {
        TransportMode mode = null;
        switch(transport_mode) {
            case "Taxi":
                mode = TransportMode.TAXI;
                break;
            case "Bus":
                mode = TransportMode.BUS;
                break;
            case "Foot":
                mode = TransportMode.FOOT;
                break;
        }
        return mode;
    }

    public static String toString(TransportMode transport_mode) {
        String mode = null;
        switch(transport_mode) {
            case TAXI:
                mode = "Taxi";
                break;
            case BUS:
                mode = "Bus";
                break;
            case FOOT:
                mode = "Foot";
                break;
        }
        return mode;
    }
}
