package com.krishika.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.krishika.Activity.Api.ApiClient;
import com.krishika.Activity.Api.ApiHelper;
import com.krishika.Activity.Api.ApiService;
import com.krishika.Activity.Api.productModel;
import com.krishika.Adapter.CategoryAdapter;
import com.krishika.Adapter.ProductAdapter;
import com.krishika.Domain.Category;
import com.krishika.R;
import com.krishika.databinding.ActivityMainBinding;
import com.krishika.databinding.ActivityTestBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class test extends AppCompatActivity {
    private ActivityTestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//         fetchProductsOnlyLog();
        initCategory();
    }
    private void fetchProductsOnlyLog() {
        ApiService productApi = ApiClient.getClient().create(ApiService.class);
        Call<List<productModel>> call = productApi.getProducts();

        call.enqueue(new Callback<List<productModel>>() {
            @Override
            public void onResponse(Call<List<productModel>> call, Response<List<productModel>> response) {
                if (!response.isSuccessful()) {
                    Log.e("API_RESPONSE", "Failed with code: " + response.code());
                    return;
                }

                List<productModel> productList = response.body();
                if (productList != null) {
                    for (productModel product : productList) {
                        String name = product.title.get("en");
                        int price = product.price;
                        Log.d("PRODUCT_LOG", "Name: " + name + ", Price: ₹" + price);
                    }
                } else {
                    Log.d("PRODUCT_LOG", "No products found.");
                }
            }

            @Override
            public void onFailure(Call<List<productModel>> call, Throwable t) {
                Log.e("API_ERROR", "Error fetching products: " + t.getMessage());
            }
        });
    }

    private void initCategory() {
        ApiService productApi = ApiClient.getClient().create(ApiService.class);
        Call<List<productModel>> call = productApi.getProducts();
        binding.progressBarCategory.setVisibility(View.VISIBLE);

        ArrayList<productModel> productList = new ArrayList<>();
        ProductAdapter adapter = new ProductAdapter(this, productList);
        binding.categoryView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.categoryView.setAdapter(adapter);

        ApiHelper.fetchData(call, this, new ApiHelper.ApiCallback<List<productModel>>() {
            @Override
            public void onSuccess(List<productModel> products) {
                Log.e("API_SUCCESS", "Chal gya bhai");
                productList.clear();                   // Clear old data (if any)
                productList.addAll(products);          // Add new products
                adapter.notifyDataSetChanged();        // Notify RecyclerView
                binding.progressBarCategory.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("API_ERROR", "Failed: " + t.getMessage());
                binding.progressBarCategory.setVisibility(View.GONE);
            }
        });
    }


}