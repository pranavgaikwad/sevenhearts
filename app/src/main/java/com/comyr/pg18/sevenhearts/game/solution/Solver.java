package com.comyr.pg18.sevenhearts.game.solution;

import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.utils.GameData;

import java.util.ArrayList;
import java.util.Iterator;

public class Solver {

    /**
     * median value is rank of the most powerful card in the game
     * it is used to determine actual value of the card
     */
    private static final int MEDIAN_VALUE = 6;
    private static final int PIVOT_VALUE = 100;
    private static final int INFINITY = 99999;

    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;

    /**
     * Returns importance of the card as an integer named value.
     * This method is only used for medium difficulty.
     * @param c
     * @return
     */
    private static int getCardValue(Card c) {
        int v = 0;
        int r = c.getRank();
        v = MEDIAN_VALUE - r;
        v = PIVOT_VALUE - positiveOf(v);
        return v;
    }

    private static int getImportanceForCard(Card c, ArrayList<Card> allCards) {
        int baseImportance = getCardValue(c);
        Iterator<Card> it = allCards.iterator();
        while (it.hasNext()) {
            Card ct = it.next();
            if (ct.getSuit() == c.getSuit()) {
                baseImportance = baseImportance - getCardValue(ct);
            }
        }
        return baseImportance;
    }

    /**
     * returns a favorable move for current state
     * of the game on the bases of possible cards
     * To obtain possible cards of a player,
     * @return index of the card to play
     */
    private static int getIndexOfNextMove(ArrayList<Card> possibleCards) {
        int i = -1;
        int minimumValue = INFINITY;
        for (Card c : possibleCards) {
            i++;
            int v = getCardValue(c);
            if (v < minimumValue) {
                minimumValue = v;
            }
        }
        return i;
    }

    /**
     * returns next favorable move on the basis of possible cards provided
     * @see com.comyr.pg18.sevenhearts.game.resources.Table#getAvailableMovesFor(Player) to obtain possible moves
     * @param possibleCards available moves that <b>can</b> be played
     * @return {@link Card} object
     */
    public static Card getNextMove(ArrayList<Card> possibleCards, ArrayList<Card> allCards) {
        switch (GameData.DIFFICULTY) {
            case DIFFICULTY_HARD:
                return possibleCards.get(getIndexOfNextMove(possibleCards, allCards));
            case DIFFICULTY_MEDIUM:
                return possibleCards.get(getIndexOfNextMove(possibleCards));
        }
        return possibleCards.get(0);
    }

    private static int getIndexOfNextMove(ArrayList<Card> possibleCards, ArrayList<Card> allCards) {
        int importance = INFINITY;
        int i = -1;
        Iterator<Card> it = possibleCards.iterator();
        while (it.hasNext()) {
            Card c = it.next();
            i++;
            int imp = getImportanceForCard(c, allCards);
            if (imp < importance) importance = imp;
        }
        return i;
    }

    /**
     * returns the positive value for given integer
     *
     * @param i number
     * @return number * -1, if number is negative, number otherwise
     */
    private static int positiveOf(int i) {
        if (i < 0) return i * -1;
        return i;
    }

}