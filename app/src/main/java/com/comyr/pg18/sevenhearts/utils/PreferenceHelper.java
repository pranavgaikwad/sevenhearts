package com.comyr.pg18.sevenhearts.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.comyr.pg18.sevenhearts.utils.listeners.OnGameSettingsAlteredListener;

/**
 * Created by pranav on 5/10/16.
 * On 10:09 AM
 * <p>This class is used as a helper class. Whenever
 * there is a need of using shared preferences, this class
 * can be used</p>
 */
public class PreferenceHelper {
    /**
     * Shared preference identifier
     */
    private final String PREF_MAIN = "shandroid";
    /**
     * The shared preference object
     */
    private SharedPreferences prefs;
    /**
     * Static instance of the helper class
     */
    private static PreferenceHelper instance = null;
    /**
     * Used to broadcast game state changes
     */
    private OnGameSettingsAlteredListener l = null;
    /**
     * Returned as a default value for {@link #ri(String)}
     */
    public static final int STATUS_INT_NOT_FOUND = -9999;
    /**
     * Keys to access shared prefs for different properties
     */
    public static final String KEY_SETTINGS_VIBRATE = "s_vb";
    public static final String KEY_SETTINGS_VOLUME = "s_vm";
    public static final String KEY_GAMES_PLAYED = "st_gp";
    public static final String KEY_GAMES_WON = "st_gw";
    public static final String KEY_GAMES_WON_IN_ROW = "st_gwr";

    /**
     * Public constructor to initialize SharedPreferences#prefs object
     */
    public PreferenceHelper(Context context) {
        prefs = context.getSharedPreferences(PREF_MAIN, Context.MODE_PRIVATE);
    }

    /**
     * This method returns an instance of the class which is static in nature.
     *
     * @param context {@link Context} object
     * @return Instance of {@link PreferenceHelper} class
     */
    public static PreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceHelper(context);
        }
        return instance;
    }

    /**
     * Writes given key-value to share d prefs. Only applicable for values of String form.
     *
     * @param key key as a String object
     * @param val value as a String object
     */
    public void w(String key, String val) {
        SharedPreferences.Editor e = prefs.edit();
        e.putString(key, val);
        e.commit();
        updateListener(key);
    }

    /**
     * Writes given integer value along with the specified key to the shared preference.
     *
     * @param key key as a String object
     * @param val integer value
     */
    public void w(String key, int val) {
        SharedPreferences.Editor e = prefs.edit();
        e.putInt(key, val);
        e.commit();
        updateListener(key);
    }

    /**
     * Writes given boolean value along with the specified key to the shared preference.
     *
     * @param key key as a String object
     * @param val boolean value
     */
    public void w(String key, boolean val) {
        SharedPreferences.Editor e = prefs.edit();
        e.putBoolean(key, val);
        e.commit();
        updateListener(key);
    }

    /**
     * Broadcasts changed value according to provided key
     *
     * @param key Key for which value is changed
     */
    public void updateListener(String key) {
        if (l != null) {
            switch (key) {
                case KEY_SETTINGS_VOLUME:
                    l.onMusicSettingsAltered();
                    break;
            }
        }
    }

    /**
     * Game settings altered listener takes care of the volume and vibrate
     * settings change.
     */
    public void setOnGameSettingsAlteredListener(OnGameSettingsAlteredListener l) {
        this.l = l;
    }

    /**
     * This method is generalized for retrieving value from the
     * shared preferences which can be of any data type. Not all
     * datatypes are supported. Supported data types are id'd as
     * integers. For example,
     *
     * @param key key as a string object
     * @return string value for given key
     */
    public String rs(String key) {
        return prefs.getString(key, null);
    }

    /**
     * Reads a boolean value from shared prefs for given key.
     * Default value is False.
     *
     * @param key String key
     * @return Boolean value for given key
     */
    public boolean rb(String key) {
        return prefs.getBoolean(key, false);
    }

    /**
     * Reads a boolean value from shared prefs for given key with given default value.
     * For example, while reading shared prefs for game settings, for the first time,
     * volume and vibrate are kept ON unlike the default value mentioned in PreferenceHelper#rb
     *
     * @param key String key
     * @param def Default value expected, if the key is not set
     * @return boolean value for given key
     */
    public boolean rb(String key, boolean def) {
        return prefs.getBoolean(key, def);
    }

    /**
     * Reads integer value for given key from the shared preferences
     *
     * @param key String key
     * @return Integer value for given key. If not key is not found, {@link #STATUS_INT_NOT_FOUND} is returned
     */
    public int ri(String key) {
        return prefs.getInt(key, STATUS_INT_NOT_FOUND);
    }

    /**
     * Reads integer value from shared preference for given key
     *
     * @param key String key
     * @param def Default value to return, when key is not found
     * @return Integer value for given key
     */
    public int ri(String key, int def) {
        return prefs.getInt(key, def);
    }
}
