package com.example.pharmaswift.navigation_bar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pharmaswift.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> productList;
    private List<Product> filteredProductList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.filteredProductList = new ArrayList<>(productList);
    }

    @Override
    public int getCount() {
        return filteredProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = filteredProductList.get(position);

        // Inflate your item layout for the product
        // and set the product data (name, price, and image) on the UI elements
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.product_item_layout, parent, false);
        }

        ImageView productImage = convertView.findViewById(R.id.productImage);
        TextView productName = convertView.findViewById(R.id.productName);
        TextView productPrice = convertView.findViewById(R.id.productPrice);

        // Set product data (e.g., name, price, image)
        productName.setText(product.getName());
        productPrice.setText(String.format("₱%.2f", product.getPrice()));
        productImage.setImageBitmap(product.getImage()); // If image is a Bitmap

        return convertView;
    }

    // Method to filter products based on search query
    public void filter(String query) {
        filteredProductList.clear();
        if (query.isEmpty()) {
            filteredProductList.addAll(productList);  // If query is empty, show all products
        } else {
            for (Product product : productList) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredProductList.add(product);  // Add matching product
                }
            }
        }
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }
}

