package com.comyr.pg18.sevenhearts.stats;

import android.content.Context;

import com.comyr.pg18.sevenhearts.stats.listeners.OnAchievementUnlockedListener;
import com.comyr.pg18.sevenhearts.utils.PreferenceHelper;

/**
 * @author Pranav
 * <h1>Game statistics manipulation</h1>
 * <p>This class has been created to manipulate game statistics.
 * Game statistics basically include numbers that keep track of below
 * mentioned properties.</p>
 * <ul>
 * <li>Total games played</li>
 * <li>Total games won</li>
 * <li>Games won in a row</li>
 * <li>Games played in one instance of the application</li>
 * <li>Total points scored (To be decided)</li>
 * </ul>
 * <p>This class is used to maintain achievements and leaderboards in Google
 * play games console.</p>
 */
public class StatHelper {
    /**
     * This class has various methods that perform operations on data
     * related to player's statistics. For every such operation that
     * takes place on player's statistics, {@link #l} will be used to call
     * corresponding methods of {@link OnAchievementUnlockedListener}
     * Whichever class that implements {@link OnAchievementUnlockedListener} will be
     * able to receive statistics data.
     * @see OnAchievementUnlockedListener#onGamesPlayed(int)
     * @see OnAchievementUnlockedListener#onGamesWon(int)
     * @see OnAchievementUnlockedListener#onGamesWonInRow(int)
     */
    public static OnAchievementUnlockedListener l;

    /**
     * Increments the count of total games played by the user by one.
     *
     * @param context {@link Context} of activity that calls this method.
     */
    public static void incrementGamesPlayed(Context context) {
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
     * Increments games won by the player by one.
     *
     * @param context {@link Context} of activity that calls this method.
     */
    public static void incrementGamesWon(Context context) {
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
     * Increments games won in row by one.
     *
     * @param context {@link Context} of activity that calls this method.
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
     * Re-initializes <b>'games won in a row'</b> to zero.
     *
     * @param context {@link Context} of activity that calls this method.
     */
    public static void resetGamesWonInRow(Context context) {
        PreferenceHelper.getInstance(context).w(PreferenceHelper.KEY_GAMES_WON_IN_ROW, 0);
    }

    /**
     * Sets #l to given value.
     *
     * @param l {@link OnAchievementUnlockedListener} object of the class that implements this interface.
     */
    public static void setOnAchievementUnlockedListener(OnAchievementUnlockedListener l) {
        StatHelper.l = l;
    }
}
