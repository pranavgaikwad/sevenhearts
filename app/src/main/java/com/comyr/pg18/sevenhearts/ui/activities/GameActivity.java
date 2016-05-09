package com.comyr.pg18.sevenhearts.ui.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.comyr.pg18.sevenhearts.R;
import com.comyr.pg18.sevenhearts.background.tasks.GameInitTask;
import com.comyr.pg18.sevenhearts.background.threads.GameThread;
import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.game.resources.Table;
import com.comyr.pg18.sevenhearts.game.resources.constants.Suits;
import com.comyr.pg18.sevenhearts.game.resources.utils.exceptions.PlayerNotFoundException;
import com.comyr.pg18.sevenhearts.network.analytics.MixPanel;
import com.comyr.pg18.sevenhearts.ui.activities.base.CustomActivity;
import com.comyr.pg18.sevenhearts.ui.utils.FontUtils;
import com.comyr.pg18.sevenhearts.ui.utils.helper.ActivityOptionHelper;
import com.comyr.pg18.sevenhearts.ui.views.CardView;
import com.comyr.pg18.sevenhearts.ui.views.PlayerView;
import com.comyr.pg18.sevenhearts.ui.views.ScoreCardRow;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import info.hoang8f.widget.FButton;

public class GameActivity extends CustomActivity {
    /**
     * used to synchronize access to @see GameActivity#thread
     */
    private static final Object threadLock = new Object();
    /**
     * main {@link GameThread} object
     */
    private static GameThread thread;

    private final String TAG = "GameActivity";
    /**
     * holds players registered with current table. Refer : {@link Player}
     */
    private ArrayList<Player> players;
    /**
     * current table to which all the players and cards are registered. Refer : {@link Table}
     */
    private Table table;
    /**
     * the human player
     */
    private Player thisPlayer;
    /**
     * cards of the human player
     */
    private ArrayList<Card> thisPlayerCards;
    /**
     * cards in player's hands are arranged in this layout. Refer : {@link CardView} for details
     */
    private LinearLayout playerCardsLayout;
    /**
     * player views are arranged in player layout. Refer : {@link PlayerView}
     */
    private LinearLayout playersLayout;
    /**
     * this text view displays everything related to game
     */
    private TextView mainDisplayTextView;

    private GameActivity gameActivity;
    /**
     * cards when played, are placed on table. Table has different {@link CardView} objects to show cards on table.
     */
    private CardView heartsUpperCard, heartsLowerCard, diamondsUpperCard, diamondsLowerCard, spadesUpperCard, spadesLowerCard, clubsUpperCard, clubsLowerCard;
    /**
     * Background task that initiates the game. Refer : {@link GameInitTask}
     */
    private GameInitTask gameInitTask;

    public static GameThread getThread() {
        return thread;
    }

    public static void setThread(GameThread thread) {
        synchronized (threadLock) {
            GameActivity.thread = thread;
        }
    }

    public Player getThisPlayer() {
        return thisPlayer;
    }

    public void setThisPlayer(Player tp) {
        thisPlayer = tp;
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

        mAnalytics.trackAction(MixPanel.ACTION_ACTIVITY_OPEN, MixPanel.TAG_ACTIVITY, TAG);

        reset();

        gameActivity = this;

        initUI();

        gameInitTask = new GameInitTask(gameActivity);
        gameInitTask.execute();
    }

    /**
     * Main display text view is used as a board to display game state changes
     *
     * @return main display text view
     */
    public TextView getMainDisplayTextView() {
        return mainDisplayTextView;
    }

    /**
     * returns {@link PlayerView} object for given {@link Player} object
     *
     * @param player {@link Player} for which {@link PlayerView} object is to be returned
     * @return {@link PlayerView} object.  Null, if {@link PlayerView} is not found
     */
    public PlayerView getViewForPlayer(Player player) {
        if(player == null) {
            return null;
        }
        for (int i = 0; i < playersLayout.getChildCount(); i++) {
            PlayerView pv = (PlayerView) playersLayout.getChildAt(i);
            if (pv.getPlayer().equals(player)) {
                return pv;
            }
        }
        return null;
    }

