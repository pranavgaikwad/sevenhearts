package com.comyr.pg18.sevenhearts.ui.sys;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.comyr.pg18.sevenhearts.R;

import java.security.Permission;

/**
 * Created by pranav on 5/8/16.
 * On 2:45 PM
 */
public class SysUtils {
    /**
     * default vibrate time
     */
    public static int DEFAULT_VIBRATE = 400;
    /**
     * vibrates the device for given time
     * @param mil time in milliseconds to vibrate
     */
    public static void vibrate(Context context, int mil) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(mil);
    }
    /**
     * vibrates the device for default time
     * @param context activity context
     */
    public static void vibrate(Context context) {
        if(isVibrateAllowed(context)) {
            vibrate(context, DEFAULT_VIBRATE);
        }
    }
    /**
     * this method plays a sound notifying user that it's their turn to play
     */
    public static void playUserMoveSound(Context context) {
        final MediaPlayer mp = MediaPlayer.create(context, R.raw.notification);
        mp.start();
    }
    /**
     * checks if vibration is permitted by the user
     * @param context activity context
     * @return true if vibration is allowed, false otherwise
     */
    private static boolean isVibrateAllowed(Context context) {
        String per = "android.permission.VIBRATE";
        int res = context.checkCallingOrSelfPermission(per);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
