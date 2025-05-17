package com.krishika.Activity.Api;

import com.krishika.Activity.Api.productModel;

import java.util.List;


import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("product")
    Call<List<productModel>> getProducts();
}

