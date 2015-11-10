package com.example.liusu.travelapp.functionone;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.EnumMap;

import java.io.IOException;

import android.util.Log;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;
import android.content.Context;


import com.example.liusu.travelapp.sqldatabase.DBRoute;
import com.example.liusu.travelapp.sqldatabase.MyDBHandler;
import com.example.liusu.travelapp.sqldatabase.LatLngParser;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;

import com.google.android.gms.maps.model.LatLng;

public class AttractionDatabase implements RoutingListener {
    private FileDatabase file_database;

    private ArrayList<String> name_database;
    private HashMap<Integer, LatLng> latlng_database;
    private CostDatabase cost_database;

    private ArrayList<NodePairInfo> places_to_be_routed;
    private ArrayList<NodePairInfo> places_to_be_updated;

    private Context context;

    private boolean updated = true;

    private MyDBHandler dbhandler;

    public AttractionDatabase() {}

    public AttractionDatabase(Context context) {
        name_database = new ArrayList<>();
        latlng_database = new HashMap<>();
        cost_database = new CostDatabase();
        places_to_be_routed = new ArrayList<>();
        places_to_be_updated = new ArrayList<>();
        this.context = context;
        dbhandler = new MyDBHandler(context, null, null, 1);
    }

    public int size() {
        return name_database.size();
    }

    public int indexOf(String attraction) {
        return name_database.indexOf(attraction);
    }

    public String nameOf(int index) {
        return name_database.get(index);
    }

    public LatLng latLngOf(Integer index) {
        return latlng_database.get(index);
    }

    public LatLng latLngOf(String attraction) {
        return latLngOf(indexOf(attraction));
    }

    public boolean isUpdated() {
        return updated;
    }

    public boolean contains(String attraction) {
        return name_database.contains(attraction);
    }

