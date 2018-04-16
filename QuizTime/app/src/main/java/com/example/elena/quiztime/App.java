package com.example.elena.quiztime;

import android.app.Application;

import com.example.elena.quiztime.dagger.component.DaggerNetComponent;
import com.example.elena.quiztime.dagger.component.NetComponent;
import com.example.elena.quiztime.dagger.module.AppModule;
import com.example.elena.quiztime.dagger.module.NetModule;

/**
 * Created by Absolute on 1/10/2018.
 */

public class App extends Application {//pass it in the manifest
    //an Application class gets instantiated before any other class when the process for the app is created
    //it's used for initialization of global state before the first activity is displayed
    //mutable shared data should not be stored here, use sharedpref or sqlite or files instead

    private NetComponent mNetComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialization logic here
        mNetComponent = DaggerNetComponent.builder()//if it cant be resolved, try build project. the project needs to be compiled so that this file would be generated
                .appModule(new AppModule(this))//method not resolved if it's not defined as module in netcomponent
                .netModule(new NetModule())
                .build();
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }


}