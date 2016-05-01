package com.comyr.pg18.sevenhearts.ui.utils.helper;

import com.comyr.pg18.sevenhearts.ui.utils.CustomActivityOptions;

/**
 * Created by pranav on 4/30/16.
 * In package ${PACKAGE_NAME}
 */
public class ActivityOptionHelper {

    public static final int ACTIVITY_MAIN = 0;
    public static final int ACTIVITY_GAME = 1;

    public static CustomActivityOptions getOptionsForActivity(int i) {
        CustomActivityOptions options = new CustomActivityOptions();
        switch (i) {
            case ACTIVITY_GAME:
                options.addNewOption(CustomActivityOptions.OPTION_LANDSCAPE);
                options.addNewOption(CustomActivityOptions.OPTION_NO_ACTION_BAR);
                break;
            case ACTIVITY_MAIN:
                options.addNewOption(CustomActivityOptions.OPTION_LANDSCAPE);
                break;
        }
        return options;
    }
}
