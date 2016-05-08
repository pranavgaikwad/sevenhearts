package com.comyr.pg18.sevenhearts.ui.utils.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.comyr.pg18.sevenhearts.ui.utils.CustomResolution;

/**
 * Created by pranav on 5/1/16.
 */
public class ResolutionHelper {
    public static final int VIEW_CARD = 0;
    public static final int VIEW_PLAYER = 1;

    /**
     * instance of the class
     */
    private static ResolutionHelper instance = null;
    /**
     * holds current screen resolution {@see ResolutionHelper#loadScreenResolution}
     */
    private CustomResolution screenResolution;


    public ResolutionHelper() {
    }

    public static ResolutionHelper getInstance() {
        if (instance == null)
            instance = new ResolutionHelper();
        return instance;
    }

    /**
     * loads current screen resolution
     * make sure that screen resolution is loaded everytime you use
     * instance of this class.
     * @param context context for activity
     */
    public void loadScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        this.screenResolution = new CustomResolution(width, height);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public int convertDpToPixel(int dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = (float) dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public int convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dp;
    }

    /**
     * returns {@link CustomResolution} object for given view
     *
     * @param view For example, @see ResolutionHelper#VIEW_CARD
     * @return {@link CustomResolution} object that holds resolutions
     * for specified view (in pixels)
     */
    public CustomResolution getResolutionForView(int view) {
        CustomResolution res;
        switch (view) {
            case VIEW_CARD:
                ValuePair vp = getPossibleResolution();
                res = screenResolution.getResolutionByPercentage(vp.getW(), vp.getH());
                break;
            case VIEW_PLAYER:
                res = screenResolution.getResolutionByPercentage(12);
                break;
            default:
                res = screenResolution;
                break;
        }
        return res;
    }

    /**
     * generates possible resolution for a card based on the
     * current aspect ratio of the screen
     * considering a linear relation between aspect ratio and height of the card,
     * for aspect ratio of 0.60, dimensions 9 x 20 are suited best
     * for aspect ratio of p, dimensions are 9 x 20 x p / 0.60
     *
     * @return {@link ValuePair} just binds height and weight together in an object
     */
    private ValuePair getPossibleResolution() {
        double p = screenResolution.getRatio();
        double heightValue = (25.0 / 0.60 * p);
        double widthValue = (10.5 / 0.60 * p);
        return new ValuePair((int) widthValue, (int) heightValue);
    }

    /**
     * class that helps handle width and height together as an object
     */
    private class ValuePair {
        private int w;
        private int h;

        public ValuePair(int w, int h) {
            this.w = w;
            this.h = h; }
        public int getH() {
            return h;
        }
        public int getW() {
            return w;
        }
    }
}
