package com.example.liusu.travelapp.UI;

/**
 * Created by lx on 2015/11/8.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.liusu.travelapp.AdditionalFunction.StartJourneyActivity;
import com.example.liusu.travelapp.MainActivity;
import com.example.liusu.travelapp.MapActivity;
import com.example.liusu.travelapp.R;

public class Tab2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2,container,false);
        //Intent myIntent = new Intent((StartJourneyActivity)getActivity(), StartJourneyActivity.class);
        //startActivity(myIntent);

        //Intent myIntent = new Intent((MainActivity)getActivity(), StartJourneyActivity.class);
        //((MainActivity)getActivity()).startActivity(myIntent);

        //Intent myIntent = new Intent((MapActivity)getActivity(), StartJourneyActivity.class);
        //((MapActivity)getActivity()).startActivity(myIntent);
        return v;
    }
}