    public void add(String attraction) {
        if (!name_database.contains(attraction)) {
            name_database.add(attraction);

            // to address problem of failed update
            // check if location already in database
            
            if (updateLatLngDatabase(attraction) && size() > 1) {
                Log.i("i", "Adding " + attraction);
                updated = false;
                updateCostDatabase(attraction);
            }
            //Toast.makeText(context, "Adding " + attraction + " to database.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateCostDatabase(String latest_attraction) {
        boolean to_download = false;
        for (int i = 0; i != size() - 1; ++i) {
            String previous_attraction = nameOf(i);
            String[] route_details = dbhandler.getRouteDetails(previous_attraction, latest_attraction);
            if (route_details[0] == null) {
                Log.i("i", "Downloading using Google API.");
                updateCostDatabaseWithAPI(previous_attraction, latest_attraction);
                updateCostDatabaseWithAPI(latest_attraction, previous_attraction);
                to_download = true;
            } else {
                Log.i("i", "Getting information from SQL database.");
                syncWithSQL(previous_attraction, latest_attraction);
                syncWithSQL(latest_attraction, previous_attraction);
                updated = true;
            }
        }
        if (to_download)
            getNextRoute();
    }

    private void syncWithSQL(String latest_attraction) {
        for (int i = 0; i != size() - 1; ++i) {
            String previous_attraction = nameOf(i);
            syncWithSQL(previous_attraction, latest_attraction);
            syncWithSQL(latest_attraction, previous_attraction);
        }
        updated = true;
    }

    private void syncWithSQL(String attraction1, String attraction2) {
        // returns detail of the route in String[] format, 0-columnid, 1-longcoord, 2-latcoord, 3-walktime, 4-bustime, 5-taxitime, 6-buscost, 7-taxicost, 8-description
        //public static EnumMap<TransportMode, ArrayList<LatLng>> stringToLatLngForAllMode(String[] latlng_string) {
        String[] route_details = dbhandler.getRouteDetails(attraction1, attraction2);
        //Log.i("i", String.format("Syncing with SQl for %s to %s", attraction1, attraction2));
        //Log.i("i", "columnid = " + route_details[0]);
        //Log.i("i", "lat array = " + route_details[1]);
        //Log.i("i", "lng array = " + route_details[2]);
        //Log.i("i", "walk time = " + route_details[3]);
        //Log.i("i", "bus time = " + route_details[4]);
        //Log.i("i", "taxi time = " + route_details[5]);
        //Log.i("i", "bus distance = " + route_details[6]);
        //Log.i("i", "taxi distance = " + route_details[7]);
        RouteInfo foot_route_info = new RouteInfo(TransportMode.FOOT);
        RouteInfo bus_route_info = new RouteInfo(TransportMode.BUS);
        RouteInfo taxi_route_info = new RouteInfo(TransportMode.TAXI);
        String[] latlng_string = new String[]{route_details[1], route_details[2]};
        //Log.i("i", "lat string:\n" + latlng_string[0]);
        //Log.i("i", "lng string:\n" + latlng_string[1]);
        EnumMap<TransportMode, ArrayList<LatLng>> all_latlngs = LatLngParser.stringToLatLngForAllMode(new String[]{route_details[1], route_details[2]});
        //Log.i("i", "size of latlng array = " + latlng_array.size());
        //Log.i("i", "first latlng of latlng array = " + latlng_array.get(0));
        //Log.i("i", "last latlng of latlng array = " + latlng_array.get(latlng_array.size() - 1));
        foot_route_info.setLatLng(all_latlngs.get(TransportMode.FOOT));
        foot_route_info.setDuration(Integer.valueOf(route_details[3]));
        foot_route_info.setDistance(0);
        bus_route_info.setLatLng(all_latlngs.get(TransportMode.BUS));
        bus_route_info.setDuration(Integer.valueOf(route_details[4]));
        bus_route_info.setDistance(6);
        taxi_route_info.setLatLng(all_latlngs.get(TransportMode.TAXI));
        taxi_route_info.setDuration(Integer.valueOf(route_details[5]));
        taxi_route_info.setDistance(7);

        cost_database.add(indexOf(attraction1), indexOf(attraction2), foot_route_info);
        cost_database.add(indexOf(attraction1), indexOf(attraction2), bus_route_info);
        cost_database.add(indexOf(attraction1), indexOf(attraction2), taxi_route_info);
    }
    
    private boolean updateLatLngDatabase(String attraction) {
        double latitude, longitude;
        boolean geocoder_success = false;
        Geocoder geocoder = new Geocoder(context);
        List<Address> matched_list = null;
        try {
            matched_list = geocoder.getFromLocationName(attraction, 1);
            latitude = matched_list.get(0).getLatitude();
            longitude = matched_list.get(0).getLongitude();
            latlng_database.put(indexOf(attraction), new LatLng(latitude, longitude));
            geocoder_success = true;
        }
        catch (Exception e) {
            if (e.getMessage() != null)
                Log.e("e", e.getMessage());
            else
                Log.e("e", "No message?");
            // remove attraction from database
            if (name_database.remove(attraction)) {
                Toast.makeText(context, "LatLng information cannot be obtained for: " + attraction + ".", Toast.LENGTH_LONG).show();
            }
        }
        return geocoder_success;
    }

    private void updateCostDatabaseWithAPI(String attraction) {
        for (int i = 0; i != name_database.size(); ++i) {
            if (!attraction.equals(nameOf(i))) {
//                Log.i("i", "In updateCostDatabase");
                for (TransportMode mode : TransportMode.values()) {
                    NodePairInfo info = new NodePairInfo(attraction, nameOf(i), mode);
                    NodePairInfo reverse_info = new NodePairInfo(nameOf(i), attraction, mode);
                    places_to_be_routed.add(info);
                    places_to_be_routed.add(reverse_info);
//                    Log.i("i", String.format("NodePairInfo(%s, %s, %s) added to places_to_be_routed",
//                            info.getSource(), info.getDestination(), info.getTransportMode()));
//                    Log.i("i", String.format("NodePairInfo(%s, %s, %s) added to places_to_be_routed",
//                            reverse_info.getSource(), reverse_info.getDestination(), reverse_info.getTransportMode()));
                }
            }
        }
        getNextRoute();
    }

    private void updateCostDatabaseWithAPI(String attraction1, String attraction2) {
        for (TransportMode mode : TransportMode.values()) {
            NodePairInfo info = new NodePairInfo(attraction1, attraction2, mode);
            NodePairInfo reverse_info = new NodePairInfo(attraction2, attraction1, mode);
            places_to_be_routed.add(info);
            places_to_be_routed.add(reverse_info);
        }
    }

    private void getNextRoute() {
        if (places_to_be_routed.size() > 0) {
            NodePairInfo info = places_to_be_routed.get(places_to_be_routed.size() - 1);
            String source = info.getSource();
            String destination = info.getDestination();
            TransportMode mode = info.getTransportMode();
            LatLng start = latLngOf(source);
            LatLng end = latLngOf(destination);

            Routing routing = new Routing.Builder()
                    .travelMode(mode.toAbstractRoutingTravelMode())
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .build();
            routing.execute();
        }
        else {
//            Log.i("i", "appending Routes, current places_to_be_updated.size() is: " + places_to_be_updated.size());
            appendRoutes();
        }
    }

    private void appendRoutes() {
//        Log.i("i", "In appendRoutes");
        for (int i = 0; i != places_to_be_updated.size(); ++i) {
            NodePairInfo info = places_to_be_updated.get(i);
            Integer source = indexOf(info.getSource());
            Integer destination = indexOf(info.getDestination());
            RouteInfo route_info = info.getRouteInfo();
            // add in latlng infomations
            route_info.addEndLatLng(latlng_database.get(source));
            route_info.addEndLatLng(latlng_database.get(destination));
            cost_database.add(source, destination, route_info);
            //Log.i("i", "end points: " + route_info.getEndPoints());
            //Log.i("i", "first index of all points: " + route_info.getPoints().get(0));
            //Log.i("i", "last index of all points: " + route_info.getPoints().get(route_info.getPoints().size() - 1));
//            Log.i("i", String.format("cost_database.add(%d, %d, %s)", source, destination, route_info.getTransportMode()));
        }
        places_to_be_updated.clear();
        updateSQLDatabase();
        updated = true;
        Log.i("i", "Finish updating database");
    }

    private void updateSQLDatabase() {
        if (size() > 1) {
            String latest_attraction = nameOf(size() - 1);
            for (int i = 0; i != size() - 1; ++i) {
                String previous_attraction = nameOf(i);
                updateSQLDatabase(previous_attraction, latest_attraction);
                updateSQLDatabase(latest_attraction, previous_attraction);
            }
        }
    }

    private void updateSQLDatabase(String attraction1, String attraction2) {
        //Log.i("i", "lat: " + latlng_string[0]);
        //Log.i("i", "lng: " + latlng_string[1]);
        //public static String[] latLngToStringForAllMode(EnumMap<TransportMode, ArrayList<LatLng>> all_latlngs) {
        EnumMap<TransportMode, ArrayList<LatLng>> all_latlngs = new EnumMap<>(TransportMode.class);
        all_latlngs.put(TransportMode.FOOT,
                routeInfoBetween(attraction1, attraction2, TransportMode.toString(TransportMode.FOOT)).getPoints());
        all_latlngs.put(TransportMode.BUS,
                routeInfoBetween(attraction1, attraction2, TransportMode.toString(TransportMode.BUS)).getPoints());
        all_latlngs.put(TransportMode.TAXI,
                routeInfoBetween(attraction1, attraction2, TransportMode.toString(TransportMode.TAXI)).getPoints());

        String[] latlng_string = LatLngParser.latLngToStringForAllMode(all_latlngs);
        DBRoute dbroute = new DBRoute(attraction1, attraction2, latlng_string[0], latlng_string[1], 
                timeBetween(indexOf(attraction1), indexOf(attraction2), TransportMode.FOOT),
                timeBetween(indexOf(attraction1), indexOf(attraction2), TransportMode.BUS),
                timeBetween(indexOf(attraction1), indexOf(attraction2), TransportMode.TAXI),
                routeInfoBetween(attraction1, attraction2, TransportMode.toString(TransportMode.BUS)).getDistance(),
                routeInfoBetween(attraction1, attraction2, TransportMode.toString(TransportMode.TAXI)).getDistance(),
                "no desc");
        dbhandler.addLocations(dbroute);
    }

    @Override
    public void onRoutingFailure() {
        // The Routing request failed
        Log.e("e", "Routing failed");
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingCancelled() {
        Log.e("e", "Routing cancelled");
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        NodePairInfo info = places_to_be_routed.get(places_to_be_routed.size() - 1);
        if (route.size() != 0) {
            info.setRoute(route.get(shortestRouteIndex));
            places_to_be_updated.add(info);
//            Log.i("i", String.format("Routing success, NodePairInfo(%s, %s, %s) added to places_to_be_updated",
//                    info.getSource(), info.getDestination(), info.getTransportMode()));
            places_to_be_routed.remove(places_to_be_routed.size() - 1);
        }
        else {
            Log.e("e", String.format("No route found from %s to %s by %s", info.getSource(), info.getDestination(), info.getTransportMode()));
        }
        getNextRoute();
    }

    public int[] allDestinationOf(int source) {
        int[] all_destinations = new int[name_database.size() - 1];
        for (int i = 0, j = 0; i != all_destinations.length; ++j) {
            if (j != source)
                all_destinations[i++] = j;
        }
        return all_destinations;
    }

    public int timeBetween(int source, int destination) {
        return timeBetween(source, destination, TransportMode.TAXI);
    }

    public int timeBetween(int source, int destination, TransportMode transport_mode) {
        return cost_database.timeBetween(source, destination, transport_mode);
    }

    public double costBetween(int source, int destination) {
        return costBetween(source, destination, TransportMode.TAXI);
    }

    public double costBetween(int source, int destination, TransportMode transport_mode) {
        return cost_database.costBetween(source, destination, transport_mode);
    }

    public int timeBetween(String source, String destination) {
        return timeBetween(indexOf(source), indexOf(destination), TransportMode.TAXI);
    }

    public int travelTimeOf(LinkedList<Integer> path) {
        int sum = 0;
        for (int i = 0; i != path.size() - 1; ++i) {
            sum += timeBetween(path.get(i), path.get(i + 1));
        }
        return sum;
    }

    public double travelCostOf(LinkedList<Integer> path) {
        double sum = 0;
        for (int i = 0; i != path.size() - 1; ++i) {
            sum += costBetween(path.get(i), path.get(i + 1));
        }
        return sum;
    }

    public int travelTimeOf(IncidentArray[] path) {
        int sum = 0;
        for (int i = 0; i != path.length; ++i) {
            sum += timeBetween(i, path[i].getDestinationNode(), path[i].getTransportMode());
        }
        return sum;
    }

    public double travelCostOf(IncidentArray[] path) {
        double sum = 0;
        for (int i = 0; i != path.length; ++i) {
            sum += costBetween(i, path[i].getDestinationNode(), path[i].getTransportMode());
        }
        return sum;
    }

    public int travelTimeOf(ArrayList<RouteInfo> path) {
        int sum = 0;
        for (int i = 0; i != path.size(); ++i) {
            sum += path.get(i).getDuration();
        }
        return sum;
    }

    public double travelCostOf(ArrayList<RouteInfo> path) {
        double sum = 0;
        for (int i = 0; i != path.size(); ++i) {
            sum += path.get(i).getCost();
        }
        return sum;
    }

    public RouteInfo routeInfoBetween(String source, String destination, String transport_mode) {
        return cost_database.getRouteInfo(indexOf(source), indexOf(destination), TransportMode.convertString(transport_mode));
    }
}

