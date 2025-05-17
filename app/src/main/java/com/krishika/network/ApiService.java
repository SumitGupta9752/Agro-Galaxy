package com.krishika.network;

import com.krishika.network.models.AuthResponse;
import com.krishika.network.models.BasicResponse;
import com.krishika.network.models.LoginRequest;
import com.krishika.network.models.SignupRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @POST("signup")
    Call<AuthResponse> signup(@Body SignupRequest body);

    @POST("login")
    Call<AuthResponse> login(@Body LoginRequest body);

    @POST("forgot-password")
    Call<BasicResponse> forgotPassword(@Body BasicResponse body);

    @POST("reset-password")
    Call<BasicResponse> resetPassword(@Body BasicResponse body);

    @POST("change-password")
    Call<BasicResponse> changePassword(
            @Header("Authorization") String bearerToken,
            @Body BasicResponse body
    );

    @POST("update-password")
    Call<BasicResponse> updatePasswordByUserId(
            @Body BasicResponse body
    );
}