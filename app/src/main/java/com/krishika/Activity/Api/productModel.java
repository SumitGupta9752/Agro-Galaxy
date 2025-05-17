
package com.krishika.Activity.Api;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class productModel {
        @SerializedName("title")
        public Map<String, String> title;

        @SerializedName("description")
        public Map<String, String> description;

        @SerializedName("moreDetails")
        public Map<String, String> moreDetails;

        @SerializedName("category")
        public Map<String, String> category;

        @SerializedName("_id")
        public String id;

        public int price;
        public int stock;
        public String brand;
        public String thumbnail;
        public int averageRating;
        public boolean deleted;
        public double discountPercentage;
        public List<String> reviews;
    }
