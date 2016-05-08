package com.comyr.pg18.sevenhearts.ui.activities.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.comyr.pg18.sevenhearts.network.analytics.MixPanel;
import com.comyr.pg18.sevenhearts.ui.utils.CustomActivityOptions;

/**
 * Created by pranav on 4/30/16.
 * Use this activity as the base of all activities in this project
 * This activity is LANDSCAPE ONLY
 */
public class CustomActivity extends AppCompatActivity {
    /**
     * analytics instance associated with this and all other
     * derived classes from this activity.
     */
    protected MixPanel mAnalytics;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mAnalytics = MixPanel.getInstance(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAnalytics = MixPanel.getInstance(this);
    }

    /**
     * sets customizable options for instance of the activity
     * @param options Refer : {@link CustomActivityOptions} for detailed info
     */
    protected void setOptions(CustomActivityOptions options) {
        for (String option : options.getOptions()) {
            switch (option) {
                case CustomActivityOptions.OPTION_LANDSCAPE:
                    // sets default orientation as landscape
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case CustomActivityOptions.OPTION_NO_ACTION_BAR:
                    getSupportActionBar().hide();
                    break;
            }
        }
    }

    protected void initUI() {

    }

    public MixPanel getmAnalytics() {
        return mAnalytics;
    }

    @Override
    protected void onDestroy() {
        mAnalytics.flush();
        super.onDestroy();
    }
}
