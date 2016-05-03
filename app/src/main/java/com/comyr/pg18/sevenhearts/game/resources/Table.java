package com.comyr.pg18.sevenhearts.game.resources;

import com.comyr.pg18.sevenhearts.game.resources.constants.Cards;
import com.comyr.pg18.sevenhearts.game.resources.constants.Constants;
import com.comyr.pg18.sevenhearts.game.resources.constants.Suits;
import com.comyr.pg18.sevenhearts.game.resources.utils.TableStateChangeListener;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.NullTableException;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.PlayerNotFoundException;

import java.util.ArrayList;

public class Table {
    private static ArrayList<Card> openCards = new ArrayList<Card>();
    private static Table instance = null;
    private final ArrayList<Suit> localSuits = new ArrayList<Suit>();
    private Deck deck;
    private ArrayList<Player> players;
    private ArrayList<Card> cards;
    private int currentPlayerIndex;
    private TableStateChangeListener l;

    // following boolean value checks if any of the player has won or not
    // true if current player is first player to get removed from the table
    // a player is removed from the table as soon as he/she is out of cards.
    private boolean isFirstPlayerToFinish = true;

    /**
     * default constructor with player names
     *
     * @param l       used to keep updated with current state of the table
     * @param players {@link Player} objects as an array
     */
    public Table(TableStateChangeListener l, Player... players) {
        // TODO Auto-generated constructor stub
        this.players = new ArrayList<Player>();
        this.cards = new ArrayList<Card>();
        this.l = l;
        this.deck = new Deck();
        registerPlayersToTable(players);
        createLocalSuits();
        currentPlayerIndex = 0;
    }

    public static void reset() {
        instance = null;
    }

    public static Table getInstance(TableStateChangeListener l, Player... players) {
        if (instance == null) {
            instance = new Table(l, players);
        }
        return instance;
    }

