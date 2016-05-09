package com.comyr.pg18.sevenhearts.background.threads;

import android.text.Html;
import android.util.Log;
import android.view.View;

import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.game.resources.Table;
import com.comyr.pg18.sevenhearts.game.solution.Solver;
import com.comyr.pg18.sevenhearts.network.analytics.MixPanel;
import com.comyr.pg18.sevenhearts.ui.activities.GameActivity;
import com.comyr.pg18.sevenhearts.ui.sys.SysUtils;
import com.comyr.pg18.sevenhearts.ui.views.CardView;
import com.comyr.pg18.sevenhearts.ui.views.PlayerView;

/**
 * Created by pranav on 5/1/16.
 * In package com.comyr.pg18.sevenhearts.ui.threads
 */
public class GameThread implements Runnable {
    /**
     * termination condition for game loop
     */
    public boolean isFinished = false;
    /**
     * stores card that human player clicks
     */
    private Card currentMove;
    /**
     * used as a lock on @see GameThread#currentMove
     * avoids concurrent update of #currentMove
     * #currentMove is updated from UI thread and background thread as well
     */
    private final Object lock = new Object();
    /**
     * current player who will play current move
     */
    public Player currentPlayer;
    /**
     * lock on current player
     */
    public final Object cpLock = new Object();
    /**
     * used as a lock on #ct to avoid concurrent manipulation
     */
    private final Object moveLock = new Object();
    /**
     * game thread object
     */
    private Thread thread;
    /**
     * string identifier for current thread instance
     */
    private String name;
    /**
     * checks if the thread is suspended @see GameThread#resume @see GameThread#suspend
     */
    private boolean suspended = false;
    /**
     * activity object to keep in touch with the UI thread
     */
    private GameActivity activity;

    public GameThread(GameActivity activity) {
        this.activity = activity;
        this.name = "Game Thread";
    }

    public String getName() {
        return name;
    }

    public void setCurrentMove(Card c) {
        synchronized (lock) {
            currentMove = c;
        }
    }

    /**
     * main game loop is handled here
     */
    @Override
    public void run() {
        Table table = activity.getTable();
        currentPlayer = table.getCurrentPlayer();
        try {
            while (!isFinished) {// Let the thread sleep for a while.
                Thread.sleep(1000);
                synchronized (cpLock) {
                    currentPlayer = table.getCurrentPlayer();
                }
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
                    Thread.sleep(1000);
                    showPlayerThinking(currentPlayer);
                    // wait to show ui
                    waitTillNextInput();
                    // player takes 4 secs to think
                    Thread.sleep(1500);
                    if (Table.getAvailableMovesFor(currentPlayer).isEmpty()) {
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
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (cpLock) {
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

                // TODO : move this block to main game activity's on player won handler
                // checks if any of the players has won
                if (currentPlayer.getCards().isEmpty()) {
                    activity.getmAnalytics().trackAction(MixPanel.ACTION_PLAYER_WON, MixPanel.TAG_PLAYER, currentPlayer.getName());
                    isFinished = true;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.getMainDisplayTextView().setText(Html.fromHtml("<font color=\"#00ff00\">Player won : " + currentPlayer.getName() + "</font>"));
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }
                            activity.showScoreCard();
                            activity.getThread().resume();
                        }
                    });
                    // waits for UI refresh
                    waitTillNextInput();
                }
            }
        } catch (InterruptedException e) {
        }
    }

    /**
     * starts the thread
     */
    public void start() {
        isFinished = false;
        if (thread == null) {
            thread = new Thread(this, name);
            thread.start();
        }
    }

    /**
     * kills the thread
     */
    public void kill() {
        thread.interrupt();
        currentMove = null;
        thread = null;
        currentPlayer = null;
    }

    /**
     * suspends the thread
     */
    void suspend() {
        suspended = true;
    }

    /**
     * resumes the thread
     */
    public synchronized void resume() {
        suspended = false;
        notify();
    }

    /**
     * tells if the thread is suspended or not
     *
     * @return true if suspended, false otherwise
     */
    public boolean isSuspended() {
        return suspended;
    }

    /**
     * wait {@link GameThread} instance till next call to resume is occurred
     * @throws InterruptedException can't wait if thread is interrupted
     */
    public void waitTillNextInput() throws InterruptedException {
        suspend();
        synchronized (this) {
            while (suspended) {
                wait();
            }
        }
    }

    /**
     * displays which player is currently thinking
     *
     * @param currentPlayer {@link Player} who is thinking
     */
    private void showPlayerThinking(final Player currentPlayer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final PlayerView pv = activity.getViewForPlayer(currentPlayer);
                if (pv != null) {
                    final String msg = currentPlayer.getName() + " is thinking";
                    activity.getMainDisplayTextView().setText(Html.fromHtml(msg));
                    pv.setCurrentPlayer();
                    if (GameActivity.getThread() != null) GameActivity.getThread().resume();
                } else {
                    final String msg = "<font color=\"#ffff00\">Your move...</font>";
                    activity.getMainDisplayTextView().setText(Html.fromHtml(msg));
                    SysUtils.vibrate(activity);
                    SysUtils.playUserMoveSound(activity);
                    if (GameActivity.getThread() != null) GameActivity.getThread().resume();
                }
            }
        });
    }

    /**
     * displays which player passed the move
     * @param currentPlayer {@link Player} who passed the move
     */
    private void showPlayerPassed(final Player currentPlayer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msg = currentPlayer.getName() + " passed current move";
                activity.getMainDisplayTextView().setText(msg);
                PlayerView pv = activity.getViewForPlayer(currentPlayer);
                if (pv != null) {
                    pv.removeCurrentPlayer();
                    GameActivity.getThread().resume();
                } else {
                    msg = currentPlayer.getName() + " passed this move";
                    activity.getMainDisplayTextView().setText(msg);
                    if(GameActivity.getThread() != null) GameActivity.getThread().resume();
                }
            }
        });
    }

    /**
     * displayes which {@link Player} played which {@link Card}
     * @param currentPlayer {@link Player} who played the move
     * @param ct {@link Card} that was played
     */
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
                if(GameActivity.getThread() != null) GameActivity.getThread().resume();
            }
        });
    }

    /**
     * draws played card on the table
     * @param ct card that is played
     */
    private void addNewCardToTable(final Card ct) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CardView cv = activity.getCardViewOnTableFor(ct);
                if (cv != null) {
                    cv.setCard(ct, true);
                    cv.setVisibility(View.VISIBLE);
                }
                if(GameActivity.getThread() != null) GameActivity.getThread().resume();
            }
        });
    }
}
