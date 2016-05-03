package com.comyr.pg18.sevenhearts.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.comyr.pg18.sevenhearts.R;
import com.comyr.pg18.sevenhearts.ui.activities.base.CustomActivity;
import com.comyr.pg18.sevenhearts.ui.utils.FontUtils;
import com.comyr.pg18.sevenhearts.ui.utils.GameData;
import com.comyr.pg18.sevenhearts.ui.utils.helper.ActivityOptionHelper;

import at.markushi.ui.CircleButton;

public class MainActivity extends CustomActivity {

    private Button playerNameButton;
    private EditText playerNameEditText;
    private CircleButton startGameButton;
    private TextView mainLabelTextView;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setOptions(ActivityOptionHelper.getOptionsForActivity(ActivityOptionHelper.ACTIVITY_MAIN));
        setContentView(R.layout.activity_main);

        mainActivity = this;

        initUI();

        playerNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playerName = playerNameEditText.getText().toString();
                if (playerName == null) playerName = "Player1";
                else if (playerName.equals("")) playerName = "Player1";
                else {
                    GameData.thisPlayerName = playerName;
                }
            }
        });

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
        playerNameButton = (Button) findViewById(R.id.button_player_name);
        playerNameEditText = (EditText) findViewById(R.id.edit_text_player_name);
        startGameButton = (CircleButton) findViewById(R.id.button_play_game);
        mainLabelTextView = (TextView) findViewById(R.id.main_label);
        mainLabelTextView.setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_CARTWHEEL));
        ((TextView) findViewById(R.id.play_label)).setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_CARTWHEEL));
        String mainTitle = "<font color=\"#F64A8A\">BADAM</font> <font color=\"#FFFFFF\">SATTI</font>";
        mainLabelTextView.setText(Html.fromHtml(mainTitle));
    }
}
