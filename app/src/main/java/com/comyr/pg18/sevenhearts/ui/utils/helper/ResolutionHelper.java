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
    private static ResolutionHelper instance;
    private CustomResolution screenResolution;

    public ResolutionHelper() {

    }

    public static ResolutionHelper getInstance() {
        if (instance == null)
            instance = new ResolutionHelper();
        return instance;
    }

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
    public int convertPixelsToDp(int px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = (float) px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dp;
    }

    public CustomResolution getResolutionForView(int view) {
        CustomResolution res;
        switch (view) {
            case VIEW_CARD:
                res = screenResolution.getResolutionByPercentage(10, 24);
                break;
            default:
                res = screenResolution;
                break;
        }
        return res;
    }

}
