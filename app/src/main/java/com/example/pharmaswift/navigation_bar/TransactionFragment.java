package com.example.pharmaswift.navigation_bar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.example.pharmaswift.R;

import java.util.ArrayList;


public class TransactionFragment extends Fragment {

    RecyclerView recycler_view;
    DatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction, container, false);
        dbHelper = new DatabaseHelper(getContext());

        MyAdapter adapter;
        ArrayList<Model> list;

        recycler_view = v.findViewById(R.id.recycler_view);

        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<>();
        adapter = new MyAdapter(getActivity(), list);

        recycler_view.setAdapter(adapter);

        list.clear();
        list.addAll(dbHelper.getAllTransactions());

        // Notify the adapter that data has changed
        adapter.notifyDataSetChanged();

        return v;
    }
}