package com.comyr.pg18.sevenhearts.game.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileHelper {

    private static FileHelper instance;
    private final String FILE_COUNT = "/home/pranav/Desktop/cardgame/count.txt";
    private final String FILE_GAME = "/home/pranav/Desktop/cardgame/game.txt";


    public FileHelper() {
        // TODO Auto-generated constructor stub

    }

    public static FileHelper getInstance() {
        if (instance == null) {
            instance = new FileHelper();
        }
        return instance;
    }

    public void appendStringToCountFile(String s) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter(FILE_COUNT, true)));
            writer.append(s);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendStringToGameFile(String s) {
        printToTerminal(s);
    }

    public void printToTerminal(String s) {
        System.out.println(s);
    }

}
