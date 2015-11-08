package com.example.liusu.travelapp.AdditionalFunction;

/**
 * Created by WSY on 8/11/15.
 */
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;



public class LocationManagerHelper implements LocationListener {


    public void onLocationChanged(Location loc) {}
    public void onProviderDisabled(String provider) { }
    public void onProviderEnabled(String provider) { }
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
