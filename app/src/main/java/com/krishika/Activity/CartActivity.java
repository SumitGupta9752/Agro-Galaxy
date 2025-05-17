package com.krishika.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.krishika.Activity.Model.UserDetails;
import com.krishika.Adapter.CartAdapter;
import com.krishika.Helper.ManagmentCart;
import com.krishika.R;
import com.krishika.databinding.ActivityCartBinding;

import java.util.HashMap;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managmentCart;
    private double tax;
    private String totalamount;
    private Button changeAddress;
    private TextView Custuser, Custphone, Custaddress;
    private Button placeOrderBtn;
    private boolean isAddressAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Custuser = findViewById(R.id.custName);
        Custphone = findViewById(R.id.custMobile);
        Custaddress = findViewById(R.id.displayAddress);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);
        changeAddress = findViewById(R.id.changeAddress);


//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        String userId = user.getUid();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
//                .getReference("Users")
//                .child(userId)
//                .child("User Details")
//                .child("Secondary");
//
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    String name = snapshot.child("name").getValue(String.class);
//                    Custuser.setText(name);
//                    String phone = snapshot.child("name").getValue(String.class);
//                    Custphone.setText(phone);
//                    String add = snapshot.child("name").getValue(String.class);
//                    Custaddress.setText(add);
//                    Log.d("FirebaseData", "Name: " + name);
//                } else {
//                    Log.d("FirebaseData", "No data found at this reference");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("FirebaseError", "Error: " + error.getMessage());
//            }
//        });

        // Fetch user details
        getUserDetails(new FirebaseCallback() {
            @Override
            public void onCallback(String name, String phone, String address) {
                if (name != null && phone != null && address != null) {
                    // Set values
                    Custuser.setText(name);
                    Custphone.setText(phone);
                    Custaddress.setText(address);
                    isAddressAvailable = true;
                } else {
                    // If details are missing, open Location_Delivery to fill in details
                    isAddressAvailable = false;
                }
            }
        });

        managmentCart = new ManagmentCart(this);
        setVariable();
        calculateCart();
        initList();

        changeAddress.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, Location_Delivery.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Get updated details
            String username = data.getStringExtra("username");
            String phone = data.getStringExtra("Mobile");
            String address = data.getStringExtra("Address");

            // Update UI
            if (username != null && phone != null && address != null) {
                Custuser.setText(username);
                Custphone.setText(phone);
                Custaddress.setText(address);
                isAddressAvailable = true; // Enable Place Order button

            }
        }
    }

    private void initList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollviewCart.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollviewCart.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.cardView.setLayoutManager(linearLayoutManager);
        adapter = new CartAdapter(managmentCart.getListCart(), this, () -> calculateCart());
        binding.cardView.setAdapter(adapter);
    }

    private void calculateCart() {
        double percentTax = 0.02; // 2% tax
        double delivery = 10;  // Delivery charge

        tax = Math.round(managmentCart.getTotalFee() * percentTax * 100.0) / 100;
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100;

        binding.totalFeeTxt.setText("Rs/-" + itemTotal);
        binding.taxTxt.setText("Rs/-" + tax);
        binding.deliveryTxt.setText("Rs/-" + delivery);
        binding.totalTxt.setText("Rs/-" + total);
        totalamount = String.valueOf(total);
        Log.d("CartActivity", "Total Amount: " + totalamount);
    }

    private void setVariable() {

        binding.backBtn.setOnClickListener(v -> finish());

        binding.placeOrderBtn.setOnClickListener(v -> {
            if (!isAddressAvailable) {
                Toast.makeText(CartActivity.this, "Please Complete the Details.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CartActivity.this, Location_Delivery.class);
                startActivityForResult(intent, 1);
                return;
            }

            Intent i = new Intent(CartActivity.this, PaymentActivity.class);
            i.putExtra("total", totalamount);
            startActivity(i);
            finish();
        });
    }

    public interface FirebaseCallback {
        void onCallback(String name, String phone, String address);
    }

    public void getUserDetails(FirebaseCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("FirebaseError", "User is not logged in.");
            callback.onCallback(null, null, null);
            return;
        }

        String userId = user.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("User Details")
                .child("Secondary");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("Customer Name").getValue(String.class);
                    String phone = snapshot.child("Phone Number").getValue(String.class);
                    String address = snapshot.child("Address").getValue(String.class);

                    Log.d("FirebaseData", "Name: " + name + ", Phone: " + phone + ", Address: " + address);
                    callback.onCallback(name, phone, address);
                } else {
                    callback.onCallback(null, null, null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error: " + error.getMessage());
            }
        });
    }
}
