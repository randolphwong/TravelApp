package com.example.liusu.travelapp.sqldatabase;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

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

        for (int i = 0; i != latlng_string.length; ++i)
            latlng_array.add(new LatLng(lat_array.get(i), lng_array.get(i)));
        return latlng_array;
    }
}
