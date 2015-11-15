package com.example.liusu.travelapp.sqldatabase;

import com.example.liusu.travelapp.functionone.TransportMode;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.EnumMap;

public class LatLngParser {
    public static ArrayList<Double> stringToDoubleArray(String double_string) {
        ArrayList<Double> double_array = new ArrayList<>();
        String[] split_string = double_string.substring(1, double_string.length() - 1).split(", ");
        for (String s : split_string)
            double_array.add(Double.parseDouble(s));
        return double_array;
    }

    public static String doubleArrayToString(ArrayList<Double> double_array) {
        return double_array.toString();
    }

    public static String[] latLngToString(ArrayList<LatLng> latlng_array) {
        ArrayList<Double> lat_array = new ArrayList<>();
        ArrayList<Double> lng_array = new ArrayList<>();
        for (int i = 0; i != latlng_array.size(); ++i) {
            lat_array.add(latlng_array.get(i).latitude);
            lng_array.add(latlng_array.get(i).longitude);
        }

        String[] latlng_string = new String[2];
        latlng_string[0] = LatLngParser.doubleArrayToString(lat_array);
        latlng_string[1] = LatLngParser.doubleArrayToString(lng_array);
        return latlng_string;
    }

    public static ArrayList<LatLng> stringToLatLng(String[] latlng_string) {
        ArrayList<LatLng> latlng_array = new ArrayList<>();
        // convert string array to double array
        ArrayList<Double> lat_array = LatLngParser.stringToDoubleArray(latlng_string[0]);
        ArrayList<Double> lng_array = LatLngParser.stringToDoubleArray(latlng_string[1]);

        for (int i = 0; i != lat_array.size(); ++i)
            latlng_array.add(new LatLng(lat_array.get(i), lng_array.get(i)));
        return latlng_array;
    }

    public static EnumMap<TransportMode, ArrayList<LatLng>> stringToLatLngForAllMode(String[] latlng_string) {
        EnumMap<TransportMode, ArrayList<LatLng>> all_latlngs = new EnumMap<>(TransportMode.class);
        // split lat_string and lng_string into their respective transport mode
        String[] lat_string_split_by_transport_mode = latlng_string[0].split(":");
        //Log.i("i", "lat string split result:");
        //for (String s : lat_string_split_by_transport_mode)
            //Log.i("i", s);
        String[] lng_string_split_by_transport_mode = latlng_string[1].split(":");
        // for each lat_string and lng_string of the respective transport mode, convert to double array
        ArrayList<Double> lat_array_foot;
        ArrayList<Double> lat_array_bus;
        ArrayList<Double> lat_array_taxi;
        ArrayList<Double> lng_array_foot;
        ArrayList<Double> lng_array_bus;
        ArrayList<Double> lng_array_taxi;

        lat_array_foot = LatLngParser.stringToDoubleArray(lat_string_split_by_transport_mode[0]);
        lat_array_bus = LatLngParser.stringToDoubleArray(lat_string_split_by_transport_mode[1]);
        lat_array_taxi = LatLngParser.stringToDoubleArray(lat_string_split_by_transport_mode[2]);

        lng_array_foot = LatLngParser.stringToDoubleArray(lng_string_split_by_transport_mode[0]);
        lng_array_bus = LatLngParser.stringToDoubleArray(lng_string_split_by_transport_mode[1]);
        lng_array_taxi = LatLngParser.stringToDoubleArray(lng_string_split_by_transport_mode[2]);

        ArrayList<LatLng> latlng_array_for_foot = new ArrayList<>();
        ArrayList<LatLng> latlng_array_for_bus = new ArrayList<>();
        ArrayList<LatLng> latlng_array_for_taxi = new ArrayList<>();

        for (int i = 0; i != lat_array_foot.size(); ++i)
            latlng_array_for_foot.add(new LatLng(lat_array_foot.get(i), lng_array_foot.get(i)));
        for (int i = 0; i != lat_array_bus.size(); ++i)
            latlng_array_for_bus.add(new LatLng(lat_array_bus.get(i), lng_array_bus.get(i)));
        for (int i = 0; i != lat_array_taxi.size(); ++i)
            latlng_array_for_taxi.add(new LatLng(lat_array_taxi.get(i), lng_array_taxi.get(i)));

        all_latlngs.put(TransportMode.FOOT, latlng_array_for_foot);
        all_latlngs.put(TransportMode.BUS, latlng_array_for_bus);
        all_latlngs.put(TransportMode.TAXI, latlng_array_for_taxi);
        
        return all_latlngs;
    }

    public static String[] latLngToStringForAllMode(EnumMap<TransportMode, ArrayList<LatLng>> all_latlngs) {
        String lat_string;
        String lng_string;
        String[] latlng_string_foot = LatLngParser.latLngToString(all_latlngs.get(TransportMode.FOOT));
        String[] latlng_string_bus = LatLngParser.latLngToString(all_latlngs.get(TransportMode.BUS));
        String[] latlng_string_taxi = LatLngParser.latLngToString(all_latlngs.get(TransportMode.TAXI));
        lat_string = latlng_string_foot[0] + ":" + latlng_string_bus[0] + ":" + latlng_string_taxi[0];
        lng_string = latlng_string_foot[1] + ":" + latlng_string_bus[1] + ":" + latlng_string_taxi[1];
        return new String[]{lat_string, lng_string};
    }
}
