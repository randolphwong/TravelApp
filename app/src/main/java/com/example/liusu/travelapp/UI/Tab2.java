package com.example.liusu.travelapp.UI;

/**
 * Created by lx on 2015/11/8.
 */
import android.app.NotificationManager;
import android.location.Location;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.liusu.travelapp.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.DateFormat;
import java.util.Date;

public class Tab2 extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public String currentLatitude, currentLongitude;
    public Location myCurrentLocation;
    public String mLastUpdateTime;
    public LatLng destination = new LatLng(1.24940, 103.83032); //test: sentosa
    private final static int proximity = 20000;
    NotificationCompat.Builder mBuilder;
    View v;
    Context context = getContext();


    // Sets an ID for the notification
    int mNotificationId = 001;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tab_2,container,false);
        buildGoogleApiClient();
        createLocationRequest();
        return v;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .setAccountName("liusumy@gmail.com")
                .build();
        mGoogleApiClient.connect();
    }

    private void updateUI(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        double currentDistance = SphericalUtil.computeDistanceBetween(currentLocation, destination);
        Toast.makeText(getContext(), "Current distance is: " + currentDistance, Toast.LENGTH_SHORT).show();
        if (currentDistance<proximity){
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
// Builds the notification and issues it.

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getContext())
                            .setSmallIcon(R.drawable.cast_ic_notification_0)
                            .setContentTitle("You are here")
                            .setAutoCancel(true)
                            .setVibrate(new long[]{0,1000,200,1000,200,1000}).setContentText("Reached destination!!");
            mNotifyMgr.notify(mNotificationId, mBuilder.build());


            Toast.makeText(getContext(), "Entered proximity range!", Toast.LENGTH_SHORT).show();

        }

    }

    public void reachDestination(View view) {
        onPause();
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
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    public void update(View view) {
        EditText firstPart = (EditText) v.findViewById(R.id.firstpart);
        EditText secondPart = (EditText) v.findViewById(R.id.secondpart);
        String first = firstPart.getText().toString();
        String second = secondPart.getText().toString();
        TextView display = (TextView) v.findViewById(R.id.display);
        display.setText(first + second);
        onResume();

    }
}
