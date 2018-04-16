package com.example.elena.quiztime.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.elena.quiztime.App;
import com.example.elena.quiztime.R;
import com.example.elena.quiztime.adapters.AnswersAdapter;
import com.example.elena.quiztime.fragments.FooterFragment;
import com.example.elena.quiztime.mvvm.question.QuestionViewModel;
import com.example.elena.quiztime.retrofit.question.QuestionData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class QuestionActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        FooterFragment.OnFooterFragmentInteractionListener {

    private QuestionViewModel mQuestionModel;
    private int id;
    private String prefDifficulty;
    private String prefAmount;
    private long mTimeMillis;

    private static int mScore;
    private static int mCurrentQuestionIndex;
    private static String sCorrectAnswer;
    private static String selection;
    private static boolean isSelected;

    private static TextView mScoreTextView;
    private static TextView mQuestionTextView;
    private static GridView mAnswersGrid;
    private static TextView mTimerTextView;
    private static ImageView mNextImageView;
    private static ImageView mDoneAllImageView;
    private static TextView mEmptyViewAnswers;
    private static ProgressBar mLoadingIndicator;

    private static List<QuestionData> mQuestionData;
    private static List<String> sCurrentAnswers;

    private static CountDownTimer mTimerAnswer;

    public static final String EXTRA_SCORE = "score";
    public static final String PREF_TIME_MILLIS = "time_millis";
    public static final String BUNDLE_SCORE= "bundle-score";
    public static final String QUESTION_INDEX_EXTRA = "question-index";
    public static final String SELECTED_ANSWER_EXTRA="selected-answer";
    public static final String TIMER_EXTRA = "timer";
    public static final String SCORE_EXTRA = "score";
    public static final int SETTINGS_REQUEST_CODE = 401;

    @Inject
    Retrofit retrofit;//dagger does not support injection into private fields

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        findViews();

        mEmptyViewAnswers.setVisibility(View.GONE);

        displayLoading();
        initializeQuestionModel();

        Log.v("openedfragment", "true");
        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.EXTRA_ID)) {
            id = intent.getIntExtra(MainActivity.EXTRA_ID, 0);
            initializePrefs();
            mTimerTextView.setVisibility(View.GONE);
            if (savedInstanceState != null && savedInstanceState.containsKey(QUESTION_INDEX_EXTRA)) {
                mCurrentQuestionIndex = savedInstanceState.getInt(QUESTION_INDEX_EXTRA);
                mScore = savedInstanceState.getInt(SCORE_EXTRA);
                mTimeMillis = savedInstanceState.getLong(TIMER_EXTRA);

                setupTimerText();
                long time = mTimeMillis;
                if (isSelected) time = 30000;
                mTimerTextView.setVisibility(View.VISIBLE);

                mScoreTextView.setVisibility(View.VISIBLE);
                mScoreTextView.setText(String.format(getString(R.string.score_value), mScore));

                if (mTimerAnswer != null) mTimerAnswer.cancel();
                mTimerAnswer = null;
                mTimerAnswer = new CountDownTimer(time, 1000) {
                    public void onTick(long millisUntilFinished) {
                        mTimeMillis = millisUntilFinished;
                        setupTimerText();
                    }
                    public void onFinish() {
                        onTimeOut();
                    }
                }.start();

                bindViews(mQuestionData.get(mCurrentQuestionIndex).getQuestion(),
                        mQuestionData.get(mCurrentQuestionIndex).getCorrect_answer(),
                        mQuestionData.get(mCurrentQuestionIndex).getIncorrect_answers(), false);
                displayAnswers();

                selection = savedInstanceState.getString(SELECTED_ANSWER_EXTRA);

            } else {
                mQuestionModel.loadQuestions(this, id, prefDifficulty, prefAmount);

                if (mTimerAnswer != null) mTimerAnswer.cancel();
                mTimerAnswer = null;
                mTimerAnswer = new CountDownTimer(30000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        setupTimerText();
                    }
                    public void onFinish() {
                        onTimeOut();
                    }
                };
            }

        }

        onChangeQuestion();

        mAnswersGrid.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mAnswersGrid.removeOnLayoutChangeListener(this);
                Log.d("finishedadapter", selection+" selected");
                if (sCurrentAnswers!=null)
                    for (int i=0; i<sCurrentAnswers.size(); i++){
                        View view = mAnswersGrid.getChildAt(i);
                        if (view==null) return;
                        TextView textView = view.findViewById(R.id.text_answer);
                        String text = textView.getText().toString();
                        Log.v("textcompareselect"+i,text);
                        if (text.equals(selection)){
                            if (sCorrectAnswer.equals(selection)){
                                textView.setBackgroundResource(R.drawable.rounded_rec_green);
                            }else{
                                textView.setBackgroundResource(R.drawable.rounded_rec_red);
                            }
                            mAnswersGrid.setEnabled(false);
                            mNextImageView.setEnabled(true);
                            mTimerAnswer.cancel();
                            break;
                    }
                }
            }
        });
    }

    private void findViews(){
        mScoreTextView =  findViewById(R.id.text_score);
        mQuestionTextView =  findViewById(R.id.text_question);
        mAnswersGrid =  findViewById(R.id.grid_answers);
        mTimerTextView = findViewById(R.id.timer_answer);
        mNextImageView =  findViewById(R.id.image_next);
        mEmptyViewAnswers = findViewById(R.id.empty_view_answers);
        mLoadingIndicator = findViewById(R.id.pb_loading_answers );
        mDoneAllImageView = findViewById(R.id.done_all_image);
    }

    public static void bindViews(String question, String correctAnswer,
                                 List<String> incorrectAnswers, boolean shuffle){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mQuestionTextView.setText(Html.fromHtml(question, Html.FROM_HTML_MODE_LEGACY));
        }
        else{
            mQuestionTextView.setText(Html.fromHtml(question));
        }
        List<String> answers= new ArrayList<>();
        if (shuffle) {
            answers.add(correctAnswer);
            answers.addAll(incorrectAnswers);
            Collections.shuffle(answers);
            sCurrentAnswers = answers;
        }else{
            answers=sCurrentAnswers;
        }
        AnswersAdapter adapter = new AnswersAdapter(mQuestionTextView.getContext(),answers,
                sCorrectAnswer);
        mAnswersGrid.setAdapter(adapter);
        mTimerTextView.setVisibility(View.VISIBLE);
    }

    public static void setDataFirstTime(List<QuestionData> questionData){

        Log.v("first time","invisible loading");
        mQuestionData = questionData;
        mScore = 0;
        mScoreTextView.setText(String.format(mAnswersGrid.getContext()
                .getString(R.string.score_value),mScore));
        Collections.shuffle(mQuestionData);
        if (questionData != null && questionData.size()>0) {
            bindViews(questionData.get(0).getQuestion(), questionData.get(0).getCorrect_answer(),
                    questionData.get(0).getIncorrect_answers(),true);
            mCurrentQuestionIndex =0;
        }

        if (mTimerAnswer!=null){
            mTimerAnswer.cancel();
            mTimerAnswer.start();
        }
        mAnswersGrid.setEnabled(true);
        mNextImageView.setEnabled(false);
        mDoneAllImageView.setEnabled(true);

        examineNetwork();

        mLoadingIndicator.setVisibility(View.GONE);
        mAnswersGrid.setVisibility(View.VISIBLE);

        if (mQuestionData!=null && mQuestionData.size()>0){
            sCorrectAnswer = mQuestionData.get(0).getCorrect_answer();
        }
    }

    public void onChangeQuestion(){
        mAnswersGrid.setEnabled(true);
        mNextImageView.setEnabled(false);
        if (mTimerAnswer!=null){
            mTimerAnswer.cancel();
            mTimerAnswer = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    mTimeMillis = millisUntilFinished;
                    setupTimerText();
                }
                public void onFinish() {
                    onTimeOut();
                }
            }.start();
        }
        else{
          //  mTimerAnswer = null;
            mTimerAnswer = new CountDownTimer(30000, 1000) {
                public void onTick(long millisUntilFinished) {
                    mTimeMillis = millisUntilFinished;
                    setupTimerText();
                }
                public void onFinish() {
                    onTimeOut();
                }
            }.start();
        }

    }

    @Override
    public void onBackPressed() {
        if (mTimerAnswer!=null)mTimerAnswer.cancel();
        mTimerAnswer = null;
        super.onBackPressed();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_difficulty_pref))){
            prefDifficulty = sharedPreferences.getString(
                    getString(R.string.key_difficulty_pref),
                    getString(R.string.difficulty_easy));
            mTimerTextView.setVisibility(View.GONE);
            mQuestionModel.loadQuestions(this,id,prefDifficulty,prefAmount);
        }else if (key.equals(getString(R.string.key_amount_pref))){
            prefAmount = sharedPreferences.getString(
                    getString(R.string.key_amount_pref),
                    "20");
            mTimerTextView.setVisibility(View.GONE);
            mQuestionModel.loadQuestions(this,id,prefDifficulty,prefAmount);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==SETTINGS_REQUEST_CODE ){
            if (resultCode==RESULT_OK){
                if (mTimerAnswer==null){
                    mTimerAnswer = new CountDownTimer(30000, 1000) {
                        public void onTick(long millisUntilFinished) {
                           setupTimerText();
                        }
                        public void onFinish() {
                            onTimeOut();
                        }
                    };
                }
                mTimerAnswer.start();
            }else{//returning from settings without any modification - keep time
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(this);
                long future = 0;
                try{
                    String sharedpref= sharedPreferences.getString(PREF_TIME_MILLIS,"0");
                    Log.v("preftime", sharedpref);
                    future = Long.valueOf(sharedpref)*1000;
                }catch (Exception e){
                    e.printStackTrace();
                }
                mTimerAnswer= new CountDownTimer(future, 1000) {
                    public void onTick(long millisUntilFinished) {
                        setupTimerText();
                    }
                    public void onFinish() {
                        onTimeOut();
                    }
                };
            mTimerAnswer.start();
            }

        }
    }

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onRefreshFragment() {
        Log.v("called", "refresh");

        mTimerTextView.setVisibility(View.INVISIBLE);
        mQuestionModel.loadQuestions(this, id, prefDifficulty, prefAmount);
    }

    @Override
    public void onSettings() {
        if (mTimerAnswer!=null){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREF_TIME_MILLIS, mTimerTextView.getText().subSequence(5,7)+"");
            editor.apply();
            mTimerAnswer.cancel();
        }
        mTimerAnswer = null;

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent,SETTINGS_REQUEST_CODE);
    }

    @Override
    public void onHome() {
        if (mTimerAnswer!=null) mTimerAnswer.cancel();
        mTimerAnswer = null;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNext() {
        isSelected=false;

        mCurrentQuestionIndex++;

        if (mQuestionData!=null && mCurrentQuestionIndex < mQuestionData.size()){
            onChangeQuestion();

            sCorrectAnswer= mQuestionData.get(mCurrentQuestionIndex).getCorrect_answer();

            bindViews(mQuestionData.get(mCurrentQuestionIndex).getQuestion(),
                    mQuestionData.get(mCurrentQuestionIndex).getCorrect_answer(),
                    mQuestionData.get(mCurrentQuestionIndex).getIncorrect_answers(),true);

        }

        displayAnswers();

        sCorrectAnswer= mQuestionData.get(mCurrentQuestionIndex).getCorrect_answer();
    }

    @Override
    public void onDoneAll() {
        setResult(RESULT_OK);
        if (mTimerAnswer!=null)mTimerAnswer.cancel();
        mTimerAnswer = null;
        Intent intent = new Intent(this, ScoreActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(MainActivity.EXTRA_ID,id);
        bundle.putInt(EXTRA_SCORE, mScore);
        intent.putExtra(BUNDLE_SCORE, bundle);
        startActivity(intent);
        finish();//=> back from scoreactivity leads to main
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(QUESTION_INDEX_EXTRA, mCurrentQuestionIndex);
        outState.putString(SELECTED_ANSWER_EXTRA, selection);
        outState.putInt(SCORE_EXTRA, mScore);
        outState.putLong(TIMER_EXTRA, mTimeMillis);
        Log.v("saveselection",selection+"");
    }

    public static void increaseScore(boolean isCorrect){
        if (isCorrect){
        mScore += 100;
        mScoreTextView.setText(String.format(mAnswersGrid.getContext()
                .getString(R.string.score_value),mScore));
        }

        mAnswersGrid.setEnabled(false);
        if (mTimerAnswer != null) mTimerAnswer.cancel();
        if (mCurrentQuestionIndex == mQuestionData.size() - 1) {
            mNextImageView.setEnabled(false);
        } else
            mNextImageView.setEnabled(true);

    }

    public static boolean isAnsweringEnabled(){
        return mAnswersGrid.isEnabled();
    }

    public static void setSelection(String mselection){
        if (mAnswersGrid.isEnabled()){
            isSelected = true;
            selection = mselection;
            Log.v("setselection",selection);
        }
    }

    private void initializeQuestionModel(){
        mQuestionModel = ViewModelProviders.of(this).get(QuestionViewModel.class);
        ((App) getApplication()).getNetComponent().injectQuestion(this);//dagger
        mQuestionModel.setRetrofit(retrofit);
    }

    private void initializePrefs(){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        prefDifficulty = sharedPreferences.getString(getString(R.string.key_difficulty_pref),
                getString(R.string.difficulty_easy));
        prefAmount = sharedPreferences.getString(getString(R.string.key_amount_pref),
                "20");
        Log.v("loadquest", id + " " + prefDifficulty + " " + prefAmount);
    }

    private void setupTimerText(){
        if (mTimeMillis / 1000 < 10) {
            mTimerTextView.setText(String.format(getString(R.string.timer_second_unit),
                    mTimeMillis / 1000));
        } else {
            mTimerTextView.setText(String.format(getString(R.string.timer_seconds),
                    mTimeMillis / 1000));
        }
    }

    private void onTimeOut(){
        mAnswersGrid.setEnabled(false);
        mTimerTextView.setText(R.string.time_expired);
        mNextImageView.setEnabled(true);
    }

    private void displayLoading(){
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mAnswersGrid.setVisibility(View.GONE);
    }

    private void displayAnswers(){
        mLoadingIndicator.setVisibility(View.GONE);
        mAnswersGrid.setVisibility(View.VISIBLE);
    }

    private static void examineNetwork(){
        if (!isNetworkAvailable(mAnswersGrid.getContext())){
            mAnswersGrid.setVisibility(View.GONE);
            mEmptyViewAnswers.setVisibility(View.VISIBLE);
            mEmptyViewAnswers.setText(mAnswersGrid.getContext().getString(R.string.message_internet_required));
        }else if (mAnswersGrid.getCount()==0){
            mAnswersGrid.setVisibility(View.GONE);
            mEmptyViewAnswers.setVisibility(View.VISIBLE);
            mEmptyViewAnswers.setText(mAnswersGrid.getContext().getString(R.string.message_no_data));
        }else{
            mEmptyViewAnswers.setVisibility(View.GONE);
            mAnswersGrid.setVisibility(View.VISIBLE);
        }
    }
}