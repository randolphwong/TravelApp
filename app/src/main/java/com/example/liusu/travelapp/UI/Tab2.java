package com.example.liusu.travelapp.UI;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ProgressBar;

import com.example.liusu.travelapp.AdditionalFunction.CheckLocationBackground;
import com.example.liusu.travelapp.R;
import com.example.liusu.travelapp.functiontwo.Database;
import com.example.liusu.travelapp.functiontwo.EditDistance;
import com.example.liusu.travelapp.functiontwo.GetUrl;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Tab2 extends Fragment{
    View v;
    Context context;
    double latitude;
    double longitude;
    public static LatLng locationOfNextDestination;
    private  AutoCompleteTextView acTextView;
    ArrayAdapter<String> adapter;
    private Geocoder geocoder;
    private WebView wv1;
    private ProgressBar mProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        v = inflater.inflate(R.layout.tab_2,container,false);
        wv1 = (WebView) v.findViewById(R.id.webView);
        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.getSettings().setSupportMultipleWindows(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
		mProgressBar.setMax(100);

        wv1.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {			
                mProgressBar.setProgress(newProgress == 100 ? 0 : newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });

        wv1.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgressBar.setProgress(0);
            }
        });

        displayWeb("https://www.tripadvisor.com.sg");

        Database db = new Database();
        adapter = new ArrayAdapter<>(context,android.R.layout.select_dialog_singlechoice,db.getList());
        acTextView= (AutoCompleteTextView)v.findViewById(R.id.autoCompleteTextView);
        acTextView.setThreshold(1);
        acTextView.setAdapter(adapter);
        geocoder = new Geocoder(context);

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

        return v;
    }

    public void reachDestination() {
        getActivity().stopService(new Intent(getActivity(), CheckLocationBackground.class));
    }
    public void update() {
        String nextDestination = acTextView.getText().toString();
        acTextView.setText("");
        Database db = new Database();
        String attractionName = EditDistance.getResult(nextDestination,db.getData());
        displayWeb(GetUrl.getUrl(attractionName));
        setNextDestination(attractionName);
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

    public void displayWeb(String url){

        if(url != null){
            mProgressBar.setProgress(0);
            wv1.loadUrl(url);
        }else{
            Toast.makeText(context, "Null URL" ,Toast.LENGTH_SHORT).show();
        }

    }

}
