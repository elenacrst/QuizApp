package com.example.elena.quiztime.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Absolute on 1/5/2018.
 */


@Database(entities = {ScoreTable.class},
        version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ScoreDao scoreDao();

}