package com.example.pharmaswift.navigation_bar;

import static com.google.maps.android.Context.getApplicationContext;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.model.PlacesSearchResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Locale;


public class HomeFragment extends Fragment implements OnMapReadyCallback {

    
    Button BTNshop;

    LinearLayout mapContainer;
    TextView textViewBalance, textViewPoints;
    private GestureDetector gestureDetector;
    private ScrollView scrollView;


    @SuppressLint({"NonConstantResourceId", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);


        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        copyDatabase();


        textViewBalance = v.findViewById(R.id.text_view_cash_balance);
        textViewPoints = v.findViewById(R.id.text_view_points_balance);

        scrollView = v.findViewById(R.id.ScrollViewHomeFragment);

        double[] userBalance = dbHelper.getBalance();
        double cashBalance = userBalance[0];
        double pointsBalance = userBalance[1];

        if (cashBalance == 0.0 && pointsBalance == 0.0) {
            Log.d("BalanceCheck", "Balance is zero, inserting default balance...");

            // Insert default values
            dbHelper.insertBalance(3110, 1230);

            // After inserting, fetch the balance again
            userBalance = dbHelper.getBalance();
            cashBalance = userBalance[0];
            pointsBalance = userBalance[1];
        } else {
            Log.d("BalanceCheck", "Existing balance found. Cash: " + cashBalance + ", Points: " + pointsBalance);
        }

        // Format and set the TextViews
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        String formattedBalance = formatter.format(cashBalance);
        String formattedPoints = formatter.format(pointsBalance);

        textViewBalance.setText(formattedBalance);
        textViewPoints.setText(formattedPoints);

        MainActivity mainActivity = (MainActivity) getActivity();
        BTNshop = v.findViewById(R.id.BTNshop);
        BTNshop.setOnClickListener(v1 -> {
                mainActivity.replaceFragment(new StoreFragment());

        });




        mapContainer = v.findViewById(R.id.map_container);

        BottomNavigationView upperNavigationView = v.findViewById(R.id.upperNavigationView);
        //disables the selected item
        upperNavigationView.getMenu().setGroupCheckable(0, true, false);
        upperNavigationView.getMenu().getItem(0).setChecked(false);
        upperNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.medicine_logo_upper_navigation_bar:
                    Toast.makeText(getContext(), "Medicine", Toast.LENGTH_SHORT).show();
                    mainActivity.replaceFragment(new MedicineFragment());

                    return false;

                case R.id.phone_logo_upper_navigation_bar:
                    Toast.makeText(getContext(), "Connecting with Pharmacist", Toast.LENGTH_SHORT).show();
                    mainActivity.replaceFragment(new ChatFragment());

                    return false;

                case R.id.puzzles_logo_upper_navigation_bar:
                    Toast.makeText(getContext(), "Earn Points", Toast.LENGTH_SHORT).show();
                    mainActivity.replaceFragment(new GameFragment());

                    return false;

                case R.id.more_logo_upper_navigation_bar:
                    Toast.makeText(getContext(), "More", Toast.LENGTH_SHORT).show();
                    return false;


            }
            return false;
        });


        // Set up gesture detector to detect map gestures
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // Disabling the scroll of other views when interacting with the map
                scrollView.requestDisallowInterceptTouchEvent(true);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });


        mapContainer.setOnTouchListener((v1, event) -> {
            gestureDetector.onTouchEvent(event);
            return false; // Allow the event to be passed on for further handling
        });

        mapContainer.setOnClickListener(view -> {
            Toast.makeText(getContext(), "Searching", Toast.LENGTH_SHORT).show();
            Log.d("PlacesTask", "Map Clicked");


        });
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.id_map);
        mapFragment.getMapAsync(this);





        return v;
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




// Only need to use when the database doesnt contain the medicines
public void copyDatabase() {
    // Path to the app's internal storage database directory
    String dbPath = getContext().getApplicationContext().getDatabasePath("PharmaSwift.db").getAbsolutePath();

    // Check if the database already exists in the app's internal storage
    File dbFile = new File(dbPath);
    if (!dbFile.exists()) {
        // If the database doesn't exist, copy it from assets
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            // Open the input stream for the database in the assets folder
            inputStream = getContext().getAssets().open("PharmaSwift.db");

            // Create the output stream for the destination database file
            outputStream = new FileOutputStream(dbFile);

            // Copy the data from the input stream to the output stream (assets to internal storage)
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            // Flush and close both streams
            outputStream.flush();
            Log.d("DatabaseCopy", "Database copied successfully to: " + dbPath);

        } catch (IOException e) {
            Log.e("DatabaseCopy", "Error copying database: " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                Log.e("DatabaseCopy", "Error closing streams: " + e.getMessage());
            }
        }
    } else {
        Log.d("DatabaseCopy", "Database already exists, no need to copy.");
    }
}





}