package com.example.liusu.travelapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import com.example.liusu.travelapp.functionone.RouteInfo;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import com.example.liusu.travelapp.functionone.AttractionDatabase;
import com.example.liusu.travelapp.functionone.PathPlanner;

public class MapActivity extends AppCompatActivity {
    LatLng coord;
    String attraction;
    GoogleMap map;
    ArrayList<Marker> markers;
    ArrayList<Polyline> polylines;
    AttractionDatabase attraction_database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        markers = new ArrayList<>();
        polylines = new ArrayList<>();
        attraction_database = new AttractionDatabase(getApplicationContext());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();
    }

    public void onMapReady() {
        map.addMarker(new MarkerOptions().position(coord).title(attraction));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 14));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    public void search(View view) {

        Database base = new Database();
        CharSequence database[][] = base.getData();

        EditText et = (EditText) findViewById(R.id.editText);
        CharSequence inp = et.getText().toString();

        String result = getResult(inp, database);
        //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();

        attraction_database.add(result);

        putMarkers();
    }

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
        if (attraction_database.size() > 0) {
            removePolylines();

            if (attraction_database.size() > 1 && attraction_database.isUpdated()) {
                Double budget = 0.0;

                try {
                    budget = Double.parseDouble(((EditText) findViewById(R.id.editTextBudget)).getText().toString());
                }
                catch (Exception ex) {
                    Log.e("e", ex.getMessage());
                }

                ArrayList<RouteInfo> path = PathPlanner.getPath(attraction_database.nameOf(0), budget, attraction_database);

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
                    polyOptions.width(10);
                    polyOptions.addAll(route_info.getRoute().getPoints());
                    Polyline polyline = map.addPolyline(polyOptions);
                    polylines.add(polyline);
                }
                Toast.makeText(getApplicationContext(), String.format("Journey time: %dmins\nJourney cost: $%f",
                        PathPlanner.durationOf(path, attraction_database), PathPlanner.costOf(path, attraction_database)), Toast.LENGTH_SHORT).show();
            }
            else
                Log.i("i", "attraction database not yet ready.");
        }
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
