package com.comyr.pg18.sevenhearts.background.tasks;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.game.solution.Solver;
import com.comyr.pg18.sevenhearts.ui.activities.GameActivity;
import com.comyr.pg18.sevenhearts.ui.views.CardView;
import com.comyr.pg18.sevenhearts.ui.views.PlayerView;

/**
 * Created by pranav on 5/4/16.
 * In package com.comyr.pg18.sevenhearts.background.tasks
 */
public class GameTask extends AsyncTask<String, Void, String> {
    public static Card currentMove;
    public static Player currentPlayer;
    public static boolean isFinished = false;
    private final String TAG = "GameTask";
    private final String DEBUG_TAG = "__GameTask__";
    private GameActivity activity;
    private String name;
    private boolean notified;

    public GameTask(GameActivity activity) {
        this.activity = activity;
        name = "Game Task";
        Log.d(TAG, "created " + name);
    }

    public void resumeExecution() {
        synchronized (this) {
            notified = true;
            this.notify();
        }
    }

    private void waitForInput() {
        synchronized (this) {
            while (!notified) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            isFinished = false;
            while (!isFinished) {
                if (isCancelled()) break;

                // Let the thread sleep for a while.
                Thread.sleep(500);

                currentPlayer = activity.getTable().getCurrentPlayer();
                // Log.d(DEBUG_TAG, "move : " + currentPlayer.getName() + " cards : " + currentPlayer.getCards().toString());
                // Log.d(DEBUG_TAG, "     : " + "available cards : " + activity.getTable().getAvailableMovesFor(currentPlayer));
                if (currentPlayer.equals(activity.getThisPlayer())) {
                    if (activity.getTable().getAvailableMovesFor(currentPlayer).isEmpty()) {
                        activity.getTable().incrementCurrentPlayerIndex();
                        Thread.sleep(1500);
                        // show that the player has passed this move
                        showPlayerPassed(currentPlayer);
                        // wait till UI refreshes
                        waitForInput();

                        continue;
                    } else {
                        Thread.sleep(1500);

                        showPlayerThinking(currentPlayer);
                        // wait to show ui
                        waitForInput();

                        // wait for user to click a card
                        waitForInput();

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentMove = currentPlayer.playCard(currentMove);
                            }
                        });

                        // wait till the currentMove is returned (resumed in onSuitsRefreshed())
                        waitForInput();

                        synchronized (activity.getTable()) {
                            activity.getTable().addNewCardToTable(currentMove);
                        }

                        addNewCardToTable(currentMove);
                        waitForInput();

                        // show played move
                        showPlayedMove(currentPlayer, currentMove);
                        // wait for ui refresh
                        waitForInput();

                        activity.getTable().incrementCurrentPlayerIndex();
                    }
                    currentMove = null;
                } else {
                    Thread.sleep(1500);

                    showPlayerThinking(currentPlayer);
                    // wait to show ui
                    waitForInput();

                    // player takes 4 secs to think
                    Thread.sleep(2000);

                    if (activity.getTable().getAvailableMovesFor(currentPlayer).isEmpty()) {
                        Log.d(DEBUG_TAG, "     : " + "reached empty");
                        Log.d(DEBUG_TAG, "     : " + "cards : " + currentPlayer.getCards().toString());
                        activity.getTable().incrementCurrentPlayerIndex();

                        // show that the player has passed this move
                        showPlayerPassed(currentPlayer);
                        // wait till UI refreshes
                        waitForInput();

                        continue;
                    } else {

                        final Card ct = Solver.getNextMove(activity.getTable().getAvailableMovesFor(currentPlayer));
                        Log.d(DEBUG_TAG, "card played : " + ct.toString());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                currentMove = currentPlayer.playCard(ct);
                            }
                        });

                        // wait for UI refresh
                        waitForInput();

                        synchronized (activity.getTable()) {
                            activity.getTable().addNewCardToTable(ct);
                        }

                        addNewCardToTable(ct);
                        waitForInput();

                        // show played move
                        showPlayedMove(currentPlayer, ct);
                        // wait for ui refresh
                        waitForInput();

                        activity.getTable().incrementCurrentPlayerIndex();
                        currentMove = null;
                    }
                }

                Log.d(DEBUG_TAG, "(P)move : " + currentPlayer.getName() + " cards : " + currentPlayer.getCards().toString());

                // TODO : move this block to main game activity's on player won handler
                // checks if any of the players has won
                if (currentPlayer.getCards().isEmpty()) {
                    Log.d(TAG, " empty cards for player " + currentPlayer.getName());
                    isFinished = true;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.getMainDisplayTextView().setText(Html.fromHtml("<font color=\"#00ff00\">Player won : " + currentPlayer.getName() + "</font>"));
                            PlayerView pv = activity.getViewForPlayer(currentPlayer);
                            if (pv == null) {
                                activity.emptyCallback();
                            } else {
                                pv.setCurrentPlayer();
                                activity.emptyCallback();
                            }
                        }
                    });
                    // waits for UI refresh
                    waitForInput();
                }
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "thread " + name + " interrupted.");
        }
        Log.d(TAG, "thread " + name + " exiting...");
        return null;
    }

    private void showPlayerThinking(final Player currentPlayer) {
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


    private void showPlayerPassed(final Player currentPlayer) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String msg = currentPlayer.getName() + " passed current move ";
                activity.getMainDisplayTextView().setText(msg);
                PlayerView pv = activity.getViewForPlayer(currentPlayer);
                if (pv != null) {
                    pv.removeCurrentPlayer();
                    activity.emptyCallback();
                } else {
                    msg = currentPlayer.getName() + " passed this move";
                    activity.getMainDisplayTextView().setText(msg);
                    activity.emptyCallback();
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
                activity.emptyCallback();
            }
        });
    }

    private void addNewCardToTable(final Card ct) {
        Log.d(DEBUG_TAG, "drawing card : " + ct.toString());
        Handler refresh = new Handler(Looper.getMainLooper());
        refresh.post(new Runnable() {
            @Override
            public void run() {
                CardView cv = activity.getCardViewOnTableFor(ct);
                if (cv != null) {
                    cv.setCard(ct, true);
                    cv.setVisibility(View.VISIBLE);
                    Log.d(DEBUG_TAG, "drawn card : " + ct.toString());
                } else Log.d(DEBUG_TAG, "card not drawn : " + ct.toString());
                activity.emptyCallback();
            }
        });
    }

    public void kill() {
        cancel(true);
    }

}
