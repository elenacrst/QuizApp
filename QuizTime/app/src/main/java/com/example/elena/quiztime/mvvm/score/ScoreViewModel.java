package com.example.elena.quiztime.mvvm.score;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.elena.quiztime.data.ScoreTable;
import com.example.elena.quiztime.ui.MainActivity;

import java.util.List;

/**
 * Created by Absolute on 1/5/2018.
 */

public class ScoreViewModel extends ViewModel{
    private MediatorLiveData<List<ScoreTable>> mScoreData;
    private ScoreInt mScoreInt;

    // No argument constructor
    public ScoreViewModel() {
        mScoreData = new MediatorLiveData<>();
        mScoreInt = new ScoreIntImpl();
    }

    @NonNull
    public LiveData<List<ScoreTable>> getScoreData() {
        return mScoreData;
    }

    public void loadScores(final Context context) {
        mScoreData.addSource(
                mScoreInt.getScores(context), new Observer<List<ScoreTable>>() {
                    @Override
                    public void onChanged(@Nullable List<ScoreTable> scoreTables) {
                        if (scoreTables!=null && scoreTables.size()>0){
                            mScoreData.setValue(scoreTables);
                            MainActivity.setupGridScores(scoreTables);
                        }
                    }

                 /*   @Override
                    public void onChanged(@Nullable List<CategoryData> categoryData) {
                        if (categoryData != null && categoryData.size()>0){
                            mCategoryData.setValue(categoryData);
                            handleResponse(categoryData, context);
                        }else{
                            handleError(context);

                        }
                    }*/
                }
        );
    }

    public ScoreInt getmScoreInt() {
        return mScoreInt;
    }
}
