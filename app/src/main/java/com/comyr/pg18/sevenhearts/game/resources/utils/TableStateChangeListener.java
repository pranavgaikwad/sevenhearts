package com.comyr.pg18.sevenhearts.game.resources.utils;

import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.game.resources.Table;

public interface TableStateChangeListener {
    void onCardAddedToTable(Table t, Card c);

    void onCardRemovedFromTable(Table t, Card c);

    void onPlayerAddedToTable(Table t, Player p);

    void onPlayerRemovedFromTable(Table t, Player p);

    void onSuitsRefreshed(Table t);

    void onTableFull(Table t);

    void onPlayerWon(Player p);
}
