package com.krishika.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.krishika.Activity.Api.productModel;
import com.krishika.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    List<productModel> productList;

    public ProductAdapter(Context context, List<productModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, brand;
        ImageView thumbnail;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtProductName);
            price = itemView.findViewById(R.id.txtProductPrice);
            brand = itemView.findViewById(R.id.txtBrand);
            thumbnail = itemView.findViewById(R.id.imgProductThumb);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        productModel product = productList.get(position);

        holder.title.setText(product.title.get("en"));
        holder.price.setText("₹" + product.price);
        holder.brand.setText(product.brand);

        Glide.with(context)
                .load(product.thumbnail)
                .placeholder(R.drawable.bg) // optional placeholder
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
