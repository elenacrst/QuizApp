package com.example.elena.quiztime.mvvm.question;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.example.elena.quiztime.retrofit.question.QuestionData;

import java.util.List;

/**
 * Created by User on 31/12/2017.
 */

public interface QuestionInt {
    MutableLiveData<List<QuestionData>> getQuestions(Context context, int categoryId, String difficulty,
                                                     String amount);
}
