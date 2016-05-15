package com.comyr.pg18.sevenhearts.stats.listeners;

/**
 * @author Pranav
 *
 */
public interface OnAchievementUnlockedListener {
    void onGamesPlayed(int count);

    void onGamesWon(int count);

    void onGamesWonInRow(int count);
}
