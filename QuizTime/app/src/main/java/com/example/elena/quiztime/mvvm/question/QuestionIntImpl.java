package com.example.elena.quiztime.mvvm.question;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.elena.quiztime.retrofit.RetrofitService;
import com.example.elena.quiztime.retrofit.question.QuestionData;
import com.example.elena.quiztime.retrofit.question.QuestionResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by User on 31/12/2017.
 */

public class QuestionIntImpl implements QuestionInt {

    private RetrofitService mRetrofitService;

    public QuestionIntImpl(){
    }

    public QuestionIntImpl(Retrofit retrofit){

        mRetrofitService = retrofit.create(RetrofitService.class);//keep this
    }

    @Override
    public MutableLiveData<List<QuestionData>> getQuestions(final Context context, int categoryId,
                                                            String difficulty, String amount) {
        final MutableLiveData<List<QuestionData>> liveData = new MutableLiveData<>();
        Call<QuestionResponse> call = mRetrofitService.getQuestionResponse(categoryId,
                difficulty, amount);
        call.enqueue(new Callback<QuestionResponse>() {
            @Override
            public void onResponse(@NonNull Call<QuestionResponse> call,
                                   @NonNull Response<QuestionResponse> response) {
                List<QuestionData> questionList = new ArrayList<>();
                if (response.body() != null && response.body().getQuestions()!=null){
                    questionList.addAll(response.body().getQuestions());
                    liveData.setValue(questionList);
                    QuestionViewModel.handleResponse(questionList, context);
                }
            }

            @Override
            public void onFailure(@NonNull Call<QuestionResponse> call, @NonNull Throwable t) {
                QuestionViewModel.handleError(context);
                Log.v("error-getting-categ",t.toString());
            }
        });
        return liveData;
    }
}
