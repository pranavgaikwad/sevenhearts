package com.comyr.pg18.sevenhearts.game.resources.utils;

import com.comyr.pg18.sevenhearts.game.resources.Player;

public interface PlayerStateChangeListener {
    public void onPlayerCardsExhausted(Player p);

    public void onPlayerCardsExhausted(Player p, String status);
}
