package com.example.elena.quiztime.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Absolute on 1/5/2018.
 */
@Entity(tableName = "user_scores")
public class ScoreTable {
    @PrimaryKey
    private int categoryId;
    private int score;

    public ScoreTable( int categoryId, int score) {
        this.categoryId = categoryId;
        this.score = score;
    }

    public ScoreTable(){}

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
