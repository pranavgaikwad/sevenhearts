package com.comyr.pg18.sevenhearts.background.tasks;

import android.os.AsyncTask;
import android.view.View;

import com.comyr.pg18.sevenhearts.background.threads.GameThread;
import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.game.resources.Table;
import com.comyr.pg18.sevenhearts.game.resources.utils.OnPlayerStateChangedListener;
import com.comyr.pg18.sevenhearts.game.resources.utils.OnTableStateChangedListener;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.PlayerNotFoundException;
import com.comyr.pg18.sevenhearts.ui.activities.GameActivity;
import com.comyr.pg18.sevenhearts.ui.utils.GameData;
import com.comyr.pg18.sevenhearts.ui.views.CardView;
import com.comyr.pg18.sevenhearts.ui.views.PlayerView;

import java.util.Iterator;

/**
 * Created by pranav on 5/6/16.
 * In package com.comyr.pg18.sevenhearts.background.tasks
 */
public class GameInitTask extends AsyncTask<String, Void, String> implements OnPlayerStateChangedListener, OnTableStateChangedListener {
    private GameActivity gameActivity;

    public GameInitTask(GameActivity gameActivity) {
        this.gameActivity = gameActivity;
    }

    @Override
    protected String doInBackground(String... params) {
        initGameResources();
        return null;
    }

    /**
     * initiates all the resources required to start the game
     */
    private void initGameResources() {
        initTable();

        setupPlayers();

        setupPlayerCards();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        GameActivity.setThread(new GameThread(gameActivity));
        GameActivity.getThread().start();
    }

    /**
     * initiates an empty {@link Table} with 4 {@link Player} objects with default {@link GameData}
     */
    private void initTable() {
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
        gameActivity.setTable(Table.getInstance(this, players));
        gameActivity.getTable().init();
    }

    /**
     * sets up {@link Player} objects and their corresponding {@link PlayerView} objects
     */
    private void setupPlayers() {
        try {
            gameActivity.setPlayers(gameActivity.getTable().getPlayers());
            gameActivity.setThisPlayer(gameActivity.getPlayerForName(GameData.thisPlayerName));
            gameActivity.getTable().distributeCards();
            Iterator<Player> it = gameActivity.getPlayers().iterator();
            while (it.hasNext()) {
                Player p = it.next();
                if (!p.equals(gameActivity.getThisPlayer())) {
                    final PlayerView playerView = new PlayerView(gameActivity);
                    playerView.setPlayer(p);
                    gameActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gameActivity.getPlayersLayout().addView(playerView);
                        }
                    });
                }
            }
        } catch (PlayerNotFoundException e) {

        }
    }

    /**
     * sets up {@link Card} objects which are in hand of the current {@link Player}
     */
    private void setupPlayerCards() {

        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameActivity.getPlayerCardsLayout().removeAllViews();
            }
        });

        gameActivity.setThisPlayerCards(gameActivity.getThisPlayer().getCards());

        int i = 0;

        Iterator<Card> it = gameActivity.getThisPlayerCards().iterator();
        while (it.hasNext()) {
            Card c = it.next();
            final CardView cv = new CardView(gameActivity);
            cv.setCard(c);
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Card clickedCard = ((CardView) v).getCard();
                    boolean valid;
                    synchronized (gameActivity.getTable()) {
                        valid = gameActivity.getTable().isMoveValid(clickedCard);
                    }
                    if (!valid) return;

                    GameActivity.getThread().setCurrentMove(clickedCard);
                    GameActivity.getThread().resume();
                }
            });
            if (i != 0)
                cv.setNegativeMargin();

            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameActivity.getPlayerCardsLayout().addView(cv);
                }
            });
            i++;
        }
    }

    @Override
    public void onPlayerCardsExhausted(Player p) {
        synchronized (gameActivity.getTable()) {
            gameActivity.getTable().removePlayerFromTable(p);
        }
    }

    @Override
    public void onPlayerCardsExhausted(Player p, String status) {
        synchronized (gameActivity.getTable()) {
            gameActivity.getTable().removePlayerFromTable(p);
        }
    }

    @Override
    public void onPlayerSuitsRefreshed(final Player p) {
        if (p.equals(gameActivity.getThisPlayer())) {
            setupPlayerCards();
            if (GameActivity.getThread() != null) GameActivity.getThread().resume();
        } else {
            PlayerView pv = gameActivity.getViewForPlayer(p);
            if (pv != null) pv.updateNameWithCount();
            if (GameActivity.getThread() != null) GameActivity.getThread().resume();
        }
    }


    @Override
    public void onOnAllPageSure(final Player p) {
        gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (gameActivity.getViewForPlayer(p) != null) {
                    gameActivity.getViewForPlayer(p).setOnPageSureColor();
                }
            }
        });
    }

    @Override
    public void onCardAddedToTable(Table t, Card c) {

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
