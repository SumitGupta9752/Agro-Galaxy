package com.krishika.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.krishika.R;
import com.krishika.databinding.ActivitySignupBinding;
import com.krishika.network.ApiService;
import com.krishika.network.RetrofitClient;
import com.krishika.network.models.SignupRequest;
import com.krishika.network.models.AuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private CircularProgressIndicator progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progress = binding.signupProgress;

        binding.HaveAcc.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        binding.signupBtn.setOnClickListener(v -> attemptSignup());
    }

    private void attemptSignup() {
        String name = binding.nameInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();
        String phone = binding.phoneInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Please fill all fields (password ≥6 chars)", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setVisibility(View.VISIBLE);
        ApiService api = RetrofitClient.getApi(this);
        SignupRequest req = new SignupRequest(email, phone, password, name, "user");
        api.signup(req).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    getSharedPreferences("prefs", MODE_PRIVATE)
                            .edit()
                            .putString("jwt_token", token)
                            .apply();
                    startActivity(new Intent(SignupActivity.this, test.class));
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, "Signup failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(SignupActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
