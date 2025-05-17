package com.krishika.Activity.Api;

import android.content.Context;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiHelper {

    public static <T> void fetchData(Call<T> call, Context context, ApiCallback<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    Toast.makeText(context, "Code: " + response.code(), Toast.LENGTH_SHORT).show();
                    callback.onFailure(new Throwable("Error code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onFailure(t);
            }
        });
    }

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onFailure(Throwable t);
    }
}
