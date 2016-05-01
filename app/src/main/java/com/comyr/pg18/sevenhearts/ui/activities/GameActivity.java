package com.comyr.pg18.sevenhearts.ui.activities;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.comyr.pg18.sevenhearts.R;
import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.game.resources.constants.Cards;
import com.comyr.pg18.sevenhearts.game.resources.constants.Suits;
import com.comyr.pg18.sevenhearts.ui.activities.base.CustomActivity;
import com.comyr.pg18.sevenhearts.ui.utils.helper.ActivityOptionHelper;
import com.comyr.pg18.sevenhearts.ui.views.CardView;

public class GameActivity extends CustomActivity {

    private LinearLayout playerCardsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setOptions(ActivityOptionHelper.getOptionsForActivity(ActivityOptionHelper.ACTIVITY_GAME));
        setContentView(R.layout.activity_game);
        initUI();

        CardView cv = new CardView(this);
        cv.setCard(new Card(Cards.QUEEN, Suits.HEARTS));
        CardView cv1 = new CardView(this);
        cv1.setCard(new Card(Cards.KING, Suits.HEARTS));
        cv1.setNegativeMargin();
        playerCardsLayout.addView(cv);
        playerCardsLayout.addView(cv1);
    }

    @Override
    protected void initUI() {
        super.initUI();
        playerCardsLayout = (LinearLayout) findViewById(R.id.linear_layout_player_cards);
    }
}
