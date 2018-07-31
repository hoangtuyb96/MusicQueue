package com.example.musicqueue;

import android.app.Application;
import android.content.Context;

public class MusicApplication extends Application{
    private static Context mContext;
    private static MusicApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        MusicApplication.mContext = getApplicationContext();
        mInstance = this;
    }

    public static MusicApplication getInstance(){
        return mInstance;
    }

    public static Context getAppContext(){
        return mContext;
    }
}
