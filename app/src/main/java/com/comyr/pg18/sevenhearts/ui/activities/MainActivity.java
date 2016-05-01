package com.comyr.pg18.sevenhearts.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import com.comyr.pg18.sevenhearts.R;
import com.comyr.pg18.sevenhearts.ui.activities.base.CustomActivity;
import com.comyr.pg18.sevenhearts.ui.utils.helper.ActivityOptionHelper;

public class MainActivity extends CustomActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setOptions(ActivityOptionHelper.getOptionsForActivity(ActivityOptionHelper.ACTIVITY_MAIN));
        setContentView(R.layout.activity_main);

        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

}