    public static Table getInstance() throws NullTableException {
        if (instance != null) return instance;
        throw new NullTableException(Constants.MESSAGE_NULL_TABLE);
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * passes move to the next player
     */
    public void incrementCurrentPlayerIndex() {
        if (players.size() != 0)
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * returns current player
     *
     * @return player {@link Player}
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * creates four local empty suits
     * used to contain cards thrown on
     * the table {@link Suit}
     */
    private void createLocalSuits() {
        localSuits.add(new Suit(Suits.CLUBS));
        localSuits.add(new Suit(Suits.DIAMONDS));
        localSuits.add(new Suit(Suits.HEARTS));
        localSuits.add(new Suit(Suits.SPADES));
    }

    /**
     * initiation tasks that are not allowed
     * to be in default constructor
     *
     */
    public void init() {
        this.deck.shuffle();
        updateTableStatus();
    }

    public void distributeCards() {
        this.deck.distributeCardsToPlayers();
        try {
            Player p = whoHasSevenOfHearts();
            if (p != null)
                this.currentPlayerIndex = getPlayerIndex(p);
            else
                throw new PlayerNotFoundException();
        } catch (PlayerNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void removeAllCards() {
        int i = cards.size() - 1;
        for (Card c : cards) {
            cards.remove(i);
            i--;
        }
        updateTableStatus();
    }

    /**
     * updates table status information
     * according to current status of
     * the game
     */
    private void updateTableStatus() {
        updateSuits();
        updateOpenCards();
        checkTableFull();
    }

    /**
     * checks if the table has all the cards
     * from {@link Deck}
     */
    private void checkTableFull() {
        if (isTableFull())
            l.onTableFull(this);
    }

    /**
     * addds a {@link Card} to current table
     *
     * @param c card to add
     */
    public void addCardToTable(Card c) {
        cards.add(c);
        l.onCardAddedToTable(this, c);
        updateTableStatus();
    }

    /**
     * wrapper for addCardToTable()
     *
     * @param c card to add
     * @return status check {@link Constants} for status information
     */
    public int addNewCardToTable(Card c) {
        if (!isMoveValid(c)) return Constants.STATUS_MOVE_INVALID;
        else {
            addCardToTable(c);
            return Constants.STATUS_MOVE_VALID;
        }
    }

    /**
     * checks if the current card is valid or not
     *
     * @param c {@link Card} to add
     * @return true if valid, false otherwise
     */
    public boolean isMoveValid(Card c) {
        for (Card c1 : Table.openCards) if (c.equals(c1)) return true;
        return false;
    }

    /**
     * provokes the re-arrangement of the new
     * cards according to suits
     */
    private void updateSuits() {
        for (Card c : cards)
            for (Suit s : localSuits) {
                if (c.getSuit() == s.getSuit()) s.addNewCard(c);
            }
        l.onSuitsRefreshed(this);
    }

    /**
     * updates the current available moves
     */
    private void updateOpenCards() {
        Table.openCards = new ArrayList<Card>();
        if (isEmpty())
            Table.openCards.add(new Card(Cards.SEVEN, Suits.HEARTS));
        else {
            for (Suit s : localSuits) {
                Table.openCards.addAll(s.availableCards);
            }
        }
    }

    /**
     * returns deck registered to current table
     *
     * @return {@link Deck}
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * returns registered players
     *
     * @return
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * wrapper for registering multiple players at a time
     *
     * @param players array of {@link Player}
     */
    public void registerPlayersToTable(Player... players) {
        for (Player p : players) {
            registerPlayerToTable(p);
        }
    }

    /**
     * registers a player with current table
     *
     * @param p {@link Player} to register
     */
    public void registerPlayerToTable(Player p) {
        players.add(p);
        l.onPlayerAddedToTable(this, p);
    }

    /**
     * checks if table cannot accomodate cards anymore
     *
     * @return true if full, false otherwise
     */
    public boolean isTableFull() {
        if (cards.size() == 52) return true;
        return false;
    }

    /**
     * removes player from the table
     *
     * @param p {@link Player} to remove
     */
    public void removePlayerFromTable(Player p) {
        if (isFirstPlayerToFinish) {
            isFirstPlayerToFinish = false;
            l.onPlayerWon(p);
        }
        try {
            players.remove(getPlayerIndex(p));
            l.onPlayerRemovedFromTable(this, p);
        } catch (PlayerNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * checks if the table has no cards
     *
     * @return true if no cards, false otherwise
     */
    public boolean isEmpty() {
        return this.cards.isEmpty();
    }

    /**
     * wrapper for getting available moves for current player
     *
     * @return
     */
    public ArrayList<Card> getAvailableMovesForCurrentPlayer() {
        Player p = this.players.get(currentPlayerIndex);
        return getAvailableMovesFor(p);
    }

    /**
     * returns possible available moves for given player
     *
     * @param p {@link Player} for whom moves are to be checked
     * @return cards as an arraylist
     */
    public ArrayList<Card> getAvailableMovesFor(Player p) {
        ArrayList<Card> playerCards = p.getCards();
        ArrayList<Card> availableCards = new ArrayList<Card>();
        for (Card c1 : playerCards) {
            for (Card c2 : Table.openCards)
                if ((c2 != null) && c1.equals(c2)) availableCards.add(c1);
        }
        return availableCards;
    }

    /**
     * returns the player who currently has Seven Of Hearts
     *
     * @return player {@link Player} with seven of hearts
     */
    public Player whoHasSevenOfHearts() {
        for (Player p : this.players) if (p.hasCard(new Card(Cards.SEVEN, Suits.HEARTS))) return p;
        return null;
    }

    /**
     * returns string representation of possible moves for current
     * status of the table
     *
     * @return open cards
     */
    public String getOpenCardsAsString() {
        String oc = "Available moves : " + "\n";
        if (Table.openCards == null) System.out.println("null opencards");
        for (Card c : Table.openCards) {
            if (oc == null) System.out.println("oc null");
            if (c == null) System.out.println("c null");
            else oc = oc + c.toString() + "\n";
        }
        return oc;
    }

    /**
     * returns index for the given player
     *
     * @param p player
     * @return index of the player
     * @throws PlayerNotFoundException if player is not available on the current table
     */
    public int getPlayerIndex(Player p) throws PlayerNotFoundException {
        int i = 0;
        for (Player pl : players) {
            if (pl.equals(p)) return i;
            i++;
        }
        throw new PlayerNotFoundException(Constants.MESSAGE_PLAYER_NOT_FOUND);
    }

    /**
     * returns current status of the table in {@link String} object
     *
     * @return status
     */
    public String getTableStatus() {
        String status = "==> Players : " + "\n";
        for (Player p : players)
            status = status + p.toString() + "\n";
        status = status + "==> Cards on table : " + "\n";
        status = status + String.valueOf(cards.size()) + "\n";
        return status;
    }

    /**
     * returns status of cards on table as a string description
     *
     * @return status of cards put on table
     * TODO: arrange in suits
     */
    public String getCardsStatus() {
        String status = "==> cards on table : " + "\n";
        for (Card c : cards) {
            status = status + c.toString() + "\n";
        }
        return status;
    }
}
