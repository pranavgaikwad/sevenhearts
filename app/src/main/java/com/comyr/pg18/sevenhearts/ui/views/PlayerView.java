package com.comyr.pg18.sevenhearts.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comyr.pg18.sevenhearts.R;
import com.comyr.pg18.sevenhearts.game.resources.Player;
import com.comyr.pg18.sevenhearts.ui.utils.CustomResolution;
import com.comyr.pg18.sevenhearts.ui.utils.FontUtils;
import com.comyr.pg18.sevenhearts.ui.utils.helper.ResolutionHelper;

import java.util.Collections;
import java.util.Random;
import java.util.Stack;

/**
 * Created by pranav on 5/1/16.
 * In package com.comyr.pg18.sevenhearts.ui.views
 */
public class PlayerView extends LinearLayout {
    private static final String COLOR_BLUE = "#0000ff";
    private static final String COLOR_YELLOW = "#ffff00";
    private static final String COLOR_WHITE = "#c5c5c5";

    /**
     * stores drawables of player avatars. These
     * drawables are used as source to player avatar image views
     */
    private static final int[] drawable_ids = {R.drawable.ic_player_king, R.drawable.ic_player_bot, R.drawable.ic_player_magician, R.drawable.ic_player_coder, R.drawable.ic_player_young, R.drawable.ic_player_thief, R.drawable.ic_player_tennis, R.drawable.ic_player_swat, R.drawable.ic_player_motor, R.drawable.ic_player_mature, R.drawable.ic_player_b_golfer, R.drawable.ic_player_detective, R.drawable.ic_player_golfer};
    /**
     * stack that stores drawables for player avatar image views
     *
     * @see PlayerView#getRandomUserImageDrawable()
     */
    private static Stack<Integer> drawables = new Stack<>();

    /**
     * this variable determines the color of the name string
     * that will be associated with the current {@link Player}
     * object. The name string displays the name of the player
     * along with a count of cards of that player.
     */
    private String nameTextColor = COLOR_WHITE;
    /**
     * {@link Player} object associated with the current view.
     */
    private Player player;
    /**
     * Player View has an image view which will display player
     * avatar image.
     */
    private ImageView imageView;
    /**
     * A text view to display player name. Refer : {@link Player}
     */
    private TextView textView;

    public PlayerView(Context context) {
        super(context);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // TODO : target api
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * call this method to reload drawables every time
     * game is started. It will make sure that unique
     * avatar images are generated.
     */
    public static void reloadDrawables() {
        drawables = new Stack<>();
        for (int id : drawable_ids) {
            drawables.push(id);
        }
    }

    /**
     * highlights the background of current view with given background color.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setCurrentPlayer() {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_bg_player_view_focussed) );
        } else {
            setBackground(getResources().getDrawable(R.drawable.drawable_bg_player_view_focussed));
        }
    }

    /**
     * un-highlights the background of the player view
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void removeCurrentPlayer() {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            setBackgroundDrawable(getResources().getDrawable(R.drawable.drawable_bg_player_view_normal) );
        } else {
            setBackground( getResources().getDrawable(R.drawable.drawable_bg_player_view_normal));
        }
    }

    /**
     * changes the color of the name String to yellow to
     * indicate that the {@link Player} associated with
     * this {@link PlayerView} has turned all page sure.
     */
    public void setOnPageSureColor() {
        nameTextColor = COLOR_YELLOW;
        updateNameWithCount();
    }

    /**
     * re-draws the text view with updated card count
     * card count = player.getCards().size()
     */
    public void updateNameWithCount() {
        String count = String.valueOf(player.getCards().size());
        String n = " <font color=\"" + nameTextColor + "\">" + player.getName() + "(" + count + ")" + "</font>";
        textView.setText(Html.fromHtml(n));
    }

    /**
     * returns random avatar image for {@link PlayerView}
     *
     * @return drawable id
     */
    private synchronized int getRandomUserImageDrawable() {
        if (drawables == null) {
            reloadDrawables();
        }
        Collections.shuffle(drawables, new Random(System.nanoTime()));
        return drawables.pop();
    }

    /**
     * returns associated {@link Player} object with current {@link PlayerView}
     * @return player object
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * sets {@link Player} for corresponding {@link PlayerView} object
     * @param player associated with {@link PlayerView}
     */
    public void setPlayer(Player player) {
        this.player = player;
        setOrientation(VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.setMargins(0, 0, 0, 4);
        setPadding(2, 2, 2, 2);
        setLayoutParams(layoutParams);
        ResolutionHelper.getInstance().loadScreenResolution(getContext());
        CustomResolution res = ResolutionHelper.getInstance().getResolutionForView(ResolutionHelper.VIEW_PLAYER);
        imageView = new ImageView(getContext());
        imageView.setImageResource(getRandomUserImageDrawable());
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(res.getHeight(), res.getHeight(), Gravity.CENTER_HORIZONTAL);
        imageParams.setMargins(0, 2, 0, 2);
        imageView.setLayoutParams(imageParams);
        textView = new TextView(getContext());
        textView.setTypeface(FontUtils.getTypeface(getContext(), FontUtils.FONT_CHAU));
        textView.setTextColor(Color.parseColor(COLOR_WHITE));
        textView.setText(player.getName() + "(" + String.valueOf(player.getCards().size()) + ")");
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0, 2, 0, 2);
        textParams.gravity = Gravity.CENTER_HORIZONTAL;
        textView.setLayoutParams(textParams);
        addView(imageView);
        addView(textView);
        removeCurrentPlayer();
    }
}
