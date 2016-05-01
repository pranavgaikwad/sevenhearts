package com.comyr.pg18.sevenhearts.game.resources.utils;

import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.game.resources.Table;

public interface TableStateChangeListener {
    public void onCardAddedToTable(Table t, Card c);

    public void onCardRemovedFromTable(Table t, Card c);

    public void onPlayerAddedToTable(Table t, Player p);

    public void onPlayerRemovedFromTable(Table t, Player p);

    public void onSuitsRefreshed(Table t);

    public void onTableFull(Table t);

    public void onPlayerWon(Player p);
}
