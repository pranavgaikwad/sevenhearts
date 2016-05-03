package com.comyr.pg18.sevenhearts.ui.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.LruCache;

import java.lang.ref.SoftReference;

/**
 * Created by pranav on 5/2/16.
 * In package com.comyr.pg18.sevenhearts.ui.utils
 */
public class FontUtils {
    public static final int FONT_ELECTRONIC = 0;
    public static final int FONT_CARTWHEEL = 1;

    private static final LruCache<String, SoftReference<Typeface>> mTypefaceCache;

    static {
        mTypefaceCache = new LruCache<String, SoftReference<Typeface>>(5);
    }

    protected static Typeface getTypeface(Context context) {
        return getTypeface(context, Typeface.NORMAL);
    }

    /**
     * @param context      pass activity context
     * @param typefacetype check FontHelper class for typeface codes
     * @return
     */
    public static Typeface getTypeface(Context context, int typefacetype) {
        if (context == null)
            return null;
        String typefaceName = "";
        switch (typefacetype) {
            case FONT_ELECTRONIC:
                typefaceName = "electronic.ttf";
                break;

            case FONT_CARTWHEEL:
                typefaceName = "cartwheel.ttf";
                break;
        }

        synchronized (mTypefaceCache) {

            if (mTypefaceCache.get(typefaceName) != null) {
                SoftReference<Typeface> ref = mTypefaceCache.get(typefaceName);
                if (ref.get() != null) {
                    return ref.get();
                }
            }

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), typefaceName);
            mTypefaceCache.put(typefaceName, new SoftReference<Typeface>(typeface));
            return typeface;
        }
    }
}
