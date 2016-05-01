package com.comyr.pg18.sevenhearts.game.resources.constants;

public class Constants {
    public static final String[] CARDS = {"Ace", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
            "Jack", "Queen", "King"};
    public static final String[] SUITS = {"Hearts", "Spades", "Clubs", "Diamonds"};

    public static final int STATUS_INT_NOT_FOUND = -1;
    public static final int STATUS_LOWER = -1;
    public static final int STATUS_GREATER = 1;
    public static final int STATUS_EQUAL = 0;
    public static final int STATUS_MOVE_INVALID = -99;
    public static final int STATUS_MOVE_VALID = -98;
    public static final int STATUS_CHOICE_INVALID = -97;

    public static final String MESSAGE_PLAYER_EXCEPTION = "General player exception";
    public static final String MESSAGE_PLAYER_NOT_FOUND = "Player not found";
    public static final String MESSAGE_CARD_EXCEPTION = "General card exception";
    public static final String MESSAGE_CARD_NOT_FOUND = "Card not found";
    public static final String MESSAGE_NULL_TABLE = "Null table";

    public static final int END_HIGH = 1;
    public static final int END_LOW = -1;

    public static String getNameForCardIndex(int i) {
        return CARDS[i];
    }
}
