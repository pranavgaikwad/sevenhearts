package com.comyr.pg18.sevenhearts.ui.utils;

import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
 * Created by pranav
 */
public class GameData {
    /**
     * player names
     */
    private static final String[] names = {"Lee", "Ian", "Sid", "Ted", "Rob", "Van", "Tom", "Kevin", "Leo", "Jam", "Sky", "Rey", "Chris", "Jim", "Pat", "Pro", "Jack", "Tim", "Fred", "Matt", "Ron", "Wes", "Rod", "Max", "Mac", "Lex", "Geo", "Ham", "Gus", "Fed", "Eli", "John", "Yoda", "Dan", "Luke"};
    /**
     * human player who is playing the game
     */
    public static String thisPlayerName = "Player1";
    /**
     * stack of different in-built player names
     * makes sure that unique player names are
     * generated everytime game starts
     */
    private static Stack<String> playerNames;

    /**
     * initiates an empty stack of names
     */
    public static void initData() {
        playerNames = new Stack<>();
        for (String n : names) {
            playerNames.push(n);
        }
    }

    /**
     * returns a random name from stack of player names
     *
     * @return {@link String} player name
     */
    public static String getRandomPlayerName() {
        Collections.shuffle(playerNames, new Random(System.nanoTime()));
        return playerNames.pop();
    }
}
