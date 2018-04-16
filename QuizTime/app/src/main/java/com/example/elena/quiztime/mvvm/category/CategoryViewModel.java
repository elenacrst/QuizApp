package com.example.elena.quiztime.mvvm.category;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.elena.quiztime.retrofit.category.CategoryData;
import com.example.elena.quiztime.ui.MainActivity;

import java.util.List;

import retrofit2.Retrofit;

/**
 * Created by User on 31/12/2017.
 */

public class CategoryViewModel extends ViewModel {
    private MediatorLiveData<List<CategoryData>> mCategoryData;
    private CategoryInt mCategInt;

    // No argument constructor
    public CategoryViewModel() {//passed from activity where it's injected
        mCategoryData = new MediatorLiveData<>();
        mCategInt = new CategoryIntImpl();

    }

    public void setRetrofit(Retrofit retrofit){
        //set retrofit here.. for categint and call the method in mainactivity after instantiating
        mCategInt = new CategoryIntImpl(retrofit);
    }

    @NonNull
    public LiveData<List<CategoryData>> getCategories() {
        return mCategoryData;
    }

    public void loadCategories(final Context context) {
        mCategoryData.addSource(
                mCategInt.getCategories(context), new Observer<List<CategoryData>>() {
                    @Override
                    public void onChanged(@Nullable List<CategoryData> categoryData) {
                        if (categoryData != null && categoryData.size()>0){
                            mCategoryData.setValue(categoryData);
                            handleResponse(categoryData, context);
                        }else{
                            handleError(context);

                        }
                    }
                }
        );
    }

    static void handleResponse(final List<CategoryData> data, final Context context) {
        for (CategoryData categoryData: data){
            Log.v("category", categoryData.getName()+" name, "+categoryData.getId()+
            " id");
        }
        MainActivity.setupGrid(data);
    }



    public static void handleError(final Context context) {
        MainActivity.stopLoading();
        Toast.makeText(context, "Error ", Toast.LENGTH_SHORT).show();
    }

    public CategoryInt getmCategInt() {
        return mCategInt;
    }
}
