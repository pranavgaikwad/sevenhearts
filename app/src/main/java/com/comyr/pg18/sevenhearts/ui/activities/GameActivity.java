package com.comyr.pg18.sevenhearts.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comyr.pg18.sevenhearts.R;
import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.Deck;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.game.resources.Table;
import com.comyr.pg18.sevenhearts.game.resources.constants.Suits;
import com.comyr.pg18.sevenhearts.game.resources.utils.PlayerStateChangeListener;
import com.comyr.pg18.sevenhearts.game.resources.utils.TableStateChangeListener;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.NullTableException;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.PlayerNotFoundException;
import com.comyr.pg18.sevenhearts.ui.activities.base.CustomActivity;
import com.comyr.pg18.sevenhearts.ui.threads.GameThread;
import com.comyr.pg18.sevenhearts.ui.utils.FontUtils;
import com.comyr.pg18.sevenhearts.ui.utils.GameData;
import com.comyr.pg18.sevenhearts.ui.utils.helper.ActivityOptionHelper;
import com.comyr.pg18.sevenhearts.ui.views.CardView;
import com.comyr.pg18.sevenhearts.ui.views.PlayerView;

import java.util.ArrayList;

public class GameActivity extends CustomActivity implements PlayerStateChangeListener, TableStateChangeListener {

    // decides which player's information to display
    private final static String thisPlayerName = GameData.thisPlayerName;
    private static ArrayList<Player> players;
    private static Table table;
    private static Deck deck;
    private static Player thisPlayer;
    private static ArrayList<Card> thisPlayerCards;
    // player with current turn on the table
    private static Player currentPlayer;
    private final String TAG = "GameActivity";
    private LinearLayout playerCardsLayout;
    private LinearLayout playersLayout;
    private TextView mainDisplayTextView;
    private GameActivity gameActivity;
    // terminating condition for main game loop
    private boolean isFinished = false;
    private PlayerView currentPlayerView;
    // table variables
    private CardView heartsUpperCard, heartsLowerCard, diamondsUpperCard, diamondsLowerCard, spadesUpperCard, spadesLowerCard, clubsUpperCard, clubsLowerCard;

    private GameThread thread;

    public static Player getThisPlayer() {
        return thisPlayer;
    }

    public static Deck getDeck() {
        return deck;
    }

    public static ArrayList<Card> getThisPlayerCards() {
        return thisPlayerCards;
    }

    public static Table getTable() {
        return table;
    }

    public static ArrayList<Player> getPlayers() {
        return players;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setOptions(ActivityOptionHelper.getOptionsForActivity(ActivityOptionHelper.ACTIVITY_GAME));
        setContentView(R.layout.activity_game);

        gameActivity = this;

        initUI();

        initGameResources();

        //playGame();

        testThread();
    }

    public TextView getMainDisplayTextView() {
        return mainDisplayTextView;
    }

    private void testThread() {
        thread = new GameThread(gameActivity);
        thread.start();
    }

    private PlayerView getCurrentPlayerView() {
        SurfaceView sv = new SurfaceView(this);
        return getViewForPlayer(currentPlayer);
    }

    public PlayerView getViewForPlayer(Player player) {
        for (int i = 0; i < playersLayout.getChildCount(); i++) {
            PlayerView pv = (PlayerView) playersLayout.getChildAt(i);
            if (pv.getPlayer().equals(player)) {
                Log.d(TAG, "comparing..." + pv.getPlayer().toString() + " with " + player);
                return pv;
            }
        }
        return null;
    }

    private void initGameResources() {
        initTable();

        setupPlayers();

        setupPlayerCards();
    }

    public void emptyCallback() {
        resumeThread();
    }

    private void initTable() {
        Table.reset();
        String player2, player3, player4;
        player2 = GameData.getRandomPlayerName();
        player3 = GameData.getRandomPlayerName();
        player4 = GameData.getRandomPlayerName();
        String[] names = {thisPlayerName, player2, player3, player4};
        Player[] players = new Player[names.length];
        int i = 0;
        for (String name : names) {
            players[i] = new Player(name, this);
            i++;
        }
        table = Table.getInstance(this, players);
        table.init();
        deck = table.getDeck();
    }

    private Player getPlayerForName(String name) throws PlayerNotFoundException {
        for (Player p : players) {
            if (p.getName().equals(name)) return p;
        }
        throw new PlayerNotFoundException("In game activity, while getting this player");
    }

    private void setupPlayers() {
        try {
            players = Table.getInstance().getPlayers();
            thisPlayer = getPlayerForName(thisPlayerName);
            table.distributeCards();
            for (Player p : players) {
                if (!p.equals(thisPlayer)) {
                    PlayerView playerView = new PlayerView(this);
                    playerView.setPlayer(p);
                    playersLayout.addView(playerView);
                }
            }
        } catch (NullTableException e) {
            Log.d(TAG, e.getMessage());
        } catch (PlayerNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private void setupPlayerCards() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerCardsLayout.removeAllViews();
            }
        });

        // Log.d(TAG, "removed views");
        thisPlayerCards = thisPlayer.getCards();
        Log.d(TAG, thisPlayerCards.toString());
        int i = 0;
        for (Card c : thisPlayerCards) {
            final CardView cv = new CardView(this);
            cv.setCard(c);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Card clickedCard = ((CardView) v).getCard();

                    if (!table.isMoveValid(clickedCard)) return;

                    thread.currentMove = ((CardView) v).getCard();
                    thread.resume();
                }
            });
            if (i != 0)
                cv.setNegativeMargin();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    playerCardsLayout.addView(cv);
                }
            });
            i++;
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        playerCardsLayout = (LinearLayout) findViewById(R.id.linear_layout_player_cards);
        playersLayout = (LinearLayout) findViewById(R.id.linear_layout_players);
        mainDisplayTextView = (TextView) findViewById(R.id.text_view_main_display);
        mainDisplayTextView.setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_ELECTRONIC));
        ((TextView) findViewById(R.id.label_clubs_container)).setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_CARTWHEEL));
        ((TextView) findViewById(R.id.label_diamonds_container)).setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_CARTWHEEL));
        ((TextView) findViewById(R.id.label_hearts_container)).setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_CARTWHEEL));
        ((TextView) findViewById(R.id.label_spades_container)).setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_CARTWHEEL));
        heartsUpperCard = (CardView) findViewById(R.id.hearts_upper_card);
        heartsLowerCard = (CardView) findViewById(R.id.hearts_lower_card);
        diamondsUpperCard = (CardView) findViewById(R.id.diamonds_upper_card);
        diamondsLowerCard = (CardView) findViewById(R.id.diamonds_lower_card);
        clubsUpperCard = (CardView) findViewById(R.id.clubs_upper_card);
        clubsLowerCard = (CardView) findViewById(R.id.clubs_lower_card);
        spadesUpperCard = (CardView) findViewById(R.id.spades_upper_card);
        spadesLowerCard = (CardView) findViewById(R.id.spades_lower_card);
    }

    @Nullable
    public CardView getCardViewOnTableFor(Card c) {
        int s = c.getSuit();
        if (c.getRank() < 6) {
            switch (s) {
                case Suits.CLUBS:
                    return clubsLowerCard;

                case Suits.SPADES:
                    return spadesLowerCard;

                case Suits.DIAMONDS:
                    return diamondsLowerCard;

                case Suits.HEARTS:
                    return heartsLowerCard;
            }
        } else {
            switch (s) {
                case Suits.CLUBS:
                    return clubsUpperCard;

                case Suits.SPADES:
                    return spadesUpperCard;

                case Suits.DIAMONDS:
                    return diamondsUpperCard;

                case Suits.HEARTS:
                    return heartsUpperCard;
            }
        }
        return null;
    }

    @Override
    public void onPlayerCardsExhausted(Player p) {
        this.table.removePlayerFromTable(p);
        if (p.equals(thisPlayer)) Log.d(TAG, "player removed...");
    }

    @Override
    public void onPlayerCardsExhausted(Player p, String status) {
        this.table.removePlayerFromTable(p);
    }

    @Override
    public void onPlayerSuitsRefreshed(Player p) {
        if (p.equals(thisPlayer)) {
            setupPlayerCards();
            Log.d(TAG, "recreated suits..");
        } else {
            PlayerView pv = getViewForPlayer(p);
            if (pv != null) pv.updateNameWithCount();
        }
        resumeThread();
    }

    @Override
    public void onOnAllPageSure(final Player p) {
        if (thread != null)
            if (!thread.isSuspended()) {
                try {
                    Handler refresh = new Handler(Looper.getMainLooper());
                    refresh.post(new Runnable() {
                        public void run() {
                            PlayerView pv = getViewForPlayer(p);
                            if (pv != null) {
                                pv.setOnPageSureColor();
                            }
                            emptyCallback();
                        }
                    });
                    // wait for ui to draw
                    thread.waitTillNextInput();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }

    public void resumeThread() {
        if (thread != null)
            if (thread.isSuspended())
                thread.resume();
    }

    @Override
    public void onCardAddedToTable(Table t, final Card c) {
        if (thread != null)
            if (!thread.isSuspended()) {
                try {
                    Handler refresh = new Handler(Looper.getMainLooper());
                    refresh.post(new Runnable() {
                        public void run() {
                            CardView cv = getCardViewOnTableFor(c);
                            if (cv != null) {
                                cv.setCard(c, true);
                                cv.setVisibility(View.VISIBLE);
                            }
                            emptyCallback();
                        }
                    });

                    // wait for ui to draw
                    thread.waitTillNextInput();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }

    public void showPlayerPassed(final Player currentPlayer) {
        Handler refresh = new Handler(Looper.getMainLooper());
        refresh.post(new Runnable() {
            @Override
            public void run() {
                String msg = currentPlayer.getName() + " passed current move ";
                getMainDisplayTextView().setText(msg);
                PlayerView pv = getViewForPlayer(currentPlayer);
                if (pv != null) {
                    pv.removeCurrentPlayer();
                    emptyCallback();
                } else {
                    msg = currentPlayer.getName() + " passed this move";
                    getMainDisplayTextView().setText(msg);
                    emptyCallback();
                }
            }
        });
    }

//    public void showPlayerThinking(final Player currentPlayer) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                final PlayerView pv = getViewForPlayer(currentPlayer);
//                if (pv != null) {
//                    final String msg = currentPlayer.getName() + " is thinking";
//                    Log.d(TAG, "check_" + msg);
//                    getMainDisplayTextView().setText(Html.fromHtml(msg));
//                    pv.setCurrentPlayer();
//                    emptyCallback();
//
//                } else {
//                    final String msg = "<font color=\"#ffff00\">Your move...</font>";
//                    Log.d(TAG, "check_" + msg);
//                    getMainDisplayTextView().setText(Html.fromHtml(msg));
//                    emptyCallback();
//
//                }
//            }
//        });
//    }

    public void showPlayedMove(final Player currentPlayer, final Card ct) {
        Handler refresh = new Handler(Looper.getMainLooper());
        refresh.post(new Runnable() {
            @Override
            public void run() {
                String msg = currentPlayer.getName() + " played " + ct.toString();
                getMainDisplayTextView().setText(msg);
                PlayerView pv = getViewForPlayer(currentPlayer);
                if (pv != null) {
                    pv.removeCurrentPlayer();
                }
                emptyCallback();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onCardRemovedFromTable(Table t, Card c) {

    }

    @Override
    public void onPlayerAddedToTable(Table t, Player p) {

    }

    @Override
    public void onPlayerRemovedFromTable(Table t, Player p) {

    }

    @Override
    public void onSuitsRefreshed(Table t) {

    }

    @Override
    public void onTableFull(Table t) {

    }

    @Override
    public void onPlayerWon(Player p) {
//        getViewForPlayer(p).setCurrentPlayer();
//        Toast.makeText(this, "player won : " + p.getName(), Toast.LENGTH_LONG).show();
//        Log.d(TAG, "on player won called for " + p.getName());
//        thread.isFinished = true;
    }
}

/**
 * private void playGame() {
 * try {
 * Card currentMove;
 * while (!isFinished) {
 * currentPlayer = Table.getInstance().getCurrentPlayer();
 * currentPlayerView = getCurrentPlayerView();
 * if(currentPlayerView != null) currentPlayerView.setCurrentPlayer();
 * <p/>
 * if (currentPlayer.equals(thisPlayer)) {
 * if(table.getAvailableMovesFor(currentPlayer).isEmpty()) {
 * table.incrementCurrentPlayerIndex();
 * if(currentPlayerView != null) currentPlayerView.removeCurrentPlayer();
 * Log.d(TAG, "current player  : " + currentPlayer.getName());
 * continue;
 * } else {
 * isFinished = true;
 * }
 * } else {
 * Log.d(TAG, "player : " + currentPlayer.getName());
 * if (table.getAvailableMovesFor(currentPlayer).isEmpty()) {
 * table.incrementCurrentPlayerIndex();
 * continue;
 * }
 * <p/>
 * currentMove = table.getAvailableMovesFor(currentPlayer).get(Solver.getIndexOfNextMove(table.getAvailableMovesFor(currentPlayer)));
 * Card ct = currentPlayer.playCard(currentMove);
 * table.addNewCardToTable(currentMove);
 * <p/>
 * this.table.incrementCurrentPlayerIndex();
 * <p/>
 * if(currentPlayerView != null) currentPlayerView.removeCurrentPlayer();
 * }
 * }
 * } catch (NullTableException e) {
 * e.printStackTrace();
 * }
 * }
 **/