package com.example.liusu.travelapp.AdditionalFunction;

import android.app.NotificationManager;
import android.content.Intent;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Service;
import android.os.IBinder;

import com.example.liusu.travelapp.R;
import com.example.liusu.travelapp.UI.Tab2;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.DateFormat;
import java.util.Date;


public class CheckLocationBackground extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public String currentLatitude, currentLongitude;
    public Location myCurrentLocation;
    public String mLastUpdateTime;
    private final static int proximity = 20000;
    NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.cast_ic_notification_0)
                    .setContentTitle("You are here")
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0,1000,200,1000,200,1000}).setContentText("Reached destination!!");
    int mNotificationId = 1;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        Toast.makeText(this, "Start Checking Location In Background", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Location Checking Stopped", Toast.LENGTH_LONG).show();
        stopLocationUpdates();
    }

    @Override
    public void onStart(Intent intent, int start_id) {
        buildGoogleApiClient();
        createLocationRequest();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Location myLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (myLastLocation != null) {
            currentLatitude = String.valueOf(myLastLocation.getLatitude());
            currentLongitude = String.valueOf(myLastLocation.getLongitude());
        }
        startLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        myCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI(myCurrentLocation);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("g", "onConnectionFailed");
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void updateUI(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        double currentDistance = SphericalUtil.computeDistanceBetween(currentLocation, Tab2.locationOfNextDestination);
        Toast.makeText(getApplicationContext(), "Current distance is: " + currentDistance, Toast.LENGTH_SHORT).show();
        if (currentDistance < proximity){
            NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
            Toast.makeText(getApplicationContext(), "You are close to your destination", Toast.LENGTH_SHORT).show();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int a) {
        Log.d("t", "connection suspended");
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

}
