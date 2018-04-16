package com.example.elena.quiztime.retrofit.category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 31/12/2017.
 */

public class CategoryResponse {
    @SerializedName("trivia_categories")
    @Expose
    private List<CategoryData> categories = new ArrayList<>();

    public void setCategories(List<CategoryData> categories) {
        this.categories = categories;
    }

    public List<CategoryData> getCategories() {
        return categories;
    }
}
