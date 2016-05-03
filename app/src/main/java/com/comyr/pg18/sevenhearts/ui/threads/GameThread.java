package com.comyr.pg18.sevenhearts.ui.threads;

import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.game.solution.Solver;
import com.comyr.pg18.sevenhearts.ui.activities.GameActivity;
import com.comyr.pg18.sevenhearts.ui.views.PlayerView;

/**
 * Created by pranav on 5/1/16.
 * In package com.comyr.pg18.sevenhearts.ui.threads
 */
public class GameThread implements Runnable {
    // game related variables
    public static boolean isFinished = false;
    public static Card currentMove;
    public static Player currentPlayer;
    private final String TAG = "TestThread";
    private Thread thread;
    private String name;
    private boolean suspended = false;
    private GameActivity activity;

//    private static GameThread instance = null;

    public GameThread(GameActivity activity) {
        this.activity = activity;
        this.name = "Game Thread";
        Log.d(TAG, "creating " + name);
    }

//    public static GameThread getInstance(GameActivity activity) {
//        if(instance == null) {
//            instance = new GameThread(activity);
//        }
//        return instance;
//    }

    public String getName() {
        return name;
    }

    public Thread getThread() {
        return thread;
    }

    @Override
    public void run() {
        try {
            while (!isFinished) {// Let the thread sleep for a while.
                Thread.sleep(500);
                currentPlayer = GameActivity.getTable().getCurrentPlayer();
                Log.d(TAG, currentPlayer.getName());
                if (currentPlayer.equals(GameActivity.getThisPlayer())) {
                    if (GameActivity.getTable().getAvailableMovesFor(currentPlayer).isEmpty()) {
                        GameActivity.getTable().incrementCurrentPlayerIndex();
                        Thread.sleep(1500);
                        // show that the player has passed this move
                        activity.showPlayerPassed(currentPlayer);
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
                                currentMove = currentPlayer.playCard(currentMove);
                            }
                        });

                        // wait till the currentMove is returned (resumed in onSuitsRefreshed())
                        waitTillNextInput();

                        synchronized (GameActivity.getTable()) {
                            GameActivity.getTable().addNewCardToTable(currentMove);
                        }
                        // show played move
                        activity.showPlayedMove(currentPlayer, currentMove);
                        // wait for ui refresh
                        waitTillNextInput();

                        GameActivity.getTable().incrementCurrentPlayerIndex();
                    }
                    currentMove = null;
                } else {
                    Thread.sleep(1000);

                    showPlayerThinking(currentPlayer);
                    // wait to show ui
                    waitTillNextInput();

                    // player takes 4 secs to think
                    Thread.sleep(1500);

                    if (GameActivity.getTable().getAvailableMovesFor(currentPlayer).isEmpty()) {
                        GameActivity.getTable().incrementCurrentPlayerIndex();

                        // show that the player has passed this move
                        activity.showPlayerPassed(currentPlayer);
                        // wait till UI refreshes
                        waitTillNextInput();

                        continue;
                    }

                    final Card ct = GameActivity.getTable().getAvailableMovesFor(currentPlayer).get(Solver.getIndexOfNextMove(GameActivity.getTable().getAvailableMovesFor(currentPlayer)));

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentMove = currentPlayer.playCard(ct);
                        }
                    });

                    // wait for UI refresh
                    waitTillNextInput();

                    synchronized (GameActivity.getTable()) {
                        GameActivity.getTable().addNewCardToTable(ct);
                    }

                    // show played move
                    activity.showPlayedMove(currentPlayer, ct);
                    // wait for ui refresh
                    waitTillNextInput();

                    GameActivity.getTable().incrementCurrentPlayerIndex();
                    currentMove = null;
                }

                // TODO : move this block to main game activity's on player won handler
                // checks if any of the players has won
                if (currentPlayer.getCards().isEmpty()) {
                    Log.d(TAG, " empty cards for player " + currentPlayer.getName());
                    isFinished = true;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplicationContext(), "Player won : " + currentPlayer.getName(), Toast.LENGTH_LONG).show();
                            PlayerView pv = activity.getViewForPlayer(currentPlayer);
                            if (pv == null) {
                                Toast.makeText(activity.getApplicationContext(), "You won !!!", Toast.LENGTH_LONG).show();
                                activity.emptyCallback();
                            } else {
                                pv.setCurrentPlayer();
                                activity.emptyCallback();
                            }
                        }
                    });
                    // waits for UI refresh
                    waitTillNextInput();
                }
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "thread " + name + " interrupted.");
        }
        Log.d(TAG, "thread " + name + " exiting...");
    }

    public void start() {
        Log.d(TAG, "Starting : " + name);
        if (thread == null) {
            thread = new Thread(this, name);
            thread.start();
        }
    }

    void suspend() {
        suspended = true;
    }

    public synchronized void resume() {
        suspended = false;
        notifyAll();
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

    public void showPlayerThinking(final Player currentPlayer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final PlayerView pv = activity.getViewForPlayer(currentPlayer);
                if (pv != null) {
                    final String msg = currentPlayer.getName() + " is thinking";
                    Log.d(TAG, "check_" + msg);
                    activity.getMainDisplayTextView().setText(Html.fromHtml(msg));
                    pv.setCurrentPlayer();
                    activity.emptyCallback();

                } else {
                    final String msg = "<font color=\"#ffff00\">Your move...</font>";
                    Log.d(TAG, "check_" + msg);
                    activity.getMainDisplayTextView().setText(Html.fromHtml(msg));
                    activity.emptyCallback();

                }
            }
        });
    }
}
