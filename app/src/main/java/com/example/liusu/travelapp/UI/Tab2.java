package com.example.liusu.travelapp.UI;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.location.Address;
import android.location.Geocoder;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.liusu.travelapp.AdditionalFunction.CheckLocationBackground;
import com.example.liusu.travelapp.functiontwo.Database;
import com.example.liusu.travelapp.functiontwo.EditDistance;
import com.example.liusu.travelapp.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Tab2 extends Fragment{
    View v;
    Context context;
    double latitude;
    double longitude;
    public static LatLng locationOfNextDestination;
    private  AutoCompleteTextView acTextView;
    private TextView displayDestination;
    ArrayAdapter<String> adapter;
    private Geocoder geocoder;
    private WebView wv1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        v = inflater.inflate(R.layout.tab_2,container,false);
        Database db = new Database();
        adapter = new ArrayAdapter<>(context,android.R.layout.select_dialog_singlechoice,db.getList());
        acTextView= (AutoCompleteTextView)v.findViewById(R.id.autoCompleteTextView);
        acTextView.setThreshold(1);
        acTextView.setAdapter(adapter);
        displayDestination = (TextView) v.findViewById(R.id.display);
        geocoder = new Geocoder(context);

        // webview
        wv1 = (WebView) v.findViewById(R.id.webView);
        wv1.setWebViewClient(new MyBrowser());

        Button update = (Button) v.findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                update();
            }
        });

        Button stop = (Button) v.findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reachDestination();
            }
        });

        Button search = (Button) v.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wv1.getSettings().setLoadsImagesAutomatically(true);
                wv1.getSettings().setJavaScriptEnabled(true);
                wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                wv1.loadUrl("https://www.tripadvisor.com.sg/Tourism-g294264-Sentosa_Island-Vacations.html");
            }
        });
        return v;
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public void reachDestination() {
        getActivity().stopService(new Intent(getActivity(), CheckLocationBackground.class));
    }
    public void update() {
        String nextDestination = acTextView.getText().toString();
        Database db = new Database();
        String attractionName = EditDistance.getResult(nextDestination,db.getData());
        setNextDestination(attractionName);
        displayDestination.setText(locationOfNextDestination.toString());
        getActivity().startService(new Intent(getActivity(), CheckLocationBackground.class));
    }
    public void setNextDestination(String attraction){

        List<Address> matched_list;
        try {
            matched_list = geocoder.getFromLocationName(attraction, 1);
            latitude = matched_list.get(0).getLatitude();
            longitude = matched_list.get(0).getLongitude();
            locationOfNextDestination = new LatLng(latitude, longitude);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
