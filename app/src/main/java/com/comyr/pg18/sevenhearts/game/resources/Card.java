package com.comyr.pg18.sevenhearts.game.resources;

import android.annotation.TargetApi;
import android.os.Build;

import com.comyr.pg18.sevenhearts.game.resources.constants.Constants;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Card {
    private String id;        // used to match cards
    private int rank;
    private int suit;
    private String rankName;
    private String suitName;

    // used to set id for view
    private int identifier;

    /**
     * initialize card with rank and suit as integers
     *
     * @param rank in integer
     * @param suit in integer
     */
    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
        initCardInfo();
    }

    public String getId() {
        return id;
    }

    public int getRank() {
        return rank;
    }

    public int getSuit() {
        return suit;
    }

    public String getRankName() {
        return rankName;
    }

    public String getSuitName() {
        return suitName;
    }

    /**
     * initiates the card
     * TODO : check target api
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void initCardInfo() {
        this.rankName = Constants.CARDS[this.rank];
        this.suitName = Constants.SUITS[this.suit];
        this.id = new String(getMD5(rankName + suitName), StandardCharsets.UTF_8);
        this.identifier = rank + (10 * suit);
    }

    public int getIdentifier() {
        return identifier;
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
     * compares ranks of two cards
     *
     * @param c card to compare with
     * @return equal, lower or greater
     */
    public int compareCardRankWith(Card c) {
        if (this.rank == c.getRank()) return Constants.STATUS_EQUAL;
        else if (this.rank > c.getRank()) return Constants.STATUS_GREATER;
        else
            return Constants.STATUS_LOWER;
    }

    /**
     * checks if suits of two cards are equal
     *
     * @param c card to compare with
     * @return true if equal, false otherwise
     */
    public boolean isSuitEqual(Card c) {
        return this.suit == c.getSuit();
    }

    /**
     * returns rank of the next card
     *
     * @return rank
     */
    public int getNextCardRank() {
        return this.rank + 1;
    }

    /**
     * used to get previous card's rank
     *
     * @return rank
     */
    public int getPrevCardRank() {
        return this.rank - 1;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        Card c = (Card) obj;
        return id.equals(c.getId());
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return rankName + " of " + suitName;
    }

    public String getBackgroundResourceName() {
        return rankName.toLowerCase() + "_of_" + suitName.toLowerCase();
    }

    public int compareTo(Card c) {
        return compareCardRankWith(c);
    }
}
