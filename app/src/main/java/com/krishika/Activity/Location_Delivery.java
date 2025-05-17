package com.krishika.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.krishika.Activity.Model.DeliveryAddress;
import com.krishika.Activity.Model.UserDetails;
import com.krishika.R;
import com.krishika.databinding.ActivityLocationDeliveryBinding;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Location_Delivery extends AppCompatActivity {

    private ActivityLocationDeliveryBinding binding;
    private Button confirmButton;
    private EditText newUsername, newUserphone, newAddress;
    private ImageView locationBtn;


    // For location fetching
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final String API_KEY = "pk.4e8f5d889cfaa73c12a43f72d7385924"; // Your LocationIQ API key
    // Using a background executor for network calls (OkHttp)
    private Executor networkExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable EdgeToEdge if using that utility
        // EdgeToEdge.enable(this); // Uncomment if using EdgeToEdge library

        binding = ActivityLocationDeliveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("User Details")
                .child("Primary");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    newUsername.setText(name);
                    Log.d("FirebaseData", "Name: " + name);
                } else {
                    Log.d("FirebaseData", "No data found at this reference");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });

        // Initialize views (using view binding or findViewById if needed)
        newUsername = binding.editUserName;
        newUserphone = binding.editUserPhone;
        newAddress = binding.addressInput;
        locationBtn = binding.detectLocationIcon;
        confirmButton = binding.confirmButton;

        // Set window insets for edge-to-edge experience.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize FusedLocationProviderClient for location fetching
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Request location permissions if not granted and then fetch location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            fetchLocation();
        }

        // When the detect location icon is clicked, fetch location and update address field
        locationBtn.setOnClickListener(v -> fetchLocation());

        // When the confirm button is clicked, save the details to Firebase
        confirmButton.setOnClickListener(v -> {
            String name = newUsername.getText().toString().trim();
            String phone = newUserphone.getText().toString().trim();
            String address = newAddress.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(Location_Delivery.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            savetodatabase(name, phone, address, success -> {
                if (success) {
                    Toast.makeText(Location_Delivery.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                }
            });
            Intent resultIntent = new Intent();
            resultIntent.putExtra("username", name); // Pass the new address
            resultIntent.putExtra("Mobile", phone); // Pass the new address
            resultIntent.putExtra("Address", address); // Pass the new address
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    // Method to fetch location and update the address field using reverse geocoding
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permissions not granted", Toast.LENGTH_SHORT).show();
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Toast.makeText(Location_Delivery.this, "Lat: " + latitude + ", Lon: " + longitude, Toast.LENGTH_SHORT).show();

                // Reverse geocode in a background thread
                networkExecutor.execute(() -> {
                    String address = getDisplayName(latitude, longitude);
                    runOnUiThread(() -> {
                        Toast.makeText(Location_Delivery.this, "Address: " + address, Toast.LENGTH_LONG).show();
                        // Optionally, update the address field with the fetched address
                        newAddress.setText(address);

                    });
                });
            } else {
                Toast.makeText(Location_Delivery.this, "Unable to fetch location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Reverse geocode using LocationIQ API (network call using OkHttp)
    private String getDisplayName(double latitude, double longitude) {
        String url = "https://us1.locationiq.com/v1/reverse.php?key=" + API_KEY
                + "&lat=" + latitude
                + "&lon=" + longitude
                + "&format=json";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                return jsonObject.getString("display_name");
            } else {
                return "Unable to fetch address. Code: " + response.code();
            }
        } catch (IOException | org.json.JSONException e) {
            e.printStackTrace();
            return "Error fetching address";
        }
    }

    // Handle location permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Callback interface for saving data to Firebase (if needed elsewhere)
    public interface DatabaseSaveCallback {
        void onSaveResult(boolean success);
    }

    // Method to save to a different node in Firebase (optional usage)
    private void savetodatabase(String name, String phone, String address, DatabaseSaveCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("Users")
                    .child(userId)
                    .child("User Details")
                    .child("Secondary");

            HashMap<String, Object> orderData = new HashMap<>();
            orderData.put("Customer Name", name);
            orderData.put("Phone Number", phone);
            orderData.put("Address", address);

            databaseReference.setValue(orderData)
                    .addOnSuccessListener(aVoid -> {
                        callback.onSaveResult(true);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Location_Delivery.this, "Failed to save details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        callback.onSaveResult(false);
                    });
        } else {
            Toast.makeText(Location_Delivery.this, "User not logged in!", Toast.LENGTH_SHORT).show();
            callback.onSaveResult(false);
        }
    }
}
