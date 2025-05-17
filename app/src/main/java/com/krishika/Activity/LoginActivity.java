package com.krishika.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.krishika.R;
import com.krishika.databinding.ActivityLoginBinding;
import com.krishika.network.ApiService;
import com.krishika.network.RetrofitClient;
import com.krishika.network.models.AuthResponse;
import com.krishika.network.models.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private CircularProgressIndicator progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progress = binding.loginProgress;

        binding.doNotHaveAcc.setOnClickListener(v -> {
            startActivity(new Intent(this, test.class));
            finish();
        });

        binding.loginBtn.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String input = binding.userInput.getText().toString().trim();
        String password = binding.passInput.getText().toString().trim();

        if (input.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine if input is email or phone
        String email = null;
        String phone = null;
        if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            email = input;
        } else {
            phone = input;
        }

        progress.setVisibility(View.VISIBLE);
        ApiService api = RetrofitClient.getApi(this);
        LoginRequest req = new LoginRequest(email, phone, password);
        api.login(req).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    getSharedPreferences("prefs", MODE_PRIVATE)
                            .edit()
                            .putString("jwt_token", token)
                            .apply();
                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, test.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                progress.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
