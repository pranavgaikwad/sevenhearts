package com.comyr.pg18.sevenhearts.application;

import android.app.Application;

import com.comyr.pg18.sevenhearts.network.analytics.MixPanel;


/**
 * Created by pranav on 5/7/16.
 * In package com.comyr.pg18.sevenhearts.application
 */
public class BadamSattiMain extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                MixPanel.getInstance(getApplicationContext()).trackAction(MixPanel.ACTION_CRASH, MixPanel.TAG_CAUSE, paramThrowable.toString());
            }
        });
    }
}
