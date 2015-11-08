package com.example.liusu.travelapp.AdditionalFunction;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class StartJourneyActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public String currentLatitude, currentLongitude;
    public Location myCurrentLocation;
    public String mLastUpdateTime;
    public LatLng testSentosa = new LatLng(1.24940, 103.83032);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_journey);
//        new PostTask().execute(5);
        buildGoogleApiClient();
        createLocationRequest();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_async_task_test, menu);
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

    @Override
    public void onConnected(Bundle connectionHint) {
        Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
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
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .setAccountName("liusumy@gmail.com")
                .build();
        mGoogleApiClient.connect();
    }

    private void updateUI(Location location) {
        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        double currentDistance = SphericalUtil.computeDistanceBetween(currentLocation, testSentosa);
        Toast.makeText(getApplicationContext(), "Current distance is: " + currentDistance, Toast.LENGTH_SHORT).show();
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
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
    public void update(View view) {
        EditText firstPart = (EditText) findViewById(R.id.firstpart);
        EditText secondPart = (EditText) findViewById(R.id.secondpart);
        String first = firstPart.getText().toString();
        String second = secondPart.getText().toString();
        TextView display = (TextView) findViewById(R.id.display);
        display.setText(first + second);
        onResume();
    }
}
