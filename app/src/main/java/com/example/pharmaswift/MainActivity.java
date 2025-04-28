package com.example.pharmaswift;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.pharmaswift.databinding.ActivityMainBinding;
import com.example.pharmaswift.navigation_bar.DatabaseHelper;
import com.example.pharmaswift.navigation_bar.HomeFragment;
import com.example.pharmaswift.navigation_bar.ProfileFragment;
import com.example.pharmaswift.navigation_bar.TransactionFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNavigationView.setSelectedItemId(R.id.home);
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.transaction:
                    replaceFragment(new TransactionFragment());
                    break;
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }


            return true;

            });



        DatabaseHelper dbHelper = new DatabaseHelper(this);
        dbHelper.updateDrugsImages(this, R.drawable.drugs_trimetazidine, "trimetazidine");


















    }



    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_main_activity, fragment);
        fragmentTransaction.commit();
    }
}