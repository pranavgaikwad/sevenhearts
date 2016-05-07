package com.comyr.pg18.sevenhearts.application;

import android.app.Application;

import com.comyr.pg18.sevenhearts.BuildConfig;
import com.comyr.pg18.sevenhearts.network.analytics.MixPanel;

import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Created by pranav on 5/7/16.
 * In package com.comyr.pg18.sevenhearts.application
 */
public class BadamSattiMain extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    paramThrowable.printStackTrace(pw);
                    MixPanel.getInstance(getApplicationContext()).trackAction(MixPanel.ACTION_CRASH, MixPanel.TAG_CAUSE, sw.toString());
                    System.exit(2);
                }
            });
        }
    }
}
