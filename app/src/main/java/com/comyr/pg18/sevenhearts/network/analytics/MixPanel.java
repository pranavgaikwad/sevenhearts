package com.comyr.pg18.sevenhearts.network.analytics;


import android.content.Context;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pranav on 5/7/16.
 * In package com.comyr.pg18.sevenhearts.network.analytics
 */
public class MixPanel {
    public static final String ACTION_ACTIVITY_OPEN = "Activity Open";
    public static final String ACTION_BACK_PRESSED = "Back Press";
    public static final String ACTION_PLAYER_WON = "Won";
    public static final String ACTION_CRASH = "Crash";
    public static final String TAG_ACTIVITY = "Activity";
    public static final String TAG_PLAYER = "Player";
    public static final String TAG_CAUSE = "Cause";
    private static MixPanel instance = null;
    private final String PROJECT_TOKEN = "1fbc375b41f87af3dd14780f059a862a";
    private Context context;
    private MixpanelAPI api;

    public MixPanel(Context context) {
        this.context = context;
        api = MixpanelAPI.getInstance(context, PROJECT_TOKEN);
    }

    public static MixPanel getInstance() {
        return instance;
    }

    public synchronized static MixPanel getInstance(Context context) {
        if (instance == null) {
            instance = new MixPanel(context);
        }
        return instance;
    }

    public void unRegisterMixPanel() {
        instance = null;
    }

    public void trackAction(String action) {
        api.track(action);
    }

    public void trackAction(String action, JSONObject payload) {
        api.track(action, payload);
    }

    public void trackAction(String action, String... payloads) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (payloads.length % 2 == 0) {
                for (int i = 0; i < payloads.length; i++) {
                    if ((i % 2) == 0) {
                        String payload = payloads[i + 1];
                        String tag = payloads[i];
                        jsonObject.put(tag, payload);
                    }
                }
            }
        } catch (JSONException e) {
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        trackAction(action, jsonObject);
    }

    public void flush() {
        api.flush();
    }

}