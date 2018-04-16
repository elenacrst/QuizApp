package com.example.elena.quiztime.mvvm.question;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.elena.quiztime.retrofit.question.QuestionData;
import com.example.elena.quiztime.ui.QuestionActivity;

import java.util.List;

import retrofit2.Retrofit;

/**
 * Created by User on 31/12/2017.
 */

public class QuestionViewModel extends ViewModel {
    private MediatorLiveData<List<QuestionData>> mQuestionData;
    private QuestionInt mQuestInt;

    // No argument constructor
    public QuestionViewModel() {
        mQuestionData = new MediatorLiveData<>();
        mQuestInt = new QuestionIntImpl();

    }

    public void setRetrofit(Retrofit retrofit){
        //set retrofit here.. for categint and call the method in mainactivity after instantiating
        mQuestInt = new QuestionIntImpl(retrofit);
    }

    @NonNull
    public LiveData<List<QuestionData>> getQuestions() {
        return mQuestionData;
    }

    public void loadQuestions(final Context context, int categoryId, String difficulty, String amount) {
        mQuestionData.addSource(
                mQuestInt.getQuestions(context, categoryId, difficulty, amount), new Observer<List<QuestionData>>() {
                    @Override
                    public void onChanged(@Nullable List<QuestionData> questionData) {
                        if (questionData != null && questionData.size()>0){
                            mQuestionData.setValue(questionData);
                            handleResponse(questionData, context);
                        }else{
                            handleError(context);

                        }
                    }
                }
        );
    }

    public static void handleResponse(final List<QuestionData> data, final Context context) {
        for (QuestionData questionData: data){
            Log.v("question", questionData.getQuestion()+" question, "
            +questionData.getCorrect_answer()+" correct ");
        }
        QuestionActivity.setDataFirstTime(data);

    }

    public static void handleError(final Context context) {
        Toast.makeText(context, "Error ", Toast.LENGTH_SHORT).show();

    }

    public QuestionInt getmQuestInt() {
        return mQuestInt;
    }
}
