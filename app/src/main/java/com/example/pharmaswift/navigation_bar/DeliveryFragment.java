package com.example.pharmaswift.navigation_bar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharmaswift.MainActivity;
import com.example.pharmaswift.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.NumberFormat;
import java.util.Locale;


public class DeliveryFragment extends Fragment implements OnMapReadyCallback {

    Button BTNSameDayDelivery, BTNPickUp,BTNScheduled,BTNCOD, BTNCreditCard, BTNPoints;
    LinearLayout BTNLayoutDelivery,BTNLayoutDeliveryPayment, productLayout, deliveryAddressLayout;
    TextView textViewDeliveryPayment, productNameTextView, productPriceTextView, userBalancePayment, userPointsPayment;
    ImageView productImageView;
    FrameLayout map_container_delivery;
    DatabaseHelper dbHelper;
    MainActivity mainActivity;
    EditText regionEditText, provinceEditText, cityEditText, barangayEditText, firstNameEditText, lastNameEditText, contactInfoEditText;

    Boolean areFieldsFilled, mapMarkerSelected;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_delivery, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.id_map_delivery);
        mapFragment.getMapAsync(this);



        BTNSameDayDelivery = v.findViewById(R.id.BTNSameDayDelivery);
        BTNPickUp = v.findViewById(R.id.BTNPickUp);
        BTNScheduled = v.findViewById(R.id.BTNScheduled);
        BTNCOD = v.findViewById(R.id.BTNCOD);
        BTNCreditCard = v.findViewById(R.id.BTNCreditCard);
        BTNPoints = v.findViewById(R.id.BTNPoints);

        BTNLayoutDelivery = v.findViewById(R.id.BTNLayoutDelivery);
        BTNLayoutDeliveryPayment = v.findViewById(R.id.BTNLayoutDeliveryPayment);
        productLayout = v.findViewById(R.id.productLayout);
        deliveryAddressLayout = v.findViewById(R.id.deliveryAddressLayout);

        textViewDeliveryPayment = v.findViewById(R.id.textViewDeliveryPayment);
        userPointsPayment = v.findViewById(R.id.userPointsPayment);
        userBalancePayment = v.findViewById(R.id.userBalancePayment);

        map_container_delivery = v.findViewById(R.id.map_container_delivery);


        productNameTextView = v.findViewById(R.id.productNameTextView);
        productPriceTextView = v.findViewById(R.id.productPriceTextView);
        productImageView = v.findViewById(R.id.productImageView);

        dbHelper = new DatabaseHelper(getContext());


        regionEditText = v.findViewById(R.id.region_edit_text);
        provinceEditText = v.findViewById(R.id.province_edit_text);
        cityEditText = v.findViewById(R.id.city_edit_text);
        barangayEditText = v.findViewById(R.id.barangay_edit_text);
        firstNameEditText = v.findViewById(R.id.first_name_edit_text);
        lastNameEditText = v.findViewById(R.id.last_name_edit_text);
        contactInfoEditText = v.findViewById(R.id.contact_info_edit_text);

        mapMarkerSelected = false;








        BTNScheduled.setOnClickListener(v1 -> {
            Toast.makeText(getContext(), "Currently Unavailable", Toast.LENGTH_SHORT).show();
        });

        BTNSameDayDelivery.setOnClickListener(v1 -> {
            deliveryAddressLayout.setVisibility(View.VISIBLE);
            map_container_delivery.setVisibility(View.GONE);
            textViewDeliveryPayment.setText("Input Address");
            showPaymentButton();

        });

        BTNCOD.setOnClickListener(v1 -> {
            Toast.makeText(getContext(), "Currently Unavailable", Toast.LENGTH_SHORT).show();
        });

        BTNCreditCard.setOnClickListener(v1 -> {
            if (mapMarkerSelected||areFieldsFilled()) { // Call the method here
                getUserBalanceAndProductData();
                mainActivity = (MainActivity) getActivity();
                mainActivity.replaceFragment(new PurchaseFragment());
            }
        });

        BTNPoints.setOnClickListener(v1 -> {
            if (mapMarkerSelected||areFieldsFilled()) { // Call the method here
                getUserPointsAndProductData();
                mainActivity = (MainActivity) getActivity();
                mainActivity.replaceFragment(new PurchaseFragment());
            }
        });



        BTNLayoutDelivery.setVisibility(View.VISIBLE);

        BTNPickUp.setOnClickListener(v1 -> {
            map_container_delivery.setVisibility(View.VISIBLE);
            BTNLayoutDelivery.setVisibility(View.GONE);
            textViewDeliveryPayment.setText("Choose Pharmacy");

        });











        return v;
    }



    public void getProductData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.d("getProductData", "Bundle received!");

            String productName = bundle.getString("productName");
            double productPrice = bundle.getDouble("productPrice");
            byte[] byteArray = bundle.getByteArray("productImage");

            Log.d("getProductData", "Product Name: " + productName);
            Log.d("getProductData", "Product Price: " + productPrice);
            Log.d("getProductData", "Image ByteArray is null? " + (byteArray == null));

            if (productName != null && byteArray != null) {
                Bitmap productBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                productNameTextView.setText(productName);
                productPriceTextView.setText(String.format("₱%.2f", productPrice));
                productImageView.setImageBitmap(productBitmap);
            } else {
                Log.e("getProductData", "Missing productName or image data!");
            }
        } else {
            Log.e("getProductData", "No bundle received!");
        }
    }

    public void getUserBalance(){
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        double[] userBalance = dbHelper.getBalance();

        double balance = userBalance[0];
        double points = userBalance[1];

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2); // Ensures two decimal places
        formatter.setMaximumFractionDigits(2); // Ensures two decimal places

        String formattedBalance = formatter.format(balance);
        String formattedPoints = formatter.format(points);

        userBalancePayment.setText(formattedBalance);
        userPointsPayment.setText(formattedPoints);
    }

    //Im lazy and its 3 hours before deadline to make this cleaner with methods
    public void getUserBalanceAndProductData() {

        double[] userBalance = dbHelper.getBalance();
        double balance = userBalance[0];
        double points = userBalance[1];

        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.d("getUserBalanceAndProductData", "Bundle received!");

            String productName = bundle.getString("productName");
            double productPrice = bundle.getDouble("productPrice");
            byte[] byteArray = bundle.getByteArray("productImage");

            Log.d("getUserBalanceAndProductData", "Product Name: " + productName);
            Log.d("getUserBalanceAndProductData", "Product Price: " + productPrice);
            Log.d("getUserBalanceAndProductData", "Image ByteArray is null? " + (byteArray == null));

            if (productName != null && byteArray != null) {
                Bitmap productBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                productNameTextView.setText(productName);
                productPriceTextView.setText(String.format("₱%.2f", productPrice));
                productImageView.setImageBitmap(productBitmap);

                balance -= productPrice;
                if (balance < 0) {
                    balance = 0; // Prevent negative balance
                }
                Log.d("getUserBalanceAndProductData", "New balance after purchase: " + balance);

                boolean updateSuccess = dbHelper.updateBalance(balance, points);

                if (updateSuccess) {
                    dbHelper.insertTransaction(productName,productPrice);
                    Log.d("getUserBalanceAndProductData", "Balance successfully updated in the database!");
                } else {
                    Log.e("getUserBalanceAndProductData", "Failed to update balance in the database!");
                }
            } else {
                Log.e("getUserBalanceAndProductData", "Missing productName or image data!");
            }
        } else {
            Log.e("getUserBalanceAndProductData", "No bundle received!");
        }

        // Format and update the TextViews
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        String formattedBalance = formatter.format(balance);
        String formattedPoints = formatter.format(points);

        userBalancePayment.setText(formattedBalance);
        userPointsPayment.setText(formattedPoints);
    }

    public void getUserPointsAndProductData() {

        double[] userBalance = dbHelper.getBalance();
        double balance = userBalance[0];
        double points = userBalance[1];

        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.d("getUserBalanceAndProductData", "Bundle received!");

            String productName = bundle.getString("productName");
            double productPrice = bundle.getDouble("productPrice");
            byte[] byteArray = bundle.getByteArray("productImage");

            Log.d("getUserBalanceAndProductData", "Product Name: " + productName);
            Log.d("getUserBalanceAndProductData", "Product Price: " + productPrice);
            Log.d("getUserBalanceAndProductData", "Image ByteArray is null? " + (byteArray == null));

            if (productName != null && byteArray != null) {
                Bitmap productBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                productNameTextView.setText(productName);
                productPriceTextView.setText(String.format("₱%.2f", productPrice));
                productImageView.setImageBitmap(productBitmap);

                // Check if the user has enough points to purchase the product
                if (points >= productPrice) {
                    // Subtract the product price from points
                    points -= productPrice;
                    Log.d("getUserBalanceAndProductData", "New points balance after purchase: " + points);

                    // Update the database with the new points balance
                    boolean updateSuccess = dbHelper.updateBalance(balance, points);

                    if (updateSuccess) {
                        dbHelper.insertTransaction(productName,productPrice);
                        Log.d("getUserBalanceAndProductData", "Points successfully updated in the database!");
                    } else {
                        Log.e("getUserBalanceAndProductData", "Failed to update points in the database!");
                    }
                } else {
                    // If the user does not have enough points
                    Log.e("getUserBalanceAndProductData", "Insufficient points for the purchase!");
                    return;  // Don't proceed with the purchase
                }
            } else {
                Log.e("getUserBalanceAndProductData", "Missing productName or image data!");
            }
        } else {
            Log.e("getUserBalanceAndProductData", "No bundle received!");
        }

        // Format and update the TextViews
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        String formattedBalance = formatter.format(balance);
        String formattedPoints = formatter.format(points);

        userBalancePayment.setText(formattedBalance);
        userPointsPayment.setText(formattedPoints);
    }

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {

            googleMap.getUiSettings().setScrollGesturesEnabled(true); // Scroll
            googleMap.getUiSettings().setZoomGesturesEnabled(true); // Zoom
            googleMap.getUiSettings().setTiltGesturesEnabled(true); // Tilt
            googleMap.getUiSettings().setRotateGesturesEnabled(true); // Rotate

            LatLng ceuLocation = new LatLng(14.870882257669667, 120.80178878458777);
            Marker ceuMarker = googleMap.addMarker(new MarkerOptions()
                    .position(ceuLocation)
                    .title("Centro Escolar University - Malolos Campus")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))); // Custom color for the marker (Rose color)

            ceuMarker.showInfoWindow();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ceuLocation, 15));

            LatLng p1Location = new LatLng(14.869940195407128, 120.80511957354992);
            Marker p1Marker = googleMap.addMarker(new MarkerOptions().position(p1Location).title("South Star Drugstore"));
            p1Marker.showInfoWindow();

            LatLng p2Location = new LatLng(14.872802168431402, 120.801729261465247);
            Marker p2Marker = googleMap.addMarker(new MarkerOptions().position(p2Location).title("WATSONS WALTERMART"));
            p2Marker.showInfoWindow();

            LatLng p3Location = new LatLng(14.875539672321104, 120.80078512392268);
            Marker p3Marker = googleMap.addMarker(new MarkerOptions().position(p3Location).title("Generika Drugstore-Malolos Cabanas"));
            p3Marker.showInfoWindow();

            LatLng p4Location = new LatLng(14.873341376490293, 120.79361826166775);
            Marker p4Marker = googleMap.addMarker(new MarkerOptions().position(p4Location).title("Amelia's Pharmacy"));
            p4Marker.showInfoWindow();

            LatLng p5Location = new LatLng(14.874635600034335, 120.79621542863438);
            Marker p5Marker = googleMap.addMarker(new MarkerOptions().position(p5Location).title("Father Dear Pharmacy"));
            p5Marker.showInfoWindow();

            LatLng p6Location = new LatLng(14.87485173524053, 120.79619586460437);
            Marker p6Marker = googleMap.addMarker(new MarkerOptions().position(p6Location).title("Tgp The Generics Pharmacy"));
            p6Marker.showInfoWindow();

            LatLng p7Location = new LatLng(14.880047664263538, 120.79241663159259);
            Marker p7Marker = googleMap.addMarker(new MarkerOptions().position(p7Location).title("Pharma One"));
            p7Marker.showInfoWindow();

            LatLng p8Location = new LatLng(14.881567518056867, 120.812723189263);
            Marker p8Marker = googleMap.addMarker(new MarkerOptions().position(p8Location).title("PharmaServe drugstore"));
            p8Marker.showInfoWindow();

            LatLng p9Location = new LatLng(14.859445935856801, 120.8009232539206);
            Marker p9Marker = googleMap.addMarker(new MarkerOptions().position(p9Location).title("Ramcor Pharmacy"));
            p9Marker.showInfoWindow();

            LatLng pLocation10 = new LatLng(14.862196605111837, 120.8069166537948);
            Marker p10Marker = googleMap.addMarker(new MarkerOptions().position(pLocation10).title("Mederi Pharmacy"));
            p10Marker.showInfoWindow();

            LatLng pLocation11 = new LatLng(14.860952212564628, 120.80919653130407);
            Marker p11Marker = googleMap.addMarker(new MarkerOptions().position(pLocation11).title("Batang Malolos Pharmacy And General Merchandise"));
            p11Marker.showInfoWindow();

            LatLng pLocation12 = new LatLng(14.874971954117301, 120.82095533562816);
            Marker p12Marker = googleMap.addMarker(new MarkerOptions().position(pLocation12).title("Farmacia Ni Dok"));
            p12Marker.showInfoWindow();

            LatLng p13Location = new LatLng(14.874349795555375, 120.8217707271422);
            Marker p13Marker = googleMap.addMarker(new MarkerOptions().position(p13Location).title("PEACHES PHARMACY"));
            p13Marker.showInfoWindow();

            LatLng p14Location = new LatLng(14.86584678207013, 120.8207836742568);
            Marker p14Marker = googleMap.addMarker(new MarkerOptions().position(p14Location).title("Family Drug PHARMACY"));
            p14Marker.showInfoWindow();

            LatLng p15Location = new LatLng(14.865509765468797, 120.81972420160535);
            Marker p15Marker = googleMap.addMarker(new MarkerOptions().position(p15Location).title("JEREMIAH 29-11 PHARMACY"));
            p15Marker.showInfoWindow();

            LatLng p16Location = new LatLng(14.841107627535939, 120.79500318425568);
            Marker p16Marker = googleMap.addMarker(new MarkerOptions().position(p16Location).title("Vhenueli Pharmacy"));
            p16Marker.showInfoWindow();

            LatLng p17Location = new LatLng(14.8371251573255, 120.78822255978794);
            Marker p17Marker = googleMap.addMarker(new MarkerOptions().position(p17Location).title("Jk Pharmacy"));
            p17Marker.showInfoWindow();

            LatLng p18Location = new LatLng(14.857286713111895, 120.81775710219577);
            Marker p18Marker = googleMap.addMarker(new MarkerOptions().position(p18Location).title("Price_Less Pharmacy"));
            p18Marker.showInfoWindow();

            LatLng p19Location = new LatLng(14.857836672349995, 120.81761295997856);
            Marker p19Marker = googleMap.addMarker(new MarkerOptions().position(p19Location).title("GOOD DOCTORS PHARMACY"));
            p19Marker.showInfoWindow();

            LatLng p20Location = new LatLng(14.858129983371043, 120.8174384720314);
            Marker p20Marker = googleMap.addMarker(new MarkerOptions().position(p20Location).title("Sariling Atin Drugstore"));
            p20Marker.showInfoWindow();

            LatLng p21Location = new LatLng(14.85790266736447, 120.81737778057153);
            Marker p21Marker = googleMap.addMarker(new MarkerOptions().position(p21Location).title("Angel Drug Store"));
            p21Marker.showInfoWindow();

            LatLng p22Location = new LatLng(14.859231568428608, 120.83109387745648);
            Marker p22Marker = googleMap.addMarker(new MarkerOptions().position(p22Location).title("Farmacia Malubag"));
            p22Marker.showInfoWindow();

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    String markerTitle = marker.getTitle();
                    Toast.makeText(getContext(), "Selected: " + markerTitle, Toast.LENGTH_SHORT).show();

                    mapMarkerSelected = true;
                    showPaymentButton();
                    map_container_delivery.setVisibility(View.VISIBLE);









                    return false;
                }
            });

            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoContents(Marker marker) {
                    // Check if the marker is the CEU marker
                    if (marker.getTitle().equals("Centro Escolar University - Malolos Campus")) {
                        return getCEUContent(marker);  // Custom info window content for CEU marker
                    }

                    // Default behavior for other markers
                    TextView textView = new TextView(getContext());
                    textView.setText(marker.getTitle());
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    textView.setBackgroundColor(Color.parseColor("#da2f7c"));
                    textView.setPadding(10, 10, 10, 10);

                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setCornerRadius(30f);
                    drawable.setColor(Color.parseColor("#da2f7c"));

                    textView.setBackground(drawable);
                    return textView;


                }


                @Nullable
                @Override
                public View getInfoWindow(@NonNull Marker marker) {
                    return null;
                }


                // Custom content for CEU marker
                public View getCEUContent(Marker marker) {
                    TextView textView = new TextView(getContext());
                    textView.setText(marker.getTitle());
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    textView.setPadding(10, 10, 10, 10);

                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setCornerRadius(30f);
                    drawable.setColor(Color.parseColor("#ee4f96"));  // Custom color for CEU marker

                    textView.setBackground(drawable);
                    return textView;
                }
            });


        }

        public void showPaymentButton(){
            BTNLayoutDeliveryPayment.setVisibility(View.VISIBLE);
            map_container_delivery.setVisibility(View.GONE);
            textViewDeliveryPayment.setText("Mode of Payment");

            productLayout.setVisibility(View.VISIBLE);
            BTNLayoutDelivery.setVisibility(View.GONE);
            getProductData();

            getUserBalance();

        }

    public boolean areFieldsFilled() {
        if (TextUtils.isEmpty(regionEditText.getText())) {
            Toast.makeText(getContext(), "Please fill out the Region", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(provinceEditText.getText())) {
            Toast.makeText(getContext(), "Please fill out the Province", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(cityEditText.getText())) {
            Toast.makeText(getContext(), "Please fill out the City / Municipality", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(barangayEditText.getText())) {
            Toast.makeText(getContext(), "Please fill out the Barangay", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(firstNameEditText.getText())) {
            Toast.makeText(getContext(), "Please fill out the First Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(lastNameEditText.getText())) {
            Toast.makeText(getContext(), "Please fill out the Last Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(contactInfoEditText.getText())) {
            Toast.makeText(getContext(), "Please fill out the Contact Info", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            // All fields are filled
            return true;
        }
    }

}