package com.example.pharmaswift.navigation_bar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.VolumeShaper;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pharmaswift.MainActivity;
import com.example.pharmaswift.R;

import org.w3c.dom.Text;

public class VerificationFragment extends Fragment {
    MainActivity mainActivity;
    private static final int pic_id = 123;
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    Button BTNCamera, BTNSubmit;
    ImageView imageView;
    TextView textView;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_verification, container, false);
        textView = v.findViewById(R.id.textViewCamera);
        imageView = v.findViewById(R.id.imageViewCamera);

        mainActivity = (MainActivity) getActivity();

        BTNSubmit = v.findViewById(R.id.BTNSubmit);
        BTNCamera = v.findViewById(R.id.BTNCamera);
        BTNCamera.setOnClickListener(v1 -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            } else {
                openCamera();
                BTNSubmit.setVisibility(View.VISIBLE);
                textView.setText("Submit for Verification");
            }
        });

        BTNSubmit.setOnClickListener(v1 -> {
            // After verification (e.g., after camera photo taken)
            onVerificationSuccess();  // Send bundle to DeliveryFragment
        });





        return v;
    }
    public void onVerificationSuccess() {
        Bundle bundle = getArguments(); // Get the bundle passed from the previous fragment
        if (bundle != null) {
            DeliveryFragment deliveryFragment = new DeliveryFragment();
            deliveryFragment.setArguments(bundle);  // Pass the same bundle to DeliveryFragment
            mainActivity.replaceFragment(deliveryFragment);  // Replace the current fragment with DeliveryFragment
        } else {
            Log.e("VerificationFragment", "Bundle is null, cannot send to DeliveryFragment");
        }
    }

    private void openCamera() {
        Log.d("camerasomething", "camera something");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, pic_id);
        } else {
            Log.d("camerasomething", "No camera app found");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == pic_id && resultCode == getActivity().RESULT_OK) {
            // Get the photo as a Bitmap
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            // Set the photo on the ImageView
            imageView.setImageBitmap(photo);
        }
    }
}