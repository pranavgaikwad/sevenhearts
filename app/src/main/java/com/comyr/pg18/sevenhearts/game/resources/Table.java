package com.comyr.pg18.sevenhearts.game.resources;

import com.comyr.pg18.sevenhearts.game.resources.constants.Cards;
import com.comyr.pg18.sevenhearts.game.resources.constants.Constants;
import com.comyr.pg18.sevenhearts.game.resources.constants.Suits;
import com.comyr.pg18.sevenhearts.game.resources.utils.OnTableStateChangedListener;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.NullTableException;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.PlayerNotFoundException;

import java.util.ArrayList;
import java.util.Iterator;

public class Table {
    /**
     * this arraylist holds the list of cards that can be played by player (cards are open)
     */
    private static ArrayList<Card> openCards = new ArrayList<Card>();
    /**
     * static instance of the table
     */
    private static Table instance = null;
    /**
     * cards on the table are arranged in suits
     * these suits are refreshed after every
     * card that is placed on the table
     */
    private final ArrayList<Suit> localSuits = new ArrayList<Suit>();
    /**
     * this lock is used on index of the current player @see Table#currentPlayerIndex
     * avoids concurrent update on #currentPlayerIndex
     */
    private final Object lock = new Object();
    /**
     * deck of cards registered with the table
     */
    private Deck deck;
    /**
     * list of players that will hold all the registered players
     */
    private ArrayList<Player> players;
    /**
     * cards placed on table
     */
    private ArrayList<Card> cards;
    /**
     * Current player is the player who will play current move
     * this variable keeps track of current player from #players list
     */
    private int currentPlayerIndex;
    /**
     * used to broadcast changed table state
     */
    private OnTableStateChangedListener l;
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
    public Table(OnTableStateChangedListener l, Player... players) {
        // TODO Auto-generated constructor stub
        this.players = new ArrayList<Player>();
        this.cards = new ArrayList<Card>();
        this.l = l;
        this.deck = new Deck();
        registerPlayersToTable(players);
        createLocalSuits();
        currentPlayerIndex = 0;
    }

    /**
     * resets table instance and all the static variables associated with it
     * Table is ready to be re-started after call to this method
     */
    public static void reset() {
        if (instance != null) {
            synchronized (instance) {
                instance = null;
            }
            openCards = new ArrayList<>();
        }
    }

    public static Table getInstance(OnTableStateChangedListener l, Player... players) {
        if (instance == null) {
            instance = new Table(l, players);
        }
        return instance;
    }

    public static Table getInstance() throws NullTableException {
        if (instance != null) return instance;
        throw new NullTableException(Constants.MESSAGE_NULL_TABLE);
    }

    /**
     * returns possible available moves for given player
     *
     * @param p {@link Player} for whom moves are to be checked
     * @return cards as an arraylist
     */
    public static ArrayList<Card> getAvailableMovesFor(Player p) {
        ArrayList<Card> playerCards = p.getCards();
        ArrayList<Card> availableCards = new ArrayList<Card>();
        Iterator<Card> outer = playerCards.iterator();
        while (outer.hasNext()) {
            Card c1 = outer.next();
            Iterator<Card> inner = Table.openCards.iterator();
            while (inner.hasNext()) {
                Card c2 = inner.next();
                if (c1 != null)
                    if (c2 != null)
                        if (c1.equals(c2))
                            availableCards.add(c1);
            }
        }
        return availableCards;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    /**
     * passes move to the next player
     */
    public void incrementCurrentPlayerIndex() {
        if (players.size() != 0) {
            synchronized (lock) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }
        }
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
        deck.shuffle();
        updateTableStatus();
    }

    /**
     * distributes cards from #deck to all the #players
     */
    public void distributeCards() {
        deck.distributeCardsToPlayers(this);
        try {
            Player p = whoHasSevenOfHearts();
            if (p != null) {
                synchronized (lock) {
                    currentPlayerIndex = getPlayerIndex(p);
                }
            }
            else
                throw new PlayerNotFoundException();
        } catch (PlayerNotFoundException e) {
        }
    }

    /**
     * removes all #cards from the table
     */
    public void removeAllCards() {
        Iterator<Card> it = cards.iterator();
        while (it.hasNext()) {
            Card c = it.next();
            it.remove();
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
        checkAllPageSure();
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
        synchronized (cards) {
            cards.add(c);
        }
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
        Iterator<Card> it = Table.openCards.iterator();
        while (it.hasNext()) {
            Card c1 = it.next();
            if (c.equals(c1)) return true;
        }
        return false;
    }

    /**
     * provokes the re-arrangement of the new
     * cards according to suits
     */
    private void updateSuits() {
        Iterator<Card> outer = cards.iterator();
        while (outer.hasNext()) {
            Card c = outer.next();
            Iterator<Suit> inner = localSuits.iterator();
            while (inner.hasNext()) {
                Suit s = inner.next();
                if (c.getSuit() == s.getSuit()) s.addNewCard(c);
            }
        }
        l.onSuitsRefreshed(this);
    }

    /**
     * updates the current available moves
     */
    private void updateOpenCards() {
        Table.openCards = new ArrayList<Card>();
        if (isEmpty()) {
            synchronized (openCards) {
                openCards.add(new Card(Cards.SEVEN, Suits.HEARTS));
            }
        }
        else {
            Iterator<Suit> it = localSuits.iterator();
            while (it.hasNext()) {
                Suit s = it.next();
                openCards.addAll(s.availableCards);
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
     * checks if table can accomodate cards anymore or not
     *
     * @return true if full, false otherwise
     */
    public boolean isTableFull() {
        return cards.size() == 52;
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
        return cards.isEmpty();
    }

    /**
     * returns the player who currently has Seven Of Hearts
     *
     * @return player {@link Player} with seven of hearts
     */
    public Player whoHasSevenOfHearts() {
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (p.hasCard(new Card(Cards.SEVEN, Suits.HEARTS))) return p;
        }
        return null;
    }

    /**
     * checks if any of the players is all page sure at
     * current point of time.
     */
    public void checkAllPageSure() {
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (isAllPageSure(p)) p.getL().onOnAllPageSure(p);
        }
    }

    /**
     * checks if the given {@link Player} is all page sure
     *
     * @param p {@link Player} object to check all page sure
     * @return true if given player is all page sure, false otherwise
     */
    private synchronized boolean isAllPageSure(Player p) {
        Iterator<Card> i = p.getCards().iterator();
        while (i.hasNext()) {
            Card c = i.next();
            if (!isCardSure(c, p)) return false;
        }
        return true;
    }

    /**
     * checks if the given {@link Card} can be played, by specified {@link Player}
     *
     * @param c {@link Card} to be checked against available moves
     * @return true if card can be played, false otherwise.
     */
    private boolean isCardSure(Card c, Player p) {
        if (c.getRank() == 6) return true;
        
        if (c.getRank() < 6) {
            if (p.hasCard(new Card(c.getRank() + 1, c.getSuit()))) return true;
            if (hasCard(new Card(c.getRank() + 1, c.getSuit()))) return true;
            else return false;
        } else {
            if (p.hasCard(new Card(c.getRank() - 1, c.getSuit()))) return true;
            if (hasCard(new Card(c.getRank() - 1, c.getSuit()))) return true;
            else return false;
        }
    }

    /**
     * checks if given card is already placed on the table
     * @param c card to check
     * @return true if placed, false otherwise
     */
    private boolean hasCard(Card c) {
        Iterator<Suit> it = localSuits.iterator();
        while (it.hasNext()) {
            Suit s = it.next();
            if (c.getSuit() == s.getSuit()) if (s.contains(c)) return true;
        }
        return false;
    }

    /**
     * returns string representation of possible moves for current
     * status of the table
     *
     * @return open cards
     */
    public String getOpenCardsAsString() {
        String oc = "Available moves : " + "\n";
        for (Card c : Table.openCards) {
            if (c == null) System.out.println("c null");
            else oc = oc + c.toString() + "\n";
        }
        return oc;
    }

    /**
     * players are stored in an arraylist for given table instance
     * this method returns the index of the player in this array list. @see Table#players
     * @param p player to get index for
     * @return index of the player
     * @throws PlayerNotFoundException if player is not available on the current table
     */
    public int getPlayerIndex(Player p) throws PlayerNotFoundException {
        int i = 0;
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player p1 = it.next();
            if (p1.equals(p)) return i;
            i++;
        }
        throw new PlayerNotFoundException(Constants.MESSAGE_PLAYER_NOT_FOUND);
    }

    /**
     * returns current status of the table as {@link String} object
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
     * returns status of cards on table as {@link String} object
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
