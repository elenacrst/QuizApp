package com.example.elena.quiztime.mvvm.category;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.elena.quiztime.retrofit.RetrofitService;
import com.example.elena.quiztime.retrofit.category.CategoryData;
import com.example.elena.quiztime.retrofit.category.CategoryResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by User on 31/12/2017.
 */

public class CategoryIntImpl implements CategoryInt {
    private RetrofitService mRetrofitService;

    public CategoryIntImpl(){

    }

    public CategoryIntImpl(Retrofit retrofit){
        mRetrofitService = retrofit.create(RetrofitService.class);//keep this
    }

    @Override
    public MutableLiveData<List<CategoryData>> getCategories(final Context context) {
        final MutableLiveData<List<CategoryData>> liveData = new MutableLiveData<>();
        Call<CategoryResponse> call = mRetrofitService.getCategoryResponse();
        call.enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResponse> call,
                                   @NonNull Response<CategoryResponse> response) {
                List<CategoryData> categList = new ArrayList<>();
                if (response.body() != null && response.body().getCategories()!=null){
                    categList.addAll(response.body().getCategories());
                    liveData.setValue(categList);
                    CategoryViewModel.handleResponse(categList, context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) {
                 CategoryViewModel.handleError(context);
                 Log.v("error-getting-categ",t.toString());
            }
        });
        return liveData;
    }
}
