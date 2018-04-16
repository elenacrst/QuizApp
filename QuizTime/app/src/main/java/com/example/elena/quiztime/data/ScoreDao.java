package com.example.elena.quiztime.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Absolute on 1/5/2018.
 */

@Dao
public interface ScoreDao {
    @Query("select * from user_scores")
    List<ScoreTable> getScores();

    @Insert
    void insertScore(ScoreTable score);

    @Update(onConflict =  REPLACE)
    void updateScore(ScoreTable score);
}