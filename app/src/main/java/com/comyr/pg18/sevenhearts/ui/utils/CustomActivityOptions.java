package com.comyr.pg18.sevenhearts.ui.utils;

import java.util.ArrayList;

/**
 * Created by pranav on 4/30/16.
 */
public class CustomActivityOptions {
    public static final String OPTION_LANDSCAPE = "landscape";
    public static final String OPTION_NO_ACTION_BAR = "noab";
    private final String TAG = "CustomActivityOptions : ";
    private ArrayList<String> options;
    private int count;

    public CustomActivityOptions() {
        options = new ArrayList<>();
    }

    public void addNewOption(String option) {
        options.add(option);
    }

    public boolean isOptionSet(String option) {
        for (String o : options) {
            if (o.equals(option)) return true;
        }
        return false;
    }

    public ArrayList<String> getOptions() {
        return options;
    }
}
