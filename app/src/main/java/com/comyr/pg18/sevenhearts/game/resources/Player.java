package com.comyr.pg18.sevenhearts.game.resources;

import com.comyr.pg18.sevenhearts.game.resources.constants.Constants;
import com.comyr.pg18.sevenhearts.game.resources.constants.Suits;
import com.comyr.pg18.sevenhearts.game.resources.utils.PlayerStateChangeListener;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.CardNotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.sort;

public class Player {
    private String id;
    private String name;

    // keeps the sum of ranks of
    // all possessed cards
    private int score;

    private ArrayList<Card> cards;
    private PlayerStateChangeListener l;

    private ArrayList<Suit> localSuits;

    /**
     * creates new player with
     *
     * @param name player name
     * @param l    keeps updated with current player state {@link PlayerStateChangeListener}
     */
    public Player(String name, PlayerStateChangeListener l) {
        this.name = name;
        this.l = l;
        this.id = name;
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
        localSuits = new ArrayList<Suit>();
        localSuits.add(new Suit(Suits.CLUBS));
        localSuits.add(new Suit(Suits.DIAMONDS));
        localSuits.add(new Suit(Suits.HEARTS));
        localSuits.add(new Suit(Suits.SPADES));
    }

    public ArrayList<Suit> getLocalSuits() {
        return localSuits;
    }

    public boolean isAllPageSure() {
        return equalLists(cards, Table.getAvailableMovesFor(this));
    }

    private boolean equalLists(List<Card> one, List<Card> two) {
        if (one == null && two == null) {
            return true;
        }

        if ((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()) {
            return false;
        }

        //to avoid messing the order of the lists we will use a copy
        //as noted in comments by A. R. S.
        one = new ArrayList<Card>(one);
        two = new ArrayList<Card>(two);

        sort(one, new Comparator<Card>() {
            @Override
            public int compare(Card lhs, Card rhs) {
                return lhs.compareTo(rhs);
            }
        });
        sort(two, new Comparator<Card>() {
            @Override
            public int compare(Card lhs, Card rhs) {
                return lhs.compareTo(rhs);
            }
        });
        return one.equals(two);
    }

    /**
     * re-arranges all the cards into suits
     *
     * @param l state change listener {@link PlayerStateChangeListener}
     */
    private void updateSuits(PlayerStateChangeListener l) {
        Iterator<Suit> suitIterator = localSuits.iterator();
        while (suitIterator.hasNext()) {
            Suit s = suitIterator.next();
            s.removeAllCards();
        }

        Iterator<Card> outer = cards.iterator();
        while (outer.hasNext()) {
            Iterator<Suit> inner = localSuits.iterator();
            Card c = outer.next();
            while (inner.hasNext()) {
                Suit s = inner.next();
                if (c.getSuit() == s.getSuit()) s.addNewCard(c);
            }
        }

        cards = new ArrayList<>();

        suitIterator = localSuits.iterator();
        while (suitIterator.hasNext()) {
            Suit s = suitIterator.next();
            s.sort();
            cards.addAll(s.getCards());
        }
        l.onPlayerSuitsRefreshed(this);
    }

    public void untakeMove(Card ct) {
        giveCard(ct);
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
        synchronized (cards) {
            cards.add(c);
        }
        updatePlayerState();
    }

    private void updatePlayerState() {
        updateScore();
        updateSuits(l);
        // checkAllPageSure();
    }

    private void checkAllPageSure() {
        if (isAllPageSure()) l.onOnAllPageSure(this);
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
        cards.remove(index);
        if (areCardsExhausted())
            l.onPlayerCardsExhausted(this);
        updatePlayerState();
        return ct;
    }

    public void removeAllCards() {
        Iterator<Card> it = cards.iterator();
        while (it.hasNext()) {
            Card c = it.next();
            it.remove();
        }
        updateSuits(l);
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
        Iterator<Card> it = cards.iterator();
        while (it.hasNext()) {
            Card crd = it.next();
            if (crd.equals(c)) return i;
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
        return cards.isEmpty();
    }

    /**
     * checks if the player has this card
     *
     * @param c card to check
     * @return true if has, false otherwise
     */
    public boolean hasCard(Card c) {
        Iterator<Card> it = cards.iterator();
        while (it.hasNext()) {
            Card playerCard = it.next();
            if (playerCard.equals(c))
                return true;
        }
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
        return p.getId().equals(id);
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
