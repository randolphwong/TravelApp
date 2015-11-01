package com.example.liusu.travelapp.functionone;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class FileDatabase {
    private Context context;
    private String file_name;

    private ArrayList<String> name_database;
    private HashMap<Integer, LatLng> latlng_database;
    private CostDatabase cost_database;

    public FileDatabase() {
    }

    public FileDatabase(Context context, String file_name) {
        this.context = context;
        this.file_name = file_name;

        initialiseDatabaseFile();
    }

    public void initialiseDatabaseFile() {
        try {
            InputStreamReader streamReader = new InputStreamReader(context.openFileInput(file_name));
            BufferedReader reader = new BufferedReader(streamReader);
        }
        catch (FileNotFoundException ex) {
            Log.e("e", ex.getMessage());
            createDatabaseFile();
        }
    }

    public void createDatabaseFile() {
        try {
            OutputStreamWriter streamWriter = new OutputStreamWriter(context.openFileOutput(file_name, context.MODE_PRIVATE));
            streamWriter.close();

        }
        catch (FileNotFoundException ex) {
            Log.e("e", ex.getMessage());
        }
        catch (IOException ex) {
            Log.e("e", ex.getMessage());
        }
    }

    public void update(AttractionDatabase attraction_database, ArrayList<String> name_database, CostDatabase cost_database, HashMap<Integer, LatLng> latlng_database) {
        ArrayList<String> name_to_update = new ArrayList<>();
        ArrayList<String> latlng_to_update = new ArrayList<>();
    }
}