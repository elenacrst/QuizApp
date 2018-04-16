package com.example.elena.quiztime.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elena.quiztime.R;
import com.example.elena.quiztime.data.ScoreTable;
import com.example.elena.quiztime.fragments.FooterFragment;
import com.example.elena.quiztime.mvvm.score.ScoreIntImpl;
import com.example.elena.quiztime.mvvm.score.ScoreViewModel;

public class ScoreActivity extends AppCompatActivity
        implements FooterFragment.OnFooterFragmentInteractionListener {

    private TextView mScoreTextView;
    private ImageView mNextImageView;
    private ImageView mDoneAllImageView;

    private static TextView mBestScoreTextView;

    private static int bestScore;

    private int mCategoryId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        findViews();

        mNextImageView.setEnabled(false);
        mDoneAllImageView.setEnabled(false);

        Intent intent = getIntent();
        if (intent.hasExtra(QuestionActivity.BUNDLE_SCORE)){
            Bundle bundle = intent.getBundleExtra(QuestionActivity.BUNDLE_SCORE);
            int score = bundle.getInt(QuestionActivity.EXTRA_SCORE);
            bestScore = score;
            mCategoryId = bundle.getInt(MainActivity.EXTRA_ID);
            Log.v("main-call","before insert call");

            ScoreIntImpl.insertScoreInDb(this, new ScoreTable(mCategoryId, score));

            mScoreTextView.setText(String.format(getString(R.string.scored_now), score));

            ScoreIntImpl.getBestScoreForCategory(this,mCategoryId);
        }

    }

    public static void setupBestScoreText(int best){
        if (best>bestScore){
            bestScore = best;
        }
        setupBestScoreTextFirst();
    }

    public static void setupBestScoreTextFirst() {
        mBestScoreTextView.setText(String.format(mBestScoreTextView.getContext()
                .getString(R.string.scored_best), bestScore));
    }

    @Override
    public void onRefreshFragment() {
        Intent intent = new Intent(this, QuestionActivity.class);
        intent.putExtra(MainActivity.EXTRA_ID, mCategoryId);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNext() {
    }

    @Override
    public void onDoneAll() {
    }

    private void findViews(){
        mScoreTextView = findViewById(R.id.score_text);
        mBestScoreTextView =  findViewById(R.id.best_score_text);
        mNextImageView = findViewById(R.id.image_next);
        mDoneAllImageView = findViewById(R.id.done_all_image);
    }

}
