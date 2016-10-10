package com.example.xtian.cecelectionvotingsystem.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xtian.cecelectionvotingsystem.R;

/**
 * Created by Xtian on 11/16/2015.
 */
public class HomeFragment extends android.support.v4.app.Fragment {

    public HomeFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);




        return rootView;
    }
}
