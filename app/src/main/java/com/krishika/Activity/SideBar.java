package com.krishika.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.krishika.R;

public class SideBar extends AppCompatActivity {

    private LinearLayout backBtn, homeBtn, profileBtn, ordersBtn, settingsBtn, logoutBtn;
    private ImageView closeSidebar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_bar);

        backBtn = findViewById(R.id.backBtn);
        homeBtn = findViewById(R.id.homeBtn);
        profileBtn = findViewById(R.id.profileBtn);
        ordersBtn = findViewById(R.id.ordersBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        closeSidebar = findViewById(R.id.closeSidebar);

        // Close sidebar on clicking the close button
        closeSidebar.setOnClickListener(v -> finish());

        // Back button to go to the previous activity
        backBtn.setOnClickListener(v -> onBackPressed());

        // Handle navigation clicks
        homeBtn.setOnClickListener(v -> navigateTo(MainActivity.class));
//        profileBtn.setOnClickListener(v -> navigateTo(ProfileActivity.class));
        ordersBtn.setOnClickListener(v -> navigateTo(OrdersActivity.class));
//        settingsBtn.setOnClickListener(v -> navigateTo(SettingsActivity.class));
        logoutBtn.setOnClickListener(v -> logout());
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(SideBar.this, activityClass);
        startActivity(intent);
        finish();
    }

    private void logout() {
        // Add logout logic if needed (e.g., FirebaseAuth sign-out)
        finish();
    }
}
