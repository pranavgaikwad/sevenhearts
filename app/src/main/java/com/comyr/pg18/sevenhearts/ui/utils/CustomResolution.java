package com.comyr.pg18.sevenhearts.ui.utils;

/**
 * Created by pranav on 5/1/16.
 * This class is used at runtime to calculate screen resolution,
 * get relative resolutions for different UI components as and when
 * required. It has utilities that can also convert resolutions from
 * dp to pixels and vice versa.
 */
public class CustomResolution {
    /**
     * width in pixels
     */
    private int width;
    /**
     * height in pixels
     */
    private int height;

    /**
     * used to maintain aspect ratio of the ui component
     */
    private double ratio;

    public CustomResolution(int width, int height) {
        this.height = height;
        this.width = width;
        this.ratio = (double) height / (double) width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public double getRatio() {
        return ratio;
    }

    /**
     * returns new resolution values which are
     * given percentage of the current screen resolution
     * for example, if current res is 800 x 800, then, for, input 50
     * this function will return new {@link CustomResolution} object
     * which is 400 x 400
     * @param percentage from 1 to 100
     * @return {@link CustomResolution}
     */
    public CustomResolution getResolutionByPercentage(double percentage) {
        double fraction = percentage / 100.0;
        double newWidth = (double) this.width * fraction;
        double newHeight = (double) this.height * fraction;
        return new CustomResolution((int) newWidth, (int) newHeight);
    }

    /**
     * returns new {@link CustomResolution} values which are
     * given percentage of the current resolution
     * for example, if current res is 800 x 800, then, for, input 50, 25
     * this function will return new {@link CustomResolution} which is 400 x 200
     *
     * @param pWidth  widht percentage
     * @param pHeight height percentage
     * @return returns {@link CustomResolution} object which is pWidth X pHeight of the original resolution
     */
    public CustomResolution getResolutionByPercentage(int pWidth, int pHeight) {
        double fractionWidth = (double) pWidth / 100;
        double fractionHeight = (double) pHeight / 100;
        int newWidth = (int) (this.width * fractionWidth);
        int newHeight = (int) (this.height * fractionHeight);
        return new CustomResolution(newWidth, newHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.valueOf(width) + "x" + String.valueOf(height);
    }
}
