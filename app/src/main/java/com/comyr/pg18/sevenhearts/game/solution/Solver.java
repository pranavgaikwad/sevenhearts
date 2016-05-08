package com.comyr.pg18.sevenhearts.game.solution;

import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.game.resources.Suit;

import java.util.ArrayList;

public class Solver {

    /**
     * median value is rank of the most powerful card in the game
     * it is used to determine actual value of the card
     */
    private static final int MEDIAN_VALUE = 6;
    private static final int PIVOT_VALUE = 100;
    private static final int INFINITY = 99999;

    /**
     * value determines the importance of the card
     * a card is important when it is closer to 7
     *
     * @return value as an integer
     */
    private static int getCardValue(Card c) {
        int v = 0;
        int r = c.getRank();
        v = MEDIAN_VALUE - r;
        v = PIVOT_VALUE - positiveOf(v);
        return v;
    }

    /**
     * returns a favorable move for current state
     * of the game on the bases of possible cards
     * To obtain possible cards of a player,
     * @see com.comyr.pg18.sevenhearts.game.resources.Table#getAvailableMovesFor(Player)
     *
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
    public static Card getNextMove(ArrayList<Card> possibleCards) {
        return possibleCards.get(getIndexOfNextMove(possibleCards));
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