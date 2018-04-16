package com.example.elena.quiztime.mvvm.category;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.example.elena.quiztime.retrofit.category.CategoryData;

import java.util.List;

/**
 * Created by User on 31/12/2017.
 */

public interface CategoryInt {
    MutableLiveData<List<CategoryData>> getCategories(Context context);
}
