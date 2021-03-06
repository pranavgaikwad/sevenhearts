package com.comyr.pg18.sevenhearts.game.resources;

import com.comyr.pg18.sevenhearts.game.resources.constants.Constants;
import com.comyr.pg18.sevenhearts.game.resources.constants.Suits;
import com.comyr.pg18.sevenhearts.game.resources.utils.OnPlayerStateChangedListener;
import com.comyr.pg18.sevenhearts.game.resources.utils.OnTableStateChangedListener;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.CardNotFoundException;
import com.comyr.pg18.sevenhearts.game.solution.Solver;

import java.util.ArrayList;
import java.util.Scanner;

public class Game implements OnTableStateChangedListener, OnPlayerStateChangedListener {

    private Table table;
    private Deck deck;

    /**
     * checks if the game is finished
     * used in main game loop
     */
    private boolean isFinished = false;

    /**
     * initiate the game with player names
     * TODO: constructor with player objects
     * instead of names
     *
     * @param names names of players
     */
    public Game(String... names) {
        // TODO Auto-generated constructor stub
        Player[] players = new Player[names.length];
        int i = 0;
        for (String name : names) {
            players[i] = new Player(name, this);
            i++;
        }
        table = new Table(this, players);
        table.init();
        deck = this.table.getDeck();
        init(players);
        table.distributeCards();
    }

    /**
     * initiates tasks that are not allowed
     * to be declared in default constructor
     *
     * @param players players objects as an array
     */
    public void init(Player... players) {

    }

    /**
     * main game loop
     *
     * @throws CardNotFoundException if matching card is not found with the player
     */
    public void start() throws CardNotFoundException {
        Scanner sc;
        int moves = 0;
        while (!isFinished) {
            moves++;
            Card c;
            String intro = "===========================================================\nMove : "
                    + String.valueOf(moves) + "\n" + "===========================================================\n";

            Player p = table.getCurrentPlayer();
            String info = "Current player : " + p.getName() + "\n\n" + table.getOpenCardsAsString() + "\n\nPlayer's cards : "
                    + p.getStatus() + "\n\n" + "Count : " + String.valueOf(p.getCards().size()) + "\n\n" + "Open moves for current player : "
                    + table.getAvailableMovesFor(p).toString() + "\n\n" + table.getCardsStatus();


            /**
             * checks if the player has any cards available
             * if no cards are available, player passes the move
             */
            if (table.getAvailableMovesFor(p).isEmpty()) {
                table.incrementCurrentPlayerIndex();
                continue;
            } else {
                c = Solver.getNextMove(table.getAvailableMovesFor(p), p.getCards());
            }
            if (c == null) {
                System.out.println("null card");
                table.incrementCurrentPlayerIndex();
                continue;
            } else {
                Card ct = p.playCard(c);
                int moveStatus = table.addNewCardToTable(c);
                if (moveStatus == Constants.STATUS_MOVE_INVALID)
                    System.out.println("Move invalid...");
            }
            table.incrementCurrentPlayerIndex();
        }
        ArrayList<Player> players = table.getPlayers();
        for (Player p : players) {
            System.out.println(p.getName() + " : " + String.valueOf(p.getScore()));
        }
    }

    /**
     * returns deck of the current game
     *
     * @return deck
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * returns table of the current game
     *
     * @return table
     */
    public Table getTable() {
        return table;
    }

    /**
     * returns suit for current suit name
     *
     * @param s suit name as a string
     * @return {@link Suits} integer for matching suit
     */
    private int getSuitForString(String s) {
        switch (s) {
            case "H":
                return Suits.HEARTS;

            case "C":
                return Suits.CLUBS;

            case "S":
                return Suits.SPADES;

            case "D":
                return Suits.DIAMONDS;
        }
        return Constants.STATUS_CHOICE_INVALID;
    }

    @Override
    public void onCardAddedToTable(Table t, Card c) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCardRemovedFromTable(Table t, Card c) {
        // TODO Auto-generated method stub
        System.out.println(c.toString() + " removed...");
    }

    @Override
    public void onPlayerAddedToTable(Table t, Player p) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPlayerRemovedFromTable(Table t, Player p) {
        // TODO Auto-generated method stub
        String removed = "==================================\n" + p.getName() + " removed...\n";

    }

    @Override
    public void onSuitsRefreshed(Table t) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTableFull(Table t) {
        // TODO Auto-generated method stub
        this.isFinished = true;

    }

    @Override
    public void onPlayerCardsExhausted(Player p, String status) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPlayerSuitsRefreshed(Player p) {

    }

    @Override
    public void onOnAllPageSure(Player p) {

    }

    @Override
    public void onPlayerWon(Player p) {
        // TODO Auto-generated method stub
        isFinished = true;

    }

    @Override
    public void onPlayerCardsExhausted(Player p) {
        // TODO Auto-generated method stub
        table.removePlayerFromTable(p);
    }
}
