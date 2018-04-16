package com.example.elena.quiztime.retrofit.question;

import java.util.List;

/**
 * Created by User on 31/12/2017.
 */

public class QuestionData {
    private String question, correct_answer;
    private List<String> incorrect_answers;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }

    public List<String> getIncorrect_answers() {
        return incorrect_answers;
    }

    public void setIncorrect_answers(List<String> incorrect_answers) {
        this.incorrect_answers = incorrect_answers;
    }

}
