package com.comyr.pg18.sevenhearts.ui.activities;

import android.util.Log;

/**
 * Created by pranav on 5/1/16.
 * In package com.comyr.pg18.sevenhearts.ui.activities
 */
public class TestThread implements Runnable {
    public static boolean isFinished = false;
    private final String TAG = "TestThread";
    private Thread thread;
    private String name;
    private boolean suspended = false;

    public TestThread(String name) {
        this.name = name;
        Log.d(TAG, "creating " + name);
    }

    public String getName() {
        return name;
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    public void run() {
        Log.d(TAG, "running : " + name);
        try {
            while (!isFinished) {// Let the thread sleep for a while.
                suspend();
                Log.d(TAG, "thread " + name + " resumed");
                synchronized (this) {
                    while (suspended) {
                        wait();
                    }
                }
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "thread " + name + " interrupted.");
        }
        Log.d(TAG, "thread " + name + " exiting...");
    }

    public void start() {
        Log.d(TAG, "Starting : " + name);
        if (thread == null) {
            thread = new Thread(this, name);
            thread.start();
        }
    }

    void suspend() {
        suspended = true;
    }

    synchronized void resume() {
        suspended = false;
        notify();
    }
}
