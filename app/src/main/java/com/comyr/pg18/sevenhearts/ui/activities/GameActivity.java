package com.comyr.pg18.sevenhearts.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comyr.pg18.sevenhearts.R;
import com.comyr.pg18.sevenhearts.background.tasks.GameInitTask;
import com.comyr.pg18.sevenhearts.background.tasks.GameTask;
import com.comyr.pg18.sevenhearts.background.threads.GameThread;
import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.Deck;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.game.resources.Table;
import com.comyr.pg18.sevenhearts.game.resources.constants.Suits;
import com.comyr.pg18.sevenhearts.game.resources.utils.PlayerStateChangeListener;
import com.comyr.pg18.sevenhearts.game.resources.utils.TableStateChangeListener;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.PlayerNotFoundException;
import com.comyr.pg18.sevenhearts.ui.activities.base.CustomActivity;
import com.comyr.pg18.sevenhearts.ui.utils.FontUtils;
import com.comyr.pg18.sevenhearts.ui.utils.GameData;
import com.comyr.pg18.sevenhearts.ui.utils.helper.ActivityOptionHelper;
import com.comyr.pg18.sevenhearts.ui.views.CardView;
import com.comyr.pg18.sevenhearts.ui.views.PlayerView;

import java.util.ArrayList;
import java.util.Iterator;

public class GameActivity extends CustomActivity implements PlayerStateChangeListener, TableStateChangeListener {

    private static final Object threadLock = new Object();
    private static GameThread thread;
    private final String TAG = "GameActivity";
    private ArrayList<Player> players;
    private Table table;
    private Deck deck;
    private Player thisPlayer;
    private ArrayList<Card> thisPlayerCards;
    // player with current turn on the table
    private Player currentPlayer;
    private LinearLayout playerCardsLayout;
    private LinearLayout playersLayout;
    private TextView mainDisplayTextView;
    private GameActivity gameActivity;
    // table variables
    private CardView heartsUpperCard, heartsLowerCard, diamondsUpperCard, diamondsLowerCard, spadesUpperCard, spadesLowerCard, clubsUpperCard, clubsLowerCard;
    private GameInitTask gameInitTask;
    private GameTask task;

    public static GameThread getThread() {
        return thread;
    }

    public static void setThread(GameThread thread) {
        synchronized (threadLock) {
            GameActivity.thread = thread;
        }
    }

    public static void resumeThread() {
        if (thread != null)
            if (thread.isSuspended())
                thread.resume();
    }

    public Player getThisPlayer() {
        return thisPlayer;
    }

    public void setThisPlayer(Player tp) {
        thisPlayer = tp;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck d) {
        deck = d;
    }

    public ArrayList<Card> getThisPlayerCards() {
        return thisPlayerCards;
    }

    public void setThisPlayerCards(ArrayList<Card> tpc) {
        thisPlayerCards = tpc;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table t) {
        table = t;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> ps) {
        players = ps;
    }

    public LinearLayout getPlayersLayout() {
        return playersLayout;
    }

    public LinearLayout getPlayerCardsLayout() {
        return playerCardsLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setOptions(ActivityOptionHelper.getOptionsForActivity(ActivityOptionHelper.ACTIVITY_GAME));

        setContentView(R.layout.activity_game);

        reset();

        gameActivity = this;

        initUI();

        gameInitTask = new GameInitTask(gameActivity);
        gameInitTask.execute();
    }

    private void testTask() {
        task = new GameTask(gameActivity);
        task.execute();
    }

    public TextView getMainDisplayTextView() {
        return mainDisplayTextView;
    }

    public void testThread() {
        thread = new GameThread(gameActivity);
        thread.start();
    }

    public PlayerView getViewForPlayer(Player player) {
        for (int i = 0; i < playersLayout.getChildCount(); i++) {
            PlayerView pv = (PlayerView) playersLayout.getChildAt(i);
            if (pv.getPlayer().equals(player)) {
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
//        resumeTask();
    }

    public void resumeTask() {
        if (task != null)
            task.resumeExecution();
    }

    private void initTable() {
        Log.d("table status", "Initing table");
        table = null;
        GameData.initData();
        String p2 = GameData.getRandomPlayerName();
        String p3 = GameData.getRandomPlayerName();
        String p4 = GameData.getRandomPlayerName();
        String[] names = {GameData.thisPlayerName, p2, p3, p4};
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

    public Player getPlayerForName(String name) throws PlayerNotFoundException {
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (p.getName().equals(name)) return p;
        }
        throw new PlayerNotFoundException("In game activity, while getting this player");
    }

    private void setupPlayers() {
        try {
            players = table.getPlayers();
            thisPlayer = getPlayerForName(GameData.thisPlayerName);
            table.distributeCards();
            for (Player p : players) {
                if (!p.equals(thisPlayer)) {
                    PlayerView playerView = new PlayerView(this);
                    playerView.setPlayer(p);
                    playersLayout.addView(playerView);
                }
            }
//        } catch (NullTableException e) {
        } catch (PlayerNotFoundException e) {

        }
    }

    private void setupPlayerCards() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playerCardsLayout.removeAllViews();
            }
        });
        thisPlayerCards = thisPlayer.getCards();
        int i = 0;
        for (Card c : thisPlayerCards) {
            final CardView cv = new CardView(this);
            cv.setCard(c);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Card clickedCard = ((CardView) v).getCard();
                    boolean valid = table.isMoveValid(clickedCard);
                    if (!valid) return;
                    thread.setCurrentMove(clickedCard);
                    emptyCallback();
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
        synchronized (table) {
            table.removePlayerFromTable(p);
        }
    }

    @Override
    public void onPlayerCardsExhausted(Player p, String status) {
        synchronized (table) {
            table.removePlayerFromTable(p);
        }
    }

    @Override
    public void onPlayerSuitsRefreshed(final Player p) {
        if (p.equals(thisPlayer)) {
            setupPlayerCards();
            emptyCallback();
        } else {
            PlayerView pv = getViewForPlayer(p);
            if (pv != null) pv.updateNameWithCount();
            emptyCallback();
        }

    }

    @Override
    public void onOnAllPageSure(final Player p) {

    }

    @Override
    public void onCardAddedToTable(Table t, final Card c) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        thread.kill();
        reset();
        finish();
    }

    private void reset() {
        gameInitTask = null;
        players = null;
        Table.reset();
        table = null;
        deck = null;
        thisPlayer = null;
        thisPlayerCards = null;
        currentPlayer = null;
        gameActivity = null;
        //task.cancel(true);
        //task = null;
        thread = null;
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
    }
}

