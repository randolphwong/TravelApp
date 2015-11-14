package com.example.liusu.travelapp.UI;

/**
 * Created by lx on 2015/11/8.
 */
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Button;

import java.util.ArrayList;

import com.example.liusu.travelapp.Database;
import com.example.liusu.travelapp.MainActivity;
import com.example.liusu.travelapp.MapActivity;
import com.example.liusu.travelapp.R;

import com.example.liusu.travelapp.functionone.RouteInfo;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;

import com.example.liusu.travelapp.functionone.AttractionDatabase;
import com.example.liusu.travelapp.functionone.PathPlanner;


public class Tab1 extends Fragment {
    LatLng coord;
    String attraction;
    //public static GoogleMap map;
    MapView m;
    GoogleMap map;
    ArrayList<Marker> markers;
    ArrayList<Polyline> polylines;
    AttractionDatabase attraction_database;
    boolean plot_straight_route = false;
    View v;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tab_1, container, false);
        //Intent myIntent = new Intent((MainActivity)getActivity(), MainActivity.class);
        //(MainActivity)getActivity().
        //startActivity(myIntent);
        //Intent myIntent = new Intent((MainActivity)getActivity(), MapActivity.class);
        //((MainActivity)getActivity()).startActivity(myIntent);
        markers = new ArrayList<>();
        polylines = new ArrayList<>();
        attraction_database = new AttractionDatabase(getContext());

        m = (MapView) v.findViewById(R.id.mapview);
        m.onCreate(savedInstanceState);
        map = m.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);

        Button button = (Button) v.findViewById(R.id.search);
        button.setOnClickListener(new OnClickListener()
        {
                  @Override
                  public void onClick(View view) {
              
                      Database base = new Database();
                      CharSequence database[][] = base.getData();
              
                      EditText et = (EditText) v.findViewById(R.id.editText);
                      CharSequence inp = et.getText().toString().toLowerCase();
              
                      String result = getResult(inp, database);
                      //Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
              
                      if (attraction_database.isUpdated()) {
                          et.setText("");
                          if (!attraction_database.contains(result)) {
                              attraction_database.add(result);
                              putMarkers();
                              if (attraction_database.isUpdated())
                                  onPlot(v.findViewById(R.id.buttonPlot));
                          }
                      }
                      else
                          Toast.makeText(getContext(), "Still downloading route information.", Toast.LENGTH_SHORT).show();
                  }
        }); 
        return v;
    }

    @Override
    public void onResume() {
        m.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        m.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        m.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        m.onLowMemory();
    }

    /*
     *@Override
     *public void onCreate(Bundle savedInstanceState) {
     *    super.onCreate(savedInstanceState);
     *    markers = new ArrayList<>();
     *    polylines = new ArrayList<>();
     *    attraction_database = new AttractionDatabase(getContext());
     *}
     */

    /*
     *@Override
     *public void onStart() {
     *    super.onStart();
     *    try {
     *        //SupportMapFragment mapFragment =
     *                //(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
     *        map = ((SupportMapFragment) MainActivity.fragmentManager
     *            .findFragmentById(R.id.map)).getMap();
     *        map = mapFragment.getMap();
     *        map.setMyLocationEnabled(true);
     *    }
     *    catch (NullPointerException e) {
     *        Toast.makeText(getContext(), "Google Play services is not available.", Toast.LENGTH_SHORT).show();
     *        Log.e("e", "null pointer");
     *    }
     *    catch (Exception e) {
     *        Log.e("e", "Some other exception");
     *    }
     *}
     */


    public void onMapReady() {
        map.addMarker(new MarkerOptions().position(coord).title(attraction));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 14));
    }

    public static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static int computeLevenshteinDistance(CharSequence lhs, CharSequence rhs) {
        int[][] distance = new int[lhs.length() + 1][rhs.length() + 1];

        for (int i = 0; i <= lhs.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= rhs.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= lhs.length(); i++)
            for (int j = 1; j <= rhs.length(); j++)
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1));

        return distance[lhs.length()][rhs.length()];
    }

/*
 *    public void search(View view) {
 *
 *        Database base = new Database();
 *        CharSequence database[][] = base.getData();
 *
 *        EditText et = (EditText) v.findViewById(R.id.editText);
 *        CharSequence inp = et.getText().toString().toLowerCase();
 *
 *        String result = getResult(inp, database);
 *        //Toast.makeText(getContext(), result, Toast.LENGTH_SHORT).show();
 *
 *        if (attraction_database.isUpdated()) {
 *            et.setText("");
 *            if (!attraction_database.contains(result)) {
 *                attraction_database.add(result);
 *                putMarkers();
 *                if (attraction_database.isUpdated())
 *                    onPlot(v.findViewById(R.id.buttonPlot));
 *            }
 *        }
 *        else
 *            Toast.makeText(getContext(), "Still downloading route information.", Toast.LENGTH_SHORT).show();
 *    }
 */

    public void putMarkers() {
        removeMarkers();

        if (attraction_database.size() > 0) {
            LatLng latlng_of_attraction = null;
            String name_of_attraction;

            for (int i = 0; i != attraction_database.size(); ++i) {
                latlng_of_attraction = attraction_database.latLngOf(i);
                name_of_attraction = attraction_database.nameOf(i);
                markers.add(map.addMarker(new MarkerOptions().position(latlng_of_attraction).title(name_of_attraction)));
            }

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng_of_attraction, 12));
        }
    }

    public void removeMarkers() {
        for (int i = 0; i != markers.size(); ++i) {
            markers.get(i).remove();
        }
        markers.clear();
    }

    public void onPlot(View v) {
        if (attraction_database.size() <= 1) {
            return;
        }
        if (attraction_database.isUpdated()) {
            removePolylines();
                Double budget = 0.0;

                try {
                    budget = Double.parseDouble(((EditText) v.findViewById(R.id.editTextBudget)).getText().toString());
                }
                catch (Exception ex) {
                    Log.e("e", ex.getMessage());
                }

                ArrayList<RouteInfo> path = PathPlanner.getPath(attraction_database.nameOf(0), budget, attraction_database);
                plotPath(path);
                Toast.makeText(getContext(), String.format("Journey time: %dmins\nJourney cost: $%f",
                        PathPlanner.durationOf(path, attraction_database), PathPlanner.costOf(path, attraction_database)), Toast.LENGTH_SHORT).show();
        }
        else {
            Log.i("i", "attraction database not yet ready.");
            Toast.makeText(getContext(), "Still downloading route information.", Toast.LENGTH_SHORT).show();
        }
    }

    public void plotPath(ArrayList<RouteInfo> path) {

        for (int i = 0; i != path.size(); ++i) {
            RouteInfo route_info = path.get(i);
            PolylineOptions polyOptions = new PolylineOptions();
            switch (route_info.getTransportMode()) {
                case TAXI:
                    polyOptions.color(Color.RED);
                    break;
                case BUS:
                    polyOptions.color(Color.BLUE);
                    break;
                case FOOT:
                    polyOptions.color(Color.MAGENTA);
                    break;
            }
            if (i != 0)
                polyOptions.width(8);
            else
                polyOptions.width(15);

            if (plot_straight_route)
                polyOptions.addAll(route_info.getEndPoints());
            else
                polyOptions.addAll(route_info.getPoints());

                Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);
        }
    }

    public void onChangePlotLine(View view) {
        plot_straight_route = ((ToggleButton) view).isChecked();
        onPlot(v.findViewById(R.id.buttonPlot));
    }

    public void removePolylines() {
        for (int i = 0; i != polylines.size(); ++i) {
            polylines.get(i).remove();
        }
        polylines.clear();
    }

    public String getResult(CharSequence input, CharSequence[][] database){
        String result = "";
        int currentEditDistance = input.toString().length();
        int currentRow = 0;
        for(int i = 0 ; i < database.length ; i++){
            for(int j = 0 ; j < database[i].length ; j++){
                if(computeLevenshteinDistance(input,database[i][j]) < currentEditDistance){
                    currentEditDistance = computeLevenshteinDistance(input,database[i][j]);
                    result = database[i][j].toString();
                    currentRow = i;
                }
            }
        }
        return database[currentRow][database[currentRow].length-1].toString();
    }
}
