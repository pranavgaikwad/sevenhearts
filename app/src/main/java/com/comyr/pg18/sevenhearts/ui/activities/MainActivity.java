package com.comyr.pg18.sevenhearts.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.comyr.pg18.sevenhearts.R;
import com.comyr.pg18.sevenhearts.background.tasks.BackgroundMusicTask;
import com.comyr.pg18.sevenhearts.network.analytics.MixPanel;
import com.comyr.pg18.sevenhearts.sys.SysUtils;
import com.comyr.pg18.sevenhearts.ui.activities.base.CustomActivity;
import com.comyr.pg18.sevenhearts.ui.utils.FontUtils;
import com.comyr.pg18.sevenhearts.ui.utils.helper.ActivityOptionHelper;
import com.comyr.pg18.sevenhearts.utils.PreferenceHelper;
import com.comyr.pg18.sevenhearts.utils.listeners.OnGameSettingsAlteredListener;

public class MainActivity extends CustomActivity implements OnGameSettingsAlteredListener{
    private final String TAG = "MainActivity";

    private Button startGameButton, statsButton;
    private MainActivity mainActivity;
    private ImageView vibrateButton, volumeButton;
    private int c1, c2;
    private TextView mainTitleTextView;
    private BackgroundMusicTask bgm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setOptions(ActivityOptionHelper.getOptionsForActivity(ActivityOptionHelper.ACTIVITY_MAIN));
        setContentView(R.layout.activity_main);

        PreferenceHelper.getInstance(this).setOnGameSettingsAlteredListener(this);

        playMusic();

        mAnalytics.trackAction(MixPanel.ACTION_ACTIVITY_OPEN, MixPanel.TAG_ACTIVITY, TAG);

        mainActivity = this;

        initUI();

        startGameButton.setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_HELVETICA));
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mainActivity, GameActivity.class);
                startActivity(i);
            }
        });
    }

    private void playMusic() {
        SysUtils.getInstance().initPlayer(this);
        SysUtils.getInstance().startPlayer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SysUtils.getInstance().resumePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SysUtils.getInstance().pausePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SysUtils.getInstance().stopPlayer();
    }

    @Override
    protected void initUI() {
        super.initUI();
        startGameButton = (Button) findViewById(R.id.button_play_game);
        statsButton = (Button) findViewById(R.id.button_game_stats);
        startGameButton.setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_HELVETICA));
        statsButton.setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_HELVETICA));
        vibrateButton = (ImageView) findViewById(R.id.toggle_button_vibrate);
        volumeButton = (ImageView) findViewById(R.id.toggle_button_volume);
        boolean isVolumeOn = PreferenceHelper.getInstance(this).rb(PreferenceHelper.KEY_SETTINGS_VOLUME, true);
        boolean isVibrateOn = PreferenceHelper.getInstance(this).rb(PreferenceHelper.KEY_SETTINGS_VIBRATE, true);
        mainTitleTextView = (TextView) findViewById(R.id.text_view_main_title);
        mainTitleTextView.setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_LETTERS));
        String title = "<font color=\"#E0E0E0\">Badam</font>" + "\n" + " <font color=\"#E0E0E0\">Satti</font>";
        mainTitleTextView.setText(Html.fromHtml(title));
        c1 = 1;
        c2 = 1;
        if(!isVolumeOn)  { volumeButton.setImageResource(R.drawable.ic_volume_off); c1 = 0;}
        if(!isVibrateOn) { vibrateButton.setImageResource(R.drawable.ic_vibrate_off); c2 = 0; }
        volumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (c1 == 0) {
                    volumeButton.setImageResource(R.drawable.ic_volume);
                    PreferenceHelper.getInstance(mainActivity).w(PreferenceHelper.KEY_SETTINGS_VOLUME, true);
                    c1 = 1;
                } else if (c1 == 1) {
                    volumeButton.setImageResource(R.drawable.ic_volume_off);
                    PreferenceHelper.getInstance(mainActivity).w(PreferenceHelper.KEY_SETTINGS_VOLUME, false);
                    c1 = 0;
                }
            }
        });
        vibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (c2 == 0) {
                    vibrateButton.setImageResource(R.drawable.ic_vibrate);
                    PreferenceHelper.getInstance(mainActivity).w(PreferenceHelper.KEY_SETTINGS_VIBRATE, true);
                    c2 = 1;
                } else if (c2 == 1) {
                    vibrateButton.setImageResource(R.drawable.ic_vibrate_off);
                    PreferenceHelper.getInstance(mainActivity).w(PreferenceHelper.KEY_SETTINGS_VIBRATE, false);
                    c2 = 0;
                }
            }
        });
    }

    @Override
    public void onMusicAltered() {
        boolean b = PreferenceHelper.getInstance(this).rb(PreferenceHelper.KEY_SETTINGS_VOLUME);
        if(b) {
            SysUtils.getInstance().initPlayer(this);
            SysUtils.getInstance().startPlayer();
        } else {
            SysUtils.getInstance().stopPlayer();
        }
    }
}
