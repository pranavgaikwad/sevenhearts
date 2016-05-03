package com.comyr.pg18.sevenhearts.ui.utils;

import java.util.Random;

/**
 * Created by pranav on 5/2/16.
 * In package com.comyr.pg18.sevenhearts.ui.utils
 */
public class GameData {
    public static String thisPlayerName = "Player1";

    private static String[] playerNames = {"Lee", "Ian", "Sid", "Ted", "Rob", "Van", "Tom", "Kevin", "Leo", "Jam", "Sky", "Rey", "Chris", "Jim", "Pat", "Pro", "Jack", "Tim", "Fred", "Matt", "Ron", "Wes", "Rod", "Max", "Mac", "Lex", "Geo", "Ham", "Gus", "Fed", "Eli", "John", "Yoda", "Dan", "Luke"};

    public static String getRandomPlayerName() {
        int low = 0;
        int high = playerNames.length;
        int i = new Random(System.nanoTime()).nextInt(999999);
        i = i % (high - low);
        return playerNames[i];
    }
}
