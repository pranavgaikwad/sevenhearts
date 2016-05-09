package com.comyr.pg18.sevenhearts.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.comyr.pg18.sevenhearts.R;
import com.comyr.pg18.sevenhearts.network.analytics.MixPanel;
import com.comyr.pg18.sevenhearts.ui.activities.base.CustomActivity;
import com.comyr.pg18.sevenhearts.ui.utils.FontUtils;
import com.comyr.pg18.sevenhearts.ui.utils.GameData;
import com.comyr.pg18.sevenhearts.ui.utils.helper.ActivityOptionHelper;

import info.hoang8f.widget.FButton;

public class MainActivity extends CustomActivity {
    private final String TAG = "MainActivity";

    private FButton startGameButton;
    private TextView mainLabelTextView;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setOptions(ActivityOptionHelper.getOptionsForActivity(ActivityOptionHelper.ACTIVITY_MAIN));
        setContentView(R.layout.activity_main);

        mAnalytics.trackAction(MixPanel.ACTION_ACTIVITY_OPEN, MixPanel.TAG_ACTIVITY, TAG);

        mainActivity = this;

        initUI();

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mainActivity, GameActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void initUI() {
        super.initUI();
        startGameButton = (FButton) findViewById(R.id.button_play_game);
        mainLabelTextView = (TextView) findViewById(R.id.main_label);
        mainLabelTextView.setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_CARTWHEEL));
        String mainTitle = "<font color=\"#F64A8A\">BADAM</font> <font color=\"#FFFFFF\">SATTI</font>";
        mainLabelTextView.setText(Html.fromHtml(mainTitle));
    }
}
