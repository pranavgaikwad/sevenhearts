package com.comyr.pg18.sevenhearts.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.ui.utils.FontUtils;

import org.w3c.dom.Text;

/**
 * Created by pranav on 5/9/16.
 * This class defines a row in the score card. Every row
 * in the scorecard must have player name along with their
 * scores. This class takes a {@link com.comyr.pg18.sevenhearts.game.resources.Player} object
 * to display specific player information.
 */
public class ScoreCardRow extends LinearLayout{
    /**
     * The instance of the Player associated with current score row.
     */
    private Player player;
    /**
     * Hex value for text color to be displayed in the row.
     * Default color is white.
     */
    private String color = "#ffffff";
    /**
     * Text views are used as a base element to show scores.
     * One of which is used for player name, while second one
     * will display the player's score. @see #player
     */
    private TextView nameTextView, scoreTextView;

    public ScoreCardRow(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
    }

    public ScoreCardRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }

    public ScoreCardRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScoreCardRow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setOrientation(HORIZONTAL);
    }
    /**
     * Sets text color to given value.
     * @param color Hexadecimal value of the required color
     */
    public ScoreCardRow withTextColor(String color) {
        this.color = color;
        return this;
    }
    /**
     * Sets #player object to given value.
     * @param player {@link Player} object to associate it with the row.
     */
    public ScoreCardRow forPlayer(Player player) {
        this.player = player;
        return this;
    }

    /**
     * Initiates the text views with proper player
     * information. Sets up typefaces and fonts.
     * @return Returns own object {@link ScoreCardRow }
     */
    public ScoreCardRow init() {
        int s = player.getScore();
        String scoreString =  " : " + String.valueOf(s);
        nameTextView = new TextView(getContext());
        scoreTextView = new TextView(getContext());
        nameTextView.setTypeface(FontUtils.getTypeface(getContext(), FontUtils.FONT_RIGHTEOUS));
        scoreTextView.setTypeface(FontUtils.getTypeface(getContext(), FontUtils.FONT_RIGHTEOUS));
        nameTextView.setTextColor(Color.parseColor(color));
        scoreTextView.setTextColor(Color.parseColor(color));
        nameTextView.setText(player.getName());
        scoreTextView.setText(scoreString);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(5, 0, 5, 0);
        nameTextView.setLayoutParams(params);
        scoreTextView.setLayoutParams(params);
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        addView(nameTextView);
        addView(scoreTextView);
        return this;
    }

    /**
     * This method converts the string to a fixed length string in order to
     * maintain the symmetry inside score card row.
     * @param string String to be displayed
     * @param length Maximum width of the string
     * @return
     */
    private String fixedLengthString(String string, int length) {
        return String.format("%1$"+length+ "s", string);
    }
}
