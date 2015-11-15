package com.example.liusu.travelapp.functionone;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.EnumMap;

import android.util.Log;
import android.widget.Toast;
import android.location.Address;
import android.location.Geocoder;
import android.content.Context;


import com.example.liusu.travelapp.sqldatabase.DBRoute;
import com.example.liusu.travelapp.sqldatabase.MyDBHandler;
import com.example.liusu.travelapp.sqldatabase.LatLngParser;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;

import com.google.android.gms.maps.model.LatLng;

public class AttractionDatabase implements RoutingListener {

    private ArrayList<String> name_database;
    private HashMap<Integer, LatLng> latlng_database;
    private CostDatabase cost_database;

    private ArrayList<NodePairInfo> places_to_be_routed;
    private ArrayList<NodePairInfo> places_to_be_updated;

    private Context context;

    private boolean updated = true;

    private MyDBHandler dbhandler;

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

            if (updateLatLngDatabase(attraction) && size() > 1) {
                updated = false;
                updateCostDatabase(attraction);
            }
        }
    }

    private void updateCostDatabase(String latest_attraction) {
        boolean to_download = false;
        for (int i = 0; i != size() - 1; ++i) {
            String previous_attraction = nameOf(i);
            String[] route_details = dbhandler.getRouteDetails(previous_attraction, latest_attraction);
            if (route_details[0] == null) {
                updateCostDatabaseWithAPI(previous_attraction, latest_attraction);
                updateCostDatabaseWithAPI(latest_attraction, previous_attraction);
                to_download = true;
            } else {
                syncWithSQL(previous_attraction, latest_attraction);
                syncWithSQL(latest_attraction, previous_attraction);
            }
        }
        if (to_download)
            getNextRoute();
        else
            updated = true;
    }

    private void syncWithSQL(String attraction1, String attraction2) {
        String[] route_details = dbhandler.getRouteDetails(attraction1, attraction2);
        RouteInfo foot_route_info = new RouteInfo(TransportMode.FOOT);
        RouteInfo bus_route_info = new RouteInfo(TransportMode.BUS);
        RouteInfo taxi_route_info = new RouteInfo(TransportMode.TAXI);
        EnumMap<TransportMode, ArrayList<LatLng>> all_latlngs = LatLngParser.stringToLatLngForAllMode(new String[]{route_details[1], route_details[2]});

        foot_route_info.setLatLng(all_latlngs.get(TransportMode.FOOT));
        foot_route_info.setDuration(Integer.parseInt(route_details[3]));
        foot_route_info.setDistance(0);

        bus_route_info.setLatLng(all_latlngs.get(TransportMode.BUS));
        bus_route_info.setDuration(Integer.parseInt(route_details[4]));
        bus_route_info.setDistance(Double.parseDouble(route_details[6]));

        taxi_route_info.setLatLng(all_latlngs.get(TransportMode.TAXI));
        taxi_route_info.setDuration(Integer.parseInt(route_details[5]));
        taxi_route_info.setDistance(Double.parseDouble(route_details[7]));

        cost_database.add(indexOf(attraction1), indexOf(attraction2), foot_route_info);
        cost_database.add(indexOf(attraction1), indexOf(attraction2), bus_route_info);
        cost_database.add(indexOf(attraction1), indexOf(attraction2), taxi_route_info);
    }
    
    private boolean updateLatLngDatabase(String attraction) {
        double latitude, longitude;
        boolean geocoder_success = true;
        String[] route_details = dbhandler.getRouteDetails(attraction, "");
        if (route_details[0] == null) {
            geocoder_success = updateLatLngDatabaseWithAPI(attraction);
        } else {
            latitude = Double.parseDouble(route_details[6]);
            longitude = Double.parseDouble(route_details[7]);
            latlng_database.put(indexOf(attraction), new LatLng(latitude, longitude));
        }
        return geocoder_success;
    }

    private boolean updateLatLngDatabaseWithAPI(String attraction) {
        double latitude, longitude;
        boolean geocoder_success = false;
        Geocoder geocoder = new Geocoder(context);
        List<Address> matched_list;
        try {
            matched_list = geocoder.getFromLocationName(attraction, 1);
            latitude = matched_list.get(0).getLatitude();
            longitude = matched_list.get(0).getLongitude();
            LatLng attraction_latlng = new LatLng(latitude, longitude);
            latlng_database.put(indexOf(attraction), attraction_latlng);
            putLatLngInSQL(attraction, attraction_latlng);
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

    private void putLatLngInSQL(String attraction, LatLng attraction_latlng) {
        DBRoute dbroute = new DBRoute(attraction, "", "", "", 0, 0, 0, attraction_latlng.latitude, attraction_latlng.longitude, "");
        dbhandler.addLocations(dbroute);
    }

    private void updateCostDatabaseWithAPI(String attraction1, String attraction2) {
        for (TransportMode mode : TransportMode.values()) {
            NodePairInfo info = new NodePairInfo(attraction1, attraction2, mode);
            places_to_be_routed.add(info);
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
            if (source.equals("singapore zoo"))
                start = new LatLng(1.407396, 103.783529);
            if (destination.equals("singapore zoo")) {
                end = new LatLng(1.407396, 103.783529);
            }

            Routing routing = new Routing.Builder()
                    .travelMode(mode.toAbstractRoutingTravelMode())
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .build();
            routing.execute();
        }
        else {
            appendRoutes();
        }
    }

    private void appendRoutes() {
        for (int i = 0; i != places_to_be_updated.size(); ++i) {
            NodePairInfo info = places_to_be_updated.get(i);
            Integer source = indexOf(info.getSource());
            Integer destination = indexOf(info.getDestination());
            RouteInfo route_info = info.getRouteInfo();
            // add in latlng infomations
            route_info.addEndLatLng(latlng_database.get(source));
            route_info.addEndLatLng(latlng_database.get(destination));
            cost_database.add(source, destination, route_info);
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

    public double costBetween(int source, int destination, TransportMode transport_mode) {
        return cost_database.costBetween(source, destination, transport_mode);
    }

    public int travelTimeOf(LinkedList<Integer> path) {
        int sum = 0;
        for (int i = 0; i != path.size() - 1; ++i) {
            sum += timeBetween(path.get(i), path.get(i + 1));
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

