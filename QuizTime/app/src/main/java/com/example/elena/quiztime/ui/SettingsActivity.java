package com.example.elena.quiztime.ui;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.elena.quiztime.R;
import com.example.elena.quiztime.pref.QuizPreferences;

public class SettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        QuizPreferences mSettingsFragment = QuizPreferences.newInstance(this);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container_settings, mSettingsFragment)
                .commit();
    }

    public static void changedPref(Activity activity){
        activity.setResult(RESULT_OK);
    }


}
