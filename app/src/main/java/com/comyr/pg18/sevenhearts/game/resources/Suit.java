package com.comyr.pg18.sevenhearts.game.resources;

import com.comyr.pg18.sevenhearts.game.resources.constants.Cards;
import com.comyr.pg18.sevenhearts.game.resources.constants.Constants;
import com.comyr.pg18.sevenhearts.game.resources.constants.Suits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Random;

public class Suit {
    /**
     * status suit empty constant
     */
    private final int SUIT_EMPTY = -1;
    /**
     * suit id. obtained from
     * Refer : {@link Suits}
     */
    public int suit;
    /**
     * suit name. obtained from
     * Refer : {@link Constants} @see Constants#SUITS
     */
    public String suitName;
    /**
     * open cards for current suit (Cards that can be played in current suit)
     */
    public ArrayList<Card> availableCards;
    /**
     * list of cards in the suit
     */
    private ArrayList<Card> cards;

    /**
     * empty contructor to be used on tables
     * returns empty containers for a suit
     */
    public Suit(int suit) {
        this.cards = new ArrayList<Card>();
        this.availableCards = new ArrayList<Card>();
        this.suit = suit;
        this.suitName = Constants.SUITS[suit];
        this.availableCards.add(new Card(Cards.SEVEN, this.getSuit()));
    }

    /**
     * creates suit with default values
     *
     * @param suit        {@link Suits} which suit ?
     * @param withDefault 1 if default values of cards are needed
     */
    public Suit(int suit, int withDefault) {
        if (withDefault == 1) {
            this.cards = new ArrayList<Card>();
            this.availableCards = new ArrayList<Card>();
            this.suit = suit;
            this.suitName = Constants.SUITS[suit];
            this.availableCards.add(new Card(Cards.SEVEN, this.getSuit()));
            init();
        }
    }

    /**
     * returns suit as integer
     *
     * @return suit
     */
    public int getSuit() {
        return suit;
    }

    /**
     * sets suit id {@link Suits} for current suit
     *
     * @param suit {@link Suits} id
     */
    public void setSuit(int suit) {
        this.suit = suit;
    }

    /**
     * get name of the current suit
     *
     * @return name of the suit
     */
    public String getSuitName() {
        return suitName;
    }

    /**
     * sets name for the current suit
     *
     * @param suitName {@link Constants} see SUITS array
     */
    public void setSuitName(String suitName) {
        this.suitName = suitName;
    }

    /**
     * returns {@link ArrayList} of cards in the current suit
     *
     * @return cards as an arraylist
     */
    public ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * sets cards in the current suit
     *
     * @param cards as an arraylist
     */
    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    /**
     * adds new card to given suit
     */
    public void addNewCard(Card c) {
        insertNewCard(c);
    }

    /**
     * checks if the suit is empty
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        Iterator<Card> it = cards.iterator();
        while (it.hasNext()) {
            Card c = it.next();
            if (c != null) return false;
        }
        return true;
    }

    /**
     * inserts new card to the current suit
     *
     * @param c card to add
     */
    public void insertNewCard(Card c) {
        if (!contains(c)) {
            synchronized (cards) {
                cards.add(c);
            }
        }
        updateAvailableCards();
    }

    public void removeAllCards() {
        Iterator<Card> iter = cards.iterator();
        while (iter.hasNext()) {
            Card c = iter.next();
            iter.remove();
        }
    }

    /**
     * updates the list of next possible moves
     */
    private void updateAvailableCards() {
        availableCards = new ArrayList<Card>();
        Card h = getCardForRank(getHighestCardRank());
        Card l = getCardForRank(getLowestCardRank());
        if (isEmpty()) availableCards.add(new Card(Cards.SEVEN, this.getSuit()));
        else if (h.getRank() == 12 && l.getRank() == 0) {
        }
        else if (h.getRank() == 12) availableCards.add(getCardForRank(l.getRank() - 1));
        else if (l.getRank() == 0) availableCards.add(getCardForRank(h.getRank() + 1));
        else {
            availableCards.add(getCardForRank(l.getRank() - 1));
            availableCards.add(getCardForRank(h.getRank() + 1));
        }
    }

    /**
     * returns lowest rank in the suit
     *
     * @return rank
     */
    public int getLowestCardRank() {
        if (cards.isEmpty()) return SUIT_EMPTY;
        Card lowest = cards.get(0);
        Iterator<Card> it = cards.iterator();
        while (it.hasNext()) {
            Card c = it.next();
            if (c.compareCardRankWith(lowest) == Constants.STATUS_LOWER) lowest = c;
        }
        return lowest.getRank();
    }

    /**
     * returns highest rank in the suit
     *
     * @return highest rank
     */
    public int getHighestCardRank() {
        if (cards.isEmpty()) return SUIT_EMPTY;
        Card highest = cards.get(0);
        Iterator<Card> it = cards.iterator();
        while (it.hasNext()) {
            Card c = it.next();
            if (c.compareCardRankWith(highest) == Constants.STATUS_GREATER) highest = c;
        }
        return highest.getRank();
    }

    /**
     * returns card from that matches given rank
     *
     * @param rank
     * @return
     */
    public Card getCardForRank(int rank) {
        return new Card(rank, this.suit);
    }

    /**
     * initiates cards with default set of cards
     */
    public void init() {
        for (int i = 0; i < Constants.CARDS.length; i++)
            insertNewCard(new Card(i, suit));
    }

    /**
     * shuffles the suit randomly
     */
    public void shuffle() {
        long seed = System.nanoTime();
        Collections.shuffle(cards, new Random(seed));
    }

    /**
     * sort in an ascending order of card ranks
     */
    public void sort() {
        Collections.sort(cards, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {
                // TODO Auto-generated method stub
                return o1.compareCardRankWith(o2);
            }
        });
    }

    /**
     * checks if the suit contains given card
     *
     * @param card card to check
     * @return true if contains, false otherwise
     */
    public boolean contains(Card card) {
        Iterator<Card> it = cards.iterator();
        while (it.hasNext()) {
            Card c = it.next();
            if (card.equals(c))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        String str = "";
        str = str + "==> SUIT : " + this.suitName + "\n";
        for (Card c : cards) {
            str = str + c.toString() + "\n";
        }
        return str;
    }

}

