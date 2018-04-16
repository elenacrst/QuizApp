package com.example.elena.quiztime.dagger.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Absolute on 1/10/2018.
 */

@Module
public class NetModule {

    public static final String BASE_URL = "https://opentdb.com";

    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        return new Retrofit.Builder()//move this to dagger
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
