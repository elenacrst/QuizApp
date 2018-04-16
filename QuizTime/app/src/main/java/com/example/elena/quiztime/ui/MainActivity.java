package com.example.elena.quiztime.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.elena.quiztime.App;
import com.example.elena.quiztime.R;
import com.example.elena.quiztime.adapters.ButtonsAdapter;
import com.example.elena.quiztime.data.ScoreTable;
import com.example.elena.quiztime.mvvm.category.CategoryViewModel;
import com.example.elena.quiztime.mvvm.score.ScoreViewModel;
import com.example.elena.quiztime.retrofit.category.CategoryData;

import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static GridView mCategoriesGrid;

    public static final int QUESTION_REQUEST = 10;
    public static final String EXTRA_ID = "extra-id";

    private CategoryViewModel mCategoryModel;

    private static List<CategoryData> categoryData;

    private static ScoreViewModel mScoreModel;

    private static List<ScoreTable> sScoreData;

    private static TextView mEmptyCategoriesTextView;
    private static ProgressBar mLoadingIndicator;

    @Inject
    Retrofit retrofit;//dagger does not support injection into private fields


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("called","main");

        mCategoriesGrid = findViewById(R.id.grid_categories);
        mEmptyCategoriesTextView =  findViewById(R.id.empty_view_categories);
        mLoadingIndicator =  findViewById(R.id.pb_loading);

        mLoadingIndicator.setVisibility(View.VISIBLE);
        mCategoriesGrid.setVisibility(View.GONE);

        mEmptyCategoriesTextView.setVisibility(View.GONE);

        mCategoriesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, QuestionActivity.class);
                String selectedCateg =  categoryData.get(position).getName();
                int selectedId = categoryData.get(position).getId();
                intent.putExtra(EXTRA_ID, selectedId);
                //intent
                startActivityForResult(intent, QUESTION_REQUEST);
            }
        });

        mCategoryModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        ((App) getApplication()).getNetComponent().injectMain(this);//dagger
        mCategoryModel.setRetrofit(retrofit);

        mCategoryModel.loadCategories(this);

        mScoreModel = ViewModelProviders.of(this).get(ScoreViewModel.class);


    }

    public static void setupGrid(List<CategoryData> categories){
        Log.v("main-call","setup grid");
        categoryData = categories;
        mCategoriesGrid.setAdapter(new ButtonsAdapter(mCategoriesGrid.getContext(), categories));

        if (QuestionActivity.isNetworkAvailable(mCategoriesGrid.getContext())){
            mEmptyCategoriesTextView.setVisibility(View.GONE);
            mCategoriesGrid.setVisibility(View.VISIBLE);
            mEmptyCategoriesTextView.setText(mCategoriesGrid.getContext().getString(R.string.message_internet_required));
        }else if (mCategoriesGrid.getCount()==0){
            mEmptyCategoriesTextView.setVisibility(View.GONE);
            mCategoriesGrid.setVisibility(View.VISIBLE);
            mEmptyCategoriesTextView.setText(mCategoriesGrid.getContext().getString(R.string.message_no_data));
        }else{
            mCategoriesGrid.setVisibility(View.GONE);
            mEmptyCategoriesTextView.setVisibility(View.VISIBLE);
        }

        mLoadingIndicator.setVisibility(View.GONE);

        mScoreModel.loadScores(mCategoriesGrid.getContext());
    }

    public static void setupGridScores(List<ScoreTable> scores){
        mLoadingIndicator.setVisibility(View.GONE);
        Log.v("main-call","setup grid scores");
        sScoreData = scores;
        ButtonsAdapter adapter = new ButtonsAdapter(mCategoriesGrid.getContext(), categoryData);
        adapter.setScores(scores);
        mCategoriesGrid.setAdapter(adapter);

        if (!QuestionActivity.isNetworkAvailable(mCategoriesGrid.getContext())){

            mCategoriesGrid.setVisibility(View.GONE);
            mEmptyCategoriesTextView.setVisibility(View.VISIBLE);
            mEmptyCategoriesTextView.setText(mCategoriesGrid.getContext().getString(R.string.message_internet_required));
        }else if (mCategoriesGrid.getCount()==0){
            mCategoriesGrid.setVisibility(View.GONE);
            mEmptyCategoriesTextView.setVisibility(View.VISIBLE);
            mEmptyCategoriesTextView.setText(mCategoriesGrid.getContext().getString(R.string.message_no_data));
        }else{
            mEmptyCategoriesTextView.setVisibility(View.GONE);
            mCategoriesGrid.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==QUESTION_REQUEST && resultCode == RESULT_OK){
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mCategoriesGrid.setVisibility(View.GONE);
            mScoreModel.loadScores(mCategoriesGrid.getContext());
        }
    }

    public static void stopLoading(){
        MainActivity.mLoadingIndicator.setVisibility(View.GONE);
    }
}
