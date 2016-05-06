package com.comyr.pg18.sevenhearts.background.threads;

import android.text.Html;
import android.view.View;

import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.game.resources.Table;
import com.comyr.pg18.sevenhearts.game.solution.Solver;
import com.comyr.pg18.sevenhearts.ui.activities.GameActivity;
import com.comyr.pg18.sevenhearts.ui.views.CardView;
import com.comyr.pg18.sevenhearts.ui.views.PlayerView;

/**
 * Created by pranav on 5/1/16.
 * In package com.comyr.pg18.sevenhearts.ui.threads
 */
public class GameThread implements Runnable {
    private static final Object lock = new Object();
    // game related variables
    public static boolean isFinished = false;
    public static Player currentPlayer;
    private final String TAG = "TestThread";
    private final Object moveLock = new Object();
    private Card currentMove;
    private Thread thread;
    private String name;
    private boolean suspended = false;
    private GameActivity activity;

    public GameThread(GameActivity activity) {
        this.activity = activity;
        this.name = "Game Thread";
        //Log.d(TAG, "creating " + name);
    }

    public String getName() {
        return name;
    }

    public Thread getThread() {
        return thread;
    }

    public void setCurrentMove(Card c) {
        synchronized (lock) {
            currentMove = c;
        }
    }

    @Override
    public void run() {
        Table table = activity.getTable();
        currentPlayer = table.getCurrentPlayer();
        try {
            while (!isFinished) {// Let the thread sleep for a while.
                Thread.sleep(1000);

                synchronized (currentPlayer) {
                    currentPlayer = table.getCurrentPlayer();
                }

                //Log.d("__special__", "move : " + currentPlayer.getName() + " cards : " + currentPlayer.getCards().toString());
                //Log.d("__special__", "     : " + "available cards : " + table.getAvailableMovesFor(currentPlayer));


                if (currentPlayer.equals(activity.getThisPlayer())) {

                    if (Table.getAvailableMovesFor(currentPlayer).isEmpty()) {
                        synchronized (table) {
                            table.incrementCurrentPlayerIndex();
                        }
                        Thread.sleep(1000);
                        // show that the player has passed this move
                        showPlayerPassed(currentPlayer);
                        // wait till UI refreshes
                        waitTillNextInput();

                        continue;
                    } else {
                        Thread.sleep(1000);

                        showPlayerThinking(currentPlayer);
                        // wait to show ui
                        waitTillNextInput();

                        // wait for user to click a card
                        waitTillNextInput();

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (lock) {
                                    currentMove = currentPlayer.playCard(currentMove);
                                }
                            }
                        });

                        // wait till the currentMove is returned (resumed in onSuitsRefreshed())
                        waitTillNextInput();

                        synchronized (table) {
                            table.addNewCardToTable(currentMove);
                        }

                        addNewCardToTable(currentMove);
                        waitTillNextInput();

                        // show played move
                        showPlayedMove(currentPlayer, currentMove);
                        // wait for ui refresh
                        waitTillNextInput();

                        synchronized (table) {
                            table.incrementCurrentPlayerIndex();
                        }
                    }
                    currentMove = null;

                } else {
                    //Log.d("__special__", " p cp not same" + currentPlayer.getName() + activity.getThisPlayer().getName());
                    Thread.sleep(1000);

                    showPlayerThinking(currentPlayer);
                    // wait to show ui
                    waitTillNextInput();

                    // player takes 4 secs to think
                    Thread.sleep(1500);

                    if (Table.getAvailableMovesFor(currentPlayer).isEmpty()) {
                        //Log.d("__special__", "     : " + "reached empty");
                        //Log.d("__special__", "     : " + "cards : " + currentPlayer.getCards().toString());
                        synchronized (table) {
                            table.incrementCurrentPlayerIndex();
                        }

                        // show that the player has passed this move
                        showPlayerPassed(currentPlayer);
                        // wait till UI refreshes
                        waitTillNextInput();

                        continue;
                    } else {
                        final Card ct;

                        synchronized (moveLock) {
                            ct = Solver.getNextMove(table.getAvailableMovesFor(currentPlayer));
                        }

                        //Log.d("__special__", "card played : " + ct.toString() + " for player : " + currentPlayer.getName());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (currentPlayer) {
                                    currentMove = currentPlayer.playCard(ct);
                                }
                            }
                        });

                        // wait for UI refresh
                        waitTillNextInput();

                        synchronized (table) {
                            table.addNewCardToTable(ct);
                        }

                        addNewCardToTable(ct);
                        waitTillNextInput();

                        // show played move
                        showPlayedMove(currentPlayer, ct);
                        // wait for ui refresh
                        waitTillNextInput();

                        synchronized (table) {
                            table.incrementCurrentPlayerIndex();
                        }

                        currentMove = null;

                    }
                }

                //Log.d("__special__", "(P)move : " + currentPlayer.getName() + " cards : " + currentPlayer.getCards().toString());

                // TODO : move this block to main game activity's on player won handler
                // checks if any of the players has won
                if (currentPlayer.getCards().isEmpty()) {
                    //Log.d(TAG, " empty cards for player " + currentPlayer.getName());
                    isFinished = true;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.getMainDisplayTextView().setText(Html.fromHtml("<font color=\"#00ff00\">Player won : " + currentPlayer.getName() + "</font>"));
                            PlayerView pv = activity.getViewForPlayer(currentPlayer);
                            if (pv == null) {
                                GameActivity.getThread().resume();
                            } else {
                                pv.setCurrentPlayer();
                                GameActivity.getThread().resume();
                            }
                        }
                    });
                    // waits for UI refresh
                    waitTillNextInput();
                }
            }
        } catch (InterruptedException e) {
            //Log.d(TAG, "thread " + name + " interrupted.");
        }
        //Log.d(TAG, "thread " + name + " exiting...");
    }

    public void start() {
        //Log.d(TAG, "Starting : " + name);
        isFinished = false;
        if (thread == null) {
            thread = new Thread(this, name);
            thread.start();
        }
    }

    public void kill() {
        currentPlayer = null;
        currentMove = null;
        thread.interrupt();
        thread = null;
    }

    void suspend() {
        suspended = true;
    }

    public synchronized void resume() {
        suspended = false;
        notify();
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void waitTillNextInput() throws InterruptedException {
        suspend();
        synchronized (this) {
            while (suspended) {
                wait();
            }
        }
    }

    private void showPlayerThinking(final Player currentPlayer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final PlayerView pv = activity.getViewForPlayer(currentPlayer);
                if (pv != null) {
                    final String msg = currentPlayer.getName() + " is thinking";
                    //Log.d(TAG, "check_" + msg);
                    activity.getMainDisplayTextView().setText(Html.fromHtml(msg));
                    pv.setCurrentPlayer();
                    GameActivity.getThread().resume();
                } else {
                    final String msg = "<font color=\"#ffff00\">Your move...</font>";
                    //Log.d(TAG, "check_" + msg);
                    activity.getMainDisplayTextView().setText(Html.fromHtml(msg));
                    GameActivity.getThread().resume();
                }
            }
        });
    }


    private void showPlayerPassed(final Player currentPlayer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msg = currentPlayer.getName() + " passed current move ";
                activity.getMainDisplayTextView().setText(msg);
                PlayerView pv = activity.getViewForPlayer(currentPlayer);
                if (pv != null) {
                    pv.removeCurrentPlayer();
                    GameActivity.getThread().resume();
                } else {
                    msg = currentPlayer.getName() + " passed this move";
                    activity.getMainDisplayTextView().setText(msg);
                    GameActivity.getThread().resume();
                }
            }
        });
    }

    private void showPlayedMove(final Player currentPlayer, final Card ct) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msg = currentPlayer.getName() + " played " + ct.toString();
                activity.getMainDisplayTextView().setText(msg);
                PlayerView pv = activity.getViewForPlayer(currentPlayer);
                if (pv != null) {
                    pv.removeCurrentPlayer();
                }
                GameActivity.getThread().resume();
            }
        });
    }

    private void addNewCardToTable(final Card ct) {
        //Log.d("__special__", "drawing card : " + ct.toString());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CardView cv = activity.getCardViewOnTableFor(ct);
                if (cv != null) {
                    cv.setCard(ct, true);
                    cv.setVisibility(View.VISIBLE);
                    //Log.d("__special__", "drawn card : " + ct.toString());
                }
                //Log.d("__special__", "card not drawn : " + ct.toString());
                GameActivity.getThread().resume();
            }
        });
    }
}
