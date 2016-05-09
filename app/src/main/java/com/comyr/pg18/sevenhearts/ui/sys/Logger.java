package com.comyr.pg18.sevenhearts.ui.sys;

import android.util.Log;

import com.comyr.pg18.sevenhearts.BuildConfig;

/**
 * Created by pranav on 5/8/16.
 * On 5:04 PM
 * Custom log generator class that
 * creates logs only in Debug mode
 */
public class Logger{
    public static void d(String tag, String message) {
        if(BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if(BuildConfig.DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if(BuildConfig.DEBUG) {
            Log.e(tag, message);
        }
    }
}
