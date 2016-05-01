package com.comyr.pg18.sevenhearts.ui.utils;

import android.util.Log;

/**
 * Created by pranav on 5/1/16.
 */
public class CustomResolution {
    private final String TAG = "CustomResolution";
    // w, h in pixels
    private int width;
    private int height;

    public CustomResolution(int width, int height) {
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    /**
     * returns new resolution values which are
     * given percentage of the current resolution
     * for example, if current res is 800 x 800, then, for, input 50
     * this function will return new resolution which is 400 x 400
     *
     * @param percentage from 1 to 100
     * @return {@link CustomResolution}
     */
    public CustomResolution getResolutionByPercentage(int percentage) {
        double fraction = percentage / 100;
        int newWidth = (int) (this.width * fraction);
        int newHeight = (int) (this.height * fraction);
        return new CustomResolution(newWidth, newHeight);
    }

    public CustomResolution getResolutionByPercentage(int pWidth, int pHeight) {
        double fractionWidth = (double) pWidth / 100;
        double fractionHeight = (double) pHeight / 100;
        Log.d(TAG, String.valueOf(fractionHeight) + " and " + String.valueOf(fractionWidth));
        int newWidth = (int) (this.width * fractionWidth);
        int newHeight = (int) (this.height * fractionHeight);
        Log.d(TAG, String.valueOf(newHeight) + " and " + String.valueOf(newWidth));
        return new CustomResolution(newWidth, newHeight);
    }

    @Override
    public String toString() {
        return String.valueOf(width) + "x" + String.valueOf(height);
    }
}
