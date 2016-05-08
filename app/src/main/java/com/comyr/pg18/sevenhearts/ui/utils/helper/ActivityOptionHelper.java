package com.comyr.pg18.sevenhearts.ui.utils.helper;

import com.comyr.pg18.sevenhearts.ui.utils.CustomActivityOptions;

/**
 * Created by pranav on 4/30/16.
 * In package ${PACKAGE_NAME}
 */
public class ActivityOptionHelper {

    public static final int ACTIVITY_MAIN = 0;
    public static final int ACTIVITY_GAME = 1;

    /**
     * Every new activity that has to be created is to be inherited from
     * {@link com.comyr.pg18.sevenhearts.ui.activities.base.CustomActivity}
     * CustomAcitivity takes different options to customize activity behaviour
     * following method is used to get {@link CustomActivityOptions} object for
     * required activity.
     *
     * @param i For example, @see ActivityOptionHelper#ACTIVITY_GAME
     * @return {@link CustomActivityOptions} object
     */
    public static CustomActivityOptions getOptionsForActivity(int i) {
        CustomActivityOptions options = new CustomActivityOptions();
        switch (i) {
            case ACTIVITY_GAME:
                options.addNewOption(CustomActivityOptions.OPTION_LANDSCAPE);
                options.addNewOption(CustomActivityOptions.OPTION_NO_ACTION_BAR);
                break;
            case ACTIVITY_MAIN:
                options.addNewOption(CustomActivityOptions.OPTION_LANDSCAPE);
                options.addNewOption(CustomActivityOptions.OPTION_NO_ACTION_BAR);
                break;
        }
        return options;
    }
}
