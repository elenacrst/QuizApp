package com.example.elena.quiztime.mvvm.score;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.example.elena.quiztime.data.ScoreTable;

import java.util.List;

/**
 * Created by Absolute on 1/5/2018.
 */

public interface ScoreInt {
    MutableLiveData<List<ScoreTable>> getScores(Context context);
}
