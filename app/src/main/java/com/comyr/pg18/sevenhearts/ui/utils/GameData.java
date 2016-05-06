package com.comyr.pg18.sevenhearts.ui.utils;

import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
 * Created by pranav on 5/2/16.
 * In package com.comyr.pg18.sevenhearts.ui.utils
 */
public class GameData {
    public static String thisPlayerName = "Player1";

    // makes sure if last name is not equal to current name
    private static String last;

    private static Stack<String> playerNames;
    private static String[] names = {"Lee", "Ian", "Sid", "Ted", "Rob", "Van", "Tom", "Kevin", "Leo", "Jam", "Sky", "Rey", "Chris", "Jim", "Pat", "Pro", "Jack", "Tim", "Fred", "Matt", "Ron", "Wes", "Rod", "Max", "Mac", "Lex", "Geo", "Ham", "Gus", "Fed", "Eli", "John", "Yoda", "Dan", "Luke"};

    public static void initData() {
        playerNames = new Stack<>();
        for (String n : names) {
            playerNames.push(n);
        }
    }

    public static String getRandomPlayerName() {
        Collections.shuffle(playerNames, new Random(System.nanoTime()));
        return playerNames.pop();
    }
}
