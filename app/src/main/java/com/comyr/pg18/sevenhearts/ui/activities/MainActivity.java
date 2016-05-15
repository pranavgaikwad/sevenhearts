package com.comyr.pg18.sevenhearts.ui.activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.comyr.pg18.sevenhearts.R;
import com.comyr.pg18.sevenhearts.network.analytics.MixPanel;
import com.comyr.pg18.sevenhearts.ui.activities.base.CustomActivity;
import com.comyr.pg18.sevenhearts.ui.utils.FontUtils;
import com.comyr.pg18.sevenhearts.ui.utils.helper.ActivityOptionHelper;
import com.comyr.pg18.sevenhearts.utils.PreferenceHelper;
import com.comyr.pg18.sevenhearts.utils.listeners.OnGameSettingsAlteredListener;

public class MainActivity extends CustomActivity implements OnGameSettingsAlteredListener{
    /**
     * Debug tag
     */
    private final String TAG = "MainActivity";
    /**
     * Buttons on the main screen
     */
    private Button startGameButton, statsButton;
    /**
     * Activity instance
     */
    private MainActivity mainActivity;
    /**
     * Settings buttons
     */
    private ImageView vibrateButton, volumeButton;
    /**
     * Workaround for making image views to work like toggle buttons (For settings buttons)
     */
    private int c1 = 1, c2 = 1;
    /**
     * Main game title board
     */
    private TextView mainTitleTextView;
    /**
     * Media player to play music file in background
     */
    private MediaPlayer mp = null;
    /**
     * Initially, volume and vibrate settings are to be checked before starting media player
     */
    private boolean isVolumeOn, isVibrateOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setOptions(ActivityOptionHelper.getOptionsForActivity(ActivityOptionHelper.ACTIVITY_MAIN));
        setContentView(R.layout.activity_main);

        PreferenceHelper.getInstance(this).setOnGameSettingsAlteredListener(this);

        mAnalytics.trackAction(MixPanel.ACTION_ACTIVITY_OPEN, MixPanel.TAG_ACTIVITY, TAG);

        mainActivity = this;

        initUI();

        playMusic();

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
        mp = MediaPlayer.create(this, R.raw.intro);
        mp.setLooping(true);
        if (isVolumeOn) mp.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isVolumeOn) mp.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
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
        isVolumeOn = PreferenceHelper.getInstance(this).rb(PreferenceHelper.KEY_SETTINGS_VOLUME, true);
        isVibrateOn = PreferenceHelper.getInstance(this).rb(PreferenceHelper.KEY_SETTINGS_VIBRATE, true);
        mainTitleTextView = (TextView) findViewById(R.id.text_view_main_title);
        mainTitleTextView.setTypeface(FontUtils.getTypeface(this, FontUtils.FONT_SATTI));
        String title = "Badam" + "\n" + " Satti";
        mainTitleTextView.setText(title);
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
    public void onMusicSettingsAltered() {
        boolean b = PreferenceHelper.getInstance(this).rb(PreferenceHelper.KEY_SETTINGS_VOLUME);
        if(b) {
            mp = MediaPlayer.create(this, R.raw.intro);
            mp.setLooping(true);
            mp.start();
        } else {
            mp.stop();
        }
    }
}
