package com.comyr.pg18.sevenhearts.background.tasks;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import com.comyr.pg18.sevenhearts.R;
import com.comyr.pg18.sevenhearts.utils.PreferenceHelper;
import com.comyr.pg18.sevenhearts.utils.listeners.OnGameSettingsAlteredListener;

/**
 * Created by pranav on 5/12/16.
 * On 12:59 PM
 */
public class BackgroundMusicTask extends AsyncTask<Void, Void, String> implements OnGameSettingsAlteredListener{
    private MediaPlayer mp;
    private Context context;

    public BackgroundMusicTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        boolean b = PreferenceHelper.getInstance(context).rb(PreferenceHelper.KEY_SETTINGS_VOLUME, true);
        PreferenceHelper.getInstance(context).setOnGameSettingsAlteredListener(this);
        if (b) {
            mp = MediaPlayer.create(context, R.raw.notification);
            mp.setLooping(true);
            mp.start();
        }
        return null;
    }

    @Override
    public void onMusicAltered() {
        if(mp != null) {
            boolean b = PreferenceHelper.getInstance(context).rb(PreferenceHelper.KEY_SETTINGS_VOLUME);
            if(b) {
                mp.start();
            } else {
                mp.stop();
            }
        }
    }
}
