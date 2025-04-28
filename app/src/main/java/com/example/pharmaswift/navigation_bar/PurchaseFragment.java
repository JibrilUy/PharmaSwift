package com.example.pharmaswift.navigation_bar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pharmaswift.MainActivity;
import com.example.pharmaswift.R;


public class PurchaseFragment extends Fragment {

    Button BTNOkay;
    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_purchase, container, false);

        BTNOkay = v.findViewById(R.id.BTNOkay);
        BTNOkay.setOnClickListener(v1 -> {
            mainActivity = (MainActivity) getActivity();
            mainActivity.replaceFragment(new HomeFragment());
        });








        return v;
    }
}