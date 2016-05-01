package com.comyr.pg18.sevenhearts.game.resources;

import android.annotation.TargetApi;
import android.os.Build;

import com.comyr.pg18.sevenhearts.game.resources.constants.Constants;
import com.comyr.pg18.sevenhearts.game.resources.utils.PlayerStateChangeListener;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.CardNotFoundException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Player {
    private String id;
    private String name;

    // keeps the sum of ranks of
    // all possessed cards
    private int score;

    private ArrayList<Card> cards;
    private PlayerStateChangeListener l;

    /**
     * creates new player with
     *
     * @param name player name
     * @param l    keeps updated with current player state {@link PlayerStateChangeListener}
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public Player(String name, PlayerStateChangeListener l) {
        this.name = name;
        this.l = l;
        this.id = new String(getMD5(name), StandardCharsets.UTF_8);
        init();
    }

    /**
     * returns the extremity index for current suit
     * extremity index is the value of high value cards
     * that player currently has. these cards are to be
     * played before other cards.
     *
     * @param s suit {@link com.comyr.pg18.sevenhearts.game.resources.constants.Suits}
     * @return extremity value for given suit
     */
    private static int getExtremityEndForSuit(int s) {
        return 0;
    }

    /**
     * initialization tasks not allowed in default
     * constructor
     */
    public void init() {
        cards = new ArrayList<Card>();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    /**
     * give card to the player
     * giving card changes the state of the player.
     * thus, giveCard() checks if the player is out of cards or not
     *
     * @param c card
     */
    public void giveCard(Card c) {
        cards.add(c);
        if (areCardsExhausted()) l.onPlayerCardsExhausted(this);
        updateScore();
    }

    /**
     * gets md5 for given string
     *
     * @param s string
     * @return
     */
    private byte[] getMD5(String s) {
        byte[] str = s.getBytes();
        try {
            MessageDigest d = MessageDigest.getInstance("MD5");
            return d.digest(str);
        } catch (NoSuchAlgorithmException e) {
            return str;
        }
    }

    /**
     * plays given card
     *
     * @param c {@link Card} should belong to current player
     * @return {@link Card} that was added to table, null if move not played
     */
    public Card playCard(Card c) {
        int index;
        try {
            index = getIndexCard(c);
        } catch (CardNotFoundException e) {
            return null;
        }
        Card ct = cards.get(index);
        System.out.println("index : " + String.valueOf(index));
        cards.remove(index);
        if (areCardsExhausted())
            l.onPlayerCardsExhausted(this);
        updateScore();
        return ct;
    }

    /**
     * updates score for given player
     */
    private void updateScore() {
        score = 0;
        for (Card c : cards)
            score = score + c.getRank() + 1; // 2
    }

    /**
     * get index for current card
     * index is the position of the card
     * in player's hand
     *
     * @param c card to check index for
     * @return index as integer
     * @throws CardNotFoundException if given card is not with the player
     */
    public int getIndexCard(Card c) throws CardNotFoundException {
        int i = 0;
        for (Card crd : cards) {
            if (c.equals(crd)) return i;
            i++;
        }
        throw new CardNotFoundException(Constants.MESSAGE_CARD_NOT_FOUND);
    }

    /**
     * checks if the player is out of cards
     *
     * @return true if player is out of cards, false otherwise
     */
    private boolean areCardsExhausted() {
        if (cards.isEmpty()) return true;
        for (Card c : cards) {
            if (c != null) return false;
        }
        return true;
    }

    /**
     * checks if the player has this card
     *
     * @param c card to check
     * @return true if has, false otherwise
     */
    public boolean hasCard(Card c) {
        for (Card c1 : this.cards) if (c.equals(c1)) return true;
        return false;
    }

    /**
     * returns user cards
     *
     * @return
     */
    public ArrayList<Card> getCards() {
        return cards;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        Player p = (Player) obj;
        if (p.getId().equals(id)) return true;
        return false;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "Name : " + this.name + " Id : " + this.id;
    }

    /**
     * returns current status of the player as a string
     *
     * @return status
     */
    public String getStatus() {
        String status = "";
        for (Card c : cards)
            status = status + c.toString() + "\n";
        return status;
    }
}
