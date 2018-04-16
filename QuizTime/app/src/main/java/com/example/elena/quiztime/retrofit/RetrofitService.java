package com.example.elena.quiztime.retrofit;

import com.example.elena.quiztime.retrofit.category.CategoryResponse;
import com.example.elena.quiztime.retrofit.question.QuestionResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by User on 31/12/2017.
 */

public interface RetrofitService {



    @GET("/api_category.php")
    Call<CategoryResponse> getCategoryResponse();

    @GET("/api.php")
    Call<QuestionResponse> getQuestionResponse(@Query("category") int categoryId,
                                               @Query("difficulty") String difficulty,
                                               @Query("amount")String amount);

}
