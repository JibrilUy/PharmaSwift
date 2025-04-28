package com.example.pharmaswift.navigation_bar;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pharmaswift.MainActivity;
import com.example.pharmaswift.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StoreFragment extends Fragment {

    private static final Logger log = LoggerFactory.getLogger(StoreFragment.class);
    private GridLayout storeLayout;
    private DatabaseHelper dbHelper;
    private ProductAdapter productAdapter;
    private List<Product> products;
    Button BTNClose;
    MainActivity mainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_store, container, false);

        mainActivity = (MainActivity) getActivity();
        BTNClose = v.findViewById(R.id.BTNclose);
        BTNClose.setOnClickListener(v1 -> {
            mainActivity.replaceFragment(new HomeFragment());
        });
        storeLayout = v.findViewById(R.id.storeLayout);

        dbHelper = new DatabaseHelper(getContext());
        products = dbHelper.getProductData();

        if (products == null) {
            // Handle the case where products is null, for example, initialize it as an empty list
            products = new ArrayList<>();
        }

        productAdapter = new ProductAdapter(getContext(), products);


        SearchView searchView = v.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("Searchbar", "Query Submitted");
                productAdapter.filter(query);
                displayProducts();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Searchbar", "Querying");
                productAdapter.filter(newText);
//                displayProducts();
                return true;
            }
        });



        displayProducts();



        return v;
    }

    public void displayProducts() {
        storeLayout.removeAllViews();  // Remove previous views

        // Get the screen width
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        // Calculate 50% of the screen width for each product item
        int width50Percent = screenWidth / 2;

        for (int i = 0; i < productAdapter.getCount(); i++) {
            Product product = (Product) productAdapter.getItem(i);

            // Inflate custom layout for each product (including image, name, price)
            View productView = LayoutInflater.from(getContext()).inflate(R.layout.product_item_layout, null);

            // Set width of the product item to 50% of screen width
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width50Percent, LinearLayout.LayoutParams.WRAP_CONTENT);
            productView.setLayoutParams(params);

            ImageView productImage = productView.findViewById(R.id.productImage);
            TextView productName = productView.findViewById(R.id.productName);
            TextView productPrice = productView.findViewById(R.id.productPrice);

            // Set the product data (name, price, image)
            productName.setText(product.getName());

            productPrice.setText(String.format("₱%.2f", product.getPrice()));

            productImage.setImageBitmap(product.getImage());
            productImage.setOnClickListener(v -> {
                String productName1 = product.getName();
                double productPrice1 = product.getPrice();
                Bitmap productImage1 = product.getImage();

                Log.d("ProductClicked", "Product Name: " + productName1);
                Log.d("ProductClicked", "Product Price: " + productPrice1);

                Bundle bundle = new Bundle();
                bundle.putString("productName", productName1);
                bundle.putDouble("productPrice", productPrice1);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                productImage1.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                bundle.putByteArray("productImage", byteArray);

                Log.d("ProductClicked", "Bundle created with Product Name: " + productName1 + ", Price: " + productPrice1 + ", Image byte array size: " + byteArray.length);

                if (productName1.toLowerCase().contains("prescription")) {
                    Log.d("ProductClicked", "This product is a prescription item: " + productName1);
                    VerificationFragment verificationFragment = new VerificationFragment();
                    verificationFragment.setArguments(bundle);  // Set the bundle to VerificationFragment
                    mainActivity.replaceFragment(verificationFragment);
                } else {
                    Log.d("ProductClicked", "This product is NOT a prescription item: " + productName1);
                    DeliveryFragment deliveryFragment = new DeliveryFragment();
                    deliveryFragment.setArguments(bundle);  // Set the bundle to fragment
                    mainActivity.replaceFragment(deliveryFragment);  // Use the same fragment you set bundle on
                }
            });

            // Add the product view to the GridLayout
            storeLayout.addView(productView);
        }
    }
}