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
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.liusu.travelapp.Database;
import com.example.liusu.travelapp.MainActivity;
import com.example.liusu.travelapp.R;


public class Tab1 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_1, container, false);
        Intent myIntent = new Intent((MainActivity)getActivity(), MainActivity.class);
        //(MainActivity)getActivity().
        startActivity(myIntent);
        return v;
    }

}