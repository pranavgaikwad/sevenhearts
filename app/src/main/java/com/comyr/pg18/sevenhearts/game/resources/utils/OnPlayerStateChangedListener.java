package com.comyr.pg18.sevenhearts.game.resources.utils;

import com.comyr.pg18.sevenhearts.game.resources.Player;

public interface OnPlayerStateChangedListener {
    void onPlayerCardsExhausted(Player p);

    void onPlayerCardsExhausted(Player p, String status);

    void onPlayerSuitsRefreshed(Player p);

    void onOnAllPageSure(Player p);
}
