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

import com.example.liusu.travelapp.functionone.RouteInfo;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import com.example.liusu.travelapp.functionone.AttractionDatabase;
import com.example.liusu.travelapp.functionone.PathPlanner;
import com.example.liusu.travelapp.functiontwo.Database;
import com.example.liusu.travelapp.functiontwo.EditDistance;
import com.example.liusu.travelapp.R;


public class Tab1 extends Fragment {
    LatLng coord;
    String attraction;
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
        markers = new ArrayList<>();
        polylines = new ArrayList<>();
        attraction_database = new AttractionDatabase(getContext());

        m = (MapView) v.findViewById(R.id.mapview);
        m.onCreate(savedInstanceState);
        map = m.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());

        Button button_search = (Button) v.findViewById(R.id.search);
        button_search.setOnClickListener(new OnClickListener()
        {
             @Override
             public void onClick(View view) {
                 search(view);
             }
        }); 

        Button button_plot = (Button) v.findViewById(R.id.buttonPlot);
        button_plot.setOnClickListener(new OnClickListener()
        {
             @Override
             public void onClick(View view) {
                 onPlot(view);
             }
        }); 

        ToggleButton toggle_button_route = (ToggleButton) v.findViewById(R.id.toggleButton);
        toggle_button_route.setOnClickListener(new OnClickListener()
        {
             @Override
             public void onClick(View view) {
                onChangePlotLine(view);
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

    public void onMapReady() {
        map.addMarker(new MarkerOptions().position(coord).title(attraction));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 14));
    }

    public void search(View view) {

        Database base = new Database();
        CharSequence database[][] = base.getData();

        EditText et = (EditText) v.findViewById(R.id.editText);
        CharSequence inp = et.getText().toString().toLowerCase();

        String result = EditDistance.getResult(inp, database);

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

    public void onPlot(View view) {
        if (attraction_database.size() <= 1) {
            return;
        }
        if (attraction_database.isUpdated()) {
            removePolylines();
                Double budget = 0.0;

                try {
                    budget = Double.parseDouble(((EditText) v.findViewById(R.id.editText_budget)).getText().toString());
                }
                catch (Exception ex) {
                    Log.e("e", "budget is empty");
                }

                ArrayList<RouteInfo> path = PathPlanner.getPath(attraction_database.nameOf(0), budget, attraction_database);
                plotPath(path);
                Toast.makeText(getContext(), String.format("Journey time: %dmins\nJourney cost: $%f",
                        PathPlanner.durationOf(path, attraction_database), PathPlanner.costOf(path, attraction_database)), Toast.LENGTH_SHORT).show();
        }
        else {
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
}
