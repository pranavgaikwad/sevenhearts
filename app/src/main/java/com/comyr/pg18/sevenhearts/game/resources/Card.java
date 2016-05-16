package com.comyr.pg18.sevenhearts.game.resources;

import com.comyr.pg18.sevenhearts.game.resources.constants.Constants;
import com.comyr.pg18.sevenhearts.game.resources.utils.OnCardStateChangedListener;

public class Card {
    /**
     * card id. used in @see Card#equals
     */
    private String id;
    /**
     * card rank
     */
    private int rank;
    /**
     * suit to which this card belongs
     */
    private int suit;
    /**
     * string representation of the rank @see Constants#CARDS
     */
    private String rankName;
    /**
     * string representation of the suit @see Constants#SUITS
     */
    private String suitName;
    /**
     * unique identifier. (currently not used anywhere. to be used in {@link com.comyr.pg18.sevenhearts.game.solution.Solver})
     */
    private int identifier;
    /**
     * Listener that keeps track of changes in state of this card.
     */
    private OnCardStateChangedListener l;

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
    private void initCardInfo() {
        this.rankName = Constants.CARDS[this.rank];
        this.suitName = Constants.SUITS[this.suit];
        this.id = rankName + suitName;
        this.identifier = rank + (10 * suit);
    }

    public int getIdentifier() {
        return identifier;
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

    public void setOnCardStateChangeListener(OnCardStateChangedListener l) {
        this.l = l;
    }

    public void makeSure() {
        if (l != null) l.onCardTurnedSure();
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

    public String getBackgroundResourceNameOnSure() {
        return getBackgroundResourceName() + "_o";
    }

    public int compareTo(Card c) {
        return compareCardRankWith(c);
    }
}
