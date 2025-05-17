package com.krishika.Activity;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.krishika.R;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {

    Button btn;
    TextView status;
    TextView amount;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);



        // Initialize Razorpay SDK
        Checkout.preload(getApplicationContext());



        String Totalamount = getIntent().getStringExtra("total");
        Log.d("Amount h Amount",Totalamount);
        // Link views
        btn = findViewById(R.id.btn_pay);  // Replace with correct ID
        amount = findViewById(R.id.edit_amount); // Replace with correct ID
        status = findViewById(R.id.paymentStatus); // Replace with correct ID
        amount.setText(Totalamount);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String amountStr = amount.getText().toString().trim(); // Get text & remove spaces

                    // Remove non-numeric characters except digits
                    amountStr = amountStr.replaceAll("[^0-9.]", "");

                    if (amountStr.isEmpty()) {
                        Toast.makeText(PaymentActivity.this, "Amount is invalid or empty", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    double final_amount = Double.parseDouble(amountStr);    
                    startPayment(final_amount); // Call your function

                } catch (NumberFormatException e) {
                    Toast.makeText(PaymentActivity.this, "Error processing amount", Toast.LENGTH_SHORT).show();
                    Log.e("Error", "NumberFormatException: " + e.getMessage());
                }
            }
        });


    }

    public void startPayment(double Amount) {
        Checkout checkout = new Checkout();
                                        // Replace with your actual key

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Sumit Noobda", "Chal Sumit Noobda pay kr");
            jsonObject.put("description", "Buy a food for me");
            jsonObject.put("currency", "INR");
            jsonObject.put("amount", Amount * 100); // Convert to paise

            // Add retry options
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 2); // Optional, maximum retries
            jsonObject.put("retry", retryObj);

            checkout.open(PaymentActivity.this, jsonObject);

        } catch (Exception e) {
            Log.e(TAG, "Error in starting payment", e);
            Toast.makeText(this, "Payment initiation failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String paymentId) {
        runOnUiThread(() -> status.setText("Success: " + paymentId));

        // Get the authenticated user again
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Prepare order details
            HashMap<String, Object> orderData = new HashMap<>();
            orderData.put("Order Status", "Placed");
            orderData.put("Transaction ID", paymentId);
            orderData.put("Timestamp", System.currentTimeMillis());

            // Save order data to Firebase under "Orders" node for each user
            databaseReference.child(userId).child("Orders").push().setValue(orderData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(PaymentActivity.this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(PaymentActivity.this, "Failed to save order: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        } else {
            Toast.makeText(PaymentActivity.this, "User not logged in!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPaymentError(int i, String s) {
        runOnUiThread(() -> status.setText("Error: " + s));

    }
}
