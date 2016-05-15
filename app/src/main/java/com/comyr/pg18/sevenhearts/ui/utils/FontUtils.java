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
    /**
     * font used in main display text view (shows game state information)
     */
    public static final int FONT_ELECTRONIC = 0;
    /**
     * used as main base font to draw text
     */
    public static final int FONT_CHAU = 1;
    /**
     * used on scorecard
     */
    public static final int FONT_HELVETICA = 2;
    /**
     * used on main title
     */
    public static final int FONT_SATTI = 3;

    private static final LruCache<String, SoftReference<Typeface>> mTypefaceCache;

    static {
        mTypefaceCache = new LruCache<String, SoftReference<Typeface>>(5);
    }

    protected static Typeface getTypeface(Context context) {
        return getTypeface(context, Typeface.NORMAL);
    }

    /**
     * @param context      pass activity context
     * @param typefacetype check {@link FontUtils} class for typeface codes
     * @return required {@link Typeface}
     */
    public static Typeface getTypeface(Context context, int typefacetype) {
        if (context == null)
            return null;
        String typefaceName = "";
        switch (typefacetype) {
            case FONT_ELECTRONIC:
                typefaceName = "electronic.ttf";
                break;

            case FONT_CHAU:
                typefaceName = "chau-philomene-one.ttf";
                break;

            case FONT_HELVETICA:
                typefaceName = "helvetica.ttf";
                break;

            case FONT_SATTI:
                typefaceName = "b-satti.ttf";
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
