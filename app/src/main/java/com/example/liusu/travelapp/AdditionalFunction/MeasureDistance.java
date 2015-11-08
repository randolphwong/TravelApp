package com.example.liusu.travelapp.AdditionalFunction;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;


/**
 * Created by WSY on 7/11/15.
 *
 * Possible links to resolve the user permission problem: http://stackoverflow.com/questions/32491960/android-check-permission-for-locationmanager
 *
 */

public class MeasureDistance extends Activity {
    Context mContext;
    public final static double AVERAGE_RADIUS_OF_EARTH = 6371000; //6371km, in meters here

    public MeasureDistance(Context mContext){
        this.mContext = mContext;
    }


    public int getDistance(double longitude, double latitude){
        Location currentLocation = getCurrentLocation();
        double currentLgt = currentLocation.getLongitude();
        double currentLtt = currentLocation.getLatitude();
        int distance  = calculateDistance(currentLgt,currentLtt,longitude,latitude);
        return distance;
    }

    public Location getCurrentLocation(){

        LocationManager locationManager =
                (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        LocationManagerHelper lmh = new LocationManagerHelper();

        String mlocProvider;
        Criteria hdCrit = new Criteria();

        hdCrit.setAccuracy(Criteria.ACCURACY_COARSE);

        mlocProvider = locationManager.getBestProvider(hdCrit, true);

        //Context object required, so change this to mContext -siyuan
        if ( ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},1 ); //requestCode
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1000, lmh);
        Location currentLocation = locationManager.getLastKnownLocation(mlocProvider);
        locationManager.removeUpdates(lmh);

        return currentLocation;

    }

    public int calculateDistance(double userLat, double userLng,
                                 double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (Math.round(AVERAGE_RADIUS_OF_EARTH * c));
    }

}
