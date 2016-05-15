package com.comyr.pg18.sevenhearts.stats;

import android.content.Context;

import com.comyr.pg18.sevenhearts.stats.listeners.OnAchievementUnlockedListener;
import com.comyr.pg18.sevenhearts.utils.PreferenceHelper;

/**
 * Created by pranav on 5/13/16.
 * On 12:49 PM
 * This class has been created to manipulate game <b>statistics.</b>
 * Game statistics basically include numbers that keep track of below
 * mentioned properties :
 * 1. Total games played
 * 2. Total games won
 * 3. Games won in a row
 * 4. Games played in one instance of the application
 * 5. Total points scored (To be decided)
 * This class is used to maintain achievements and leaderboards in Google
 * play games console.
 */
public class StatHelper {
    /**
     * Broadcasts achievements
     */
    public static OnAchievementUnlockedListener l;

    /**
     * Increments the count of total games played by the user
     *
     * @param context Activity context
     */
    private static void incrementGamesPlayed(Context context) {
        int o = PreferenceHelper.getInstance(context).ri(PreferenceHelper.KEY_GAMES_PLAYED);
        int n;
        if (o == PreferenceHelper.STATUS_INT_NOT_FOUND) {
            PreferenceHelper.getInstance(context).w(PreferenceHelper.KEY_GAMES_PLAYED, 1);
            n = 1;
        } else {
            n = o + 1;
            PreferenceHelper.getInstance(context).w(PreferenceHelper.KEY_GAMES_PLAYED, n);
        }
        if (l != null) l.onGamesPlayed(n);
    }

    /**
     * Increments games won by the player
     *
     * @param context Activity context
     */
    private static void incrementGamesWon(Context context) {
        int o = PreferenceHelper.getInstance(context).ri(PreferenceHelper.KEY_GAMES_WON);
        int n;
        if (o == PreferenceHelper.STATUS_INT_NOT_FOUND) {
            PreferenceHelper.getInstance(context).w(PreferenceHelper.KEY_GAMES_WON, 1);
            n = o;
        } else {
            n = o + 1;
            PreferenceHelper.getInstance(context).w(PreferenceHelper.KEY_GAMES_WON, n);
        }
        if (l != null) l.onGamesWon(n);
    }

    /**
     * Increments games played in a row by 1
     */
    public static void incrementGamesWonInRow(Context context) {
        int o = PreferenceHelper.getInstance(context).ri(PreferenceHelper.KEY_GAMES_WON_IN_ROW);
        int n;
        if (o == PreferenceHelper.STATUS_INT_NOT_FOUND) {
            PreferenceHelper.getInstance(context).w(PreferenceHelper.KEY_GAMES_WON_IN_ROW, 1);
            n = o + 1;
        } else {
            n = o + 1;
            PreferenceHelper.getInstance(context).w(PreferenceHelper.KEY_GAMES_WON_IN_ROW, n);
        }
        if (l != null) l.onGamesWonInRow(n);
    }

    /**
     * Re-initializes <b>'games won in a row'</b> property to 0
     *
     * @param context Activity context
     */
    public static void resetGamesWonInRow(Context context) {
        PreferenceHelper.getInstance(context).w(PreferenceHelper.KEY_GAMES_WON_IN_ROW, 0);
    }

    /**
     * Listener to broadcast statistics changes
     *
     * @param l {@link OnAchievementUnlockedListener} object
     */
    public static void setOnAchievementUnlockedListener(OnAchievementUnlockedListener l) {
        StatHelper.l = l;
    }
}
