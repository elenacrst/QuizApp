package com.example.elena.quiztime.mvvm.score;

import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.util.Log;

import com.example.elena.quiztime.R;
import com.example.elena.quiztime.data.AppDatabase;
import com.example.elena.quiztime.data.ScoreTable;
import com.example.elena.quiztime.ui.MainActivity;
import com.example.elena.quiztime.ui.ScoreActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Absolute on 1/5/2018.
 */

public class ScoreIntImpl implements ScoreInt{

    private static List<ScoreTable> data = new ArrayList<>();

    public ScoreIntImpl(){
    }

    @Override
    public MutableLiveData<List<ScoreTable>> getScores(final Context context) {
        final MutableLiveData<List<ScoreTable>> liveData = new MutableLiveData<>();

        queryDb(context, liveData);

        return liveData;
    }

    public static void queryDb(final Context context,
                                           final MutableLiveData<List<ScoreTable>> scores){

        io.reactivex.Observable.fromCallable(new Callable<List<ScoreTable>>() {


            @Override
            public List<ScoreTable> call() throws Exception {
                AppDatabase db = Room
                        .databaseBuilder(context, AppDatabase.class, context.getString(R.string.db_name))
                        .fallbackToDestructiveMigration()
                        .build();
                List<ScoreTable> scores = db.scoreDao().getScores();

                for (ScoreTable scoreTable: scores){
                    Log.v("scores-query", scoreTable.getCategoryId()+" "+
                            scoreTable.getScore());
                }
                data = scores;
                return data;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<ScoreTable>>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.v("error-query-scores", e.toString());
                    }

                    @Override
                    public void onComplete() {
                        scores.setValue(data);
                        MainActivity.setupGridScores(data);
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<ScoreTable> scoreTables) {

                    }

                });

    }

    public static void insertScoreInDb(final Context context, final ScoreTable score){
        Log.v("main-call","before callable");

        io.reactivex.Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Log.v("main-call","during call");
                AppDatabase db = Room
                        .databaseBuilder(context, AppDatabase.class, context.getString(R.string.db_name))
                        .fallbackToDestructiveMigration()
                        .build();
                List<ScoreTable> scores = db.scoreDao().getScores();
                int currentScore = getScoreForCategory(scores,score.getCategoryId());
                if (currentScore < score.getScore()){
                    if (currentScore == -1){
                        Log.v("main-call","inserting score");
                        db.scoreDao().insertScore(score);
                    }else{
                        Log.v("main-call","updating score");
                        db.scoreDao().updateScore(score);
                    }
                }

                return true;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.v("error", e.toString());
                    }

                    @Override
                    public void onComplete() {
                        Log.v("main-call","complete");
                    }
                });

    }

    public static int getScoreForCategory(List<ScoreTable> scores, int categoryId){
        for (ScoreTable score : scores){
            if (score.getCategoryId() == categoryId){
                return score.getScore();
            }
        }
        return -1;
    }

    public static void getBestScoreForCategory(final Context context, final int categoryId){

        io.reactivex.Observable.fromCallable(new Callable<List<ScoreTable>>() {
            @Override
            public List<ScoreTable> call() throws Exception {
                AppDatabase db = Room
                        .databaseBuilder(context, AppDatabase.class, context.getString(R.string.db_name))
                        .fallbackToDestructiveMigration()
                        .build();
                List<ScoreTable> scores = db.scoreDao().getScores();



                for (ScoreTable scoreTable: scores){
                    Log.v("scores-query", scoreTable.getCategoryId()+" "+
                            scoreTable.getScore());
                }
                data = scores;
                return data;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<ScoreTable>>() {
                    @Override
                    public void onError(Throwable e) {
                        Log.v("error-query-scores", e.toString());
                    }

                    @Override
                    public void onComplete() {
                        boolean found = false;
                        for (ScoreTable score : data){
                            if (score.getCategoryId() == categoryId){
                                ScoreActivity.setupBestScoreText(score.getScore());
                                found = true;
                            }
                        }
                        if (!found){
                            ScoreActivity.setupBestScoreTextFirst();
                        }
                    }

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<ScoreTable> scoreTables) {

                    }

                });

    }



}
