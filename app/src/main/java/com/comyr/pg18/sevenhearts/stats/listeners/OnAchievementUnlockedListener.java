package com.comyr.pg18.sevenhearts.stats.listeners;

/**
 * Created by pranav on 5/13/16.
 * On 1:20 PM
 */
public interface OnAchievementUnlockedListener {
    void onGamesPlayed(int count);

    void onGamesWon(int count);

    void onGamesWonInRow(int count);
}
