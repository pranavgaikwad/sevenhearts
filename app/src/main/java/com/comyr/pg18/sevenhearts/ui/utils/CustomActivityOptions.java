package com.comyr.pg18.sevenhearts.ui.utils;

import java.util.ArrayList;

/**
 * Created by pranav on 4/30/16.
 */
public class CustomActivityOptions {
    public static final String OPTION_LANDSCAPE = "landscape";
    public static final String OPTION_NO_ACTION_BAR = "noab";

    /**
     * an arraylist to store options string
     * for example, @see CustomActivityOptions#OPTION_LANDSCAPE
     */
    private ArrayList<String> options;

    public CustomActivityOptions() {
        options = new ArrayList<>();
    }

    /**
     * add new option to current {@link CustomActivityOptions} object
     *
     * @param option @see CustomActivityOptions
     */
    public void addNewOption(String option) {
        options.add(option);
    }

    /**
     * checks if the given option is present in options list @see CustomActivityOptions#options
     * @param option option
     * @return true if the option is set, false otherwise
     */
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
