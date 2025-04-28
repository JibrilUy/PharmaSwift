package com.example.pharmaswift.navigation_bar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pharmaswift.R;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<Model> mList;
    Context context;

    public MyAdapter(Context context, ArrayList<Model>mList){

        this.mList = mList;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.transaction_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Model model = mList.get(position);
        holder.textViewProductNameTransaction.setText(model.getProductName());
        holder.textViewDateTransaction.setText(model.getTimestamp());
        holder.textViewProductPrice.setText(String.format("₱%.2f", model.getProductPrice()));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{


        TextView textViewProductNameTransaction, textViewDateTransaction, textViewProductPrice;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            textViewProductNameTransaction = itemView.findViewById(R.id.textViewProductNameTransaction);
            textViewDateTransaction = itemView.findViewById(R.id.textViewDateTransaction);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);

        }
    }
}