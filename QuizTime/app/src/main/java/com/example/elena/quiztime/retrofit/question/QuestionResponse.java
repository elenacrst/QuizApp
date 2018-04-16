package com.example.elena.quiztime.retrofit.question;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 31/12/2017.
 */

public class QuestionResponse {
    @SerializedName("results")
    @Expose
    private List<QuestionData> questions = new ArrayList<>();

    public List<QuestionData> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionData> questions) {
        this.questions = questions;
    }
}