    /**
     * returns {@link Player} object which is registered with current {@link Table} object
     *
     * @param name of the player for which {@link Player} object is to be returned
     * @return player object
     * @throws PlayerNotFoundException if given {@link Player} is not registered with current instance of {@link Table}
     */
    public Player getPlayerForName(String name) throws PlayerNotFoundException {
        Iterator<Player> it = players.iterator();
        while (it.hasNext()) {
            Player p = it.next();
            if (p.getName().equals(name)) return p;
        }
        throw new PlayerNotFoundException("In game activity, while getting this player");
    }

    /**
     * initiates UI components.
     */
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

    /**
     * Every card which is played, is to be placed on the table in a layout
     * this layout has 8 different {@link CardView} objects. Each card has
     * to be placed correctly in one of these views. This method returns
     * correct view for every {@link Card} that is passed
     * @param c {@link Card} object for which view is to be returned
     * @return appropriate {@link CardView} for given card
     */
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
    public void onBackPressed() {
        super.onBackPressed();
        mAnalytics.trackAction(MixPanel.ACTION_BACK_PRESSED, MixPanel.TAG_ACTIVITY, TAG);
        thread.kill();
        reset();
        finish();
    }

    /**
     * resets the game state
     * game is ready to be re-started,
     * but, only after, this method is called
     */
    private void reset() {
        gameInitTask = null;
        players = null;
        Table.reset();
        table = null;
        thisPlayer = null;
        thisPlayerCards = null;
        thread = null;
        /**
         * reload player avatars to ensure unique drawables
         */
        PlayerView.reloadDrawables();
    }

    /**
     * restarts the game
     */
    public void restartGame() {
        mAnalytics.trackAction(MixPanel.ACTION_GAME_RESTART);
        thread.kill();
        reset();
        resetViews();
        gameInitTask = new GameInitTask(gameActivity);
        gameInitTask.execute();
    }

    /**
     * Shows a score card along with an option to restart and exit
     * For restart, @see #restartGame for implementation of restart
     */
    public void showScoreCard() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_scorecard);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.container_player_scores);
        ((TextView) dialog.findViewById(R.id.title_scorecard)).setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_RIGHTEOUS));
        Collections.sort(players, new Comparator<Player>() {
            @Override
            public int compare(Player lhs, Player rhs) {
                if (lhs.getScore() > rhs.getScore()) return -1;
                else if (lhs.getScore() < rhs.getScore()) return 1;
                else return 0;
            }
        });
        int i = 0;
        for (Player p : players) {
            if(i == 0) mAnalytics.trackAction(MixPanel.ACTION_HIGHEST_SCORE, MixPanel.TAG_SCORE, String.valueOf(p.getScore()));
            ScoreCardRow row = new ScoreCardRow(this).forPlayer(p).init();
            linearLayout.addView(row);
            i++;
        }
        ((FButton) dialog.findViewById(R.id.button_restart_game)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                restartGame();
            }
        });
        ((FButton) dialog.findViewById(R.id.button_exit_game)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mAnalytics.trackAction(MixPanel.ACTION_GAME_EXIT);
                thread.kill();
                reset();
                finish();
            }
        });
        dialog.show();
    }

    /**
     * Before restarting game, all the views need to be reset.
     * This method removes all views that are related to the game.
     */
    public void resetViews() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                playersLayout.removeAllViews();
                playerCardsLayout.removeAllViews();
                removeCardViewsFromTable(heartsUpperCard, heartsLowerCard, spadesUpperCard, spadesLowerCard, diamondsLowerCard, diamondsUpperCard, clubsLowerCard, clubsUpperCard);
                mainDisplayTextView.setText(getString(R.string.text_welcome));
            }
        });
    }

    /**
     * Removes cards placed on the table. After call to this method,
     * all the cards in player's hands and cards on the table are
     * removed from their respective parent views.
     * @param views An array of {@link CardView} objects that hold
     *              the cards placed on the table.
     */
    public void removeCardViewsFromTable(CardView... views) {
        for(final CardView v : views) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    v.setBackgroundColor(Color.parseColor("#00000000"));
                }
            });
        }
    }
}

