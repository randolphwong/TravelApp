package com.example.liusu.travelapp.functionone;

class IncidentArray {
    private int destination_node;
    private TransportMode transport_mode;

    public IncidentArray() {}

    public IncidentArray(int destination_node, TransportMode transport_mode) {
        this.destination_node = destination_node;
        this.transport_mode = transport_mode;
    }

    public void setDestinationNode(int destination_node) {
        this.destination_node = destination_node;
    }

    public void setTransportMode(TransportMode transport_mode) {
        this.transport_mode = transport_mode;
    }

    public int getDestinationNode() {
        return destination_node;
    }

    public TransportMode getTransportMode() {
        return transport_mode;
    }

    public String toString() {
        return destination_node + ":" + transport_mode;
    }
}

