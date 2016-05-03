package com.comyr.pg18.sevenhearts.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
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

import java.util.Random;

/**
 * Created by pranav on 5/1/16.
 * In package com.comyr.pg18.sevenhearts.ui.views
 */
public class PlayerView extends LinearLayout {
    private static final String COLOR_BLUE = "#0000ff";
    private static final String COLOR_YELLOW = "#ffff00";
    private static final String COLOR_WHITE = "#ffffff";
    private static String nameTextColor = COLOR_WHITE;
    private final String TAG = "PlayerView";
    private Player player;
    // player view has an image view for player image
    // and a text view for player's text description
    private ImageView imageView;
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

    public void setCurrentPlayer() {
        setBackgroundColor(getResources().getColor(R.color.current_player_background_color));
    }

    public void removeCurrentPlayer() {
        setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    public void setOnPageSureColor() {
        nameTextColor = COLOR_YELLOW;
    }

    public void updateNameWithCount() {
        String count = String.valueOf(player.getCards().size());
        String n = " <font color=\"" + nameTextColor + "\">" + player.getName() + "(" + count + ")" + "</font>";
        textView.setText(Html.fromHtml(n));

    }

    private int getRandomUserImage() {
        Random r = new Random();
        int i = new Random(System.nanoTime()).nextInt(999999);
        i = i % 9;
        Log.d(TAG, String.valueOf(i));
        switch (i) {
            case 0:
                return R.drawable.ic_player_b_golfer;

            case 1:
                return R.drawable.ic_player_detective;

            case 2:
                return R.drawable.ic_player_golfer;

            case 3:
                return R.drawable.ic_player_mature;

            case 4:
                return R.drawable.ic_player_motor;

            case 5:
                return R.drawable.ic_player_swat;

            case 6:
                return R.drawable.ic_player_tennis;

            case 7:
                return R.drawable.ic_player_thief;

            case 8:
                return R.drawable.ic_player_young;
        }
        return R.drawable.ic_player_young;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
        setOrientation(VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
        setLayoutParams(layoutParams);
        ResolutionHelper.getInstance().loadScreenResolution(getContext());
        CustomResolution res = ResolutionHelper.getInstance().getResolutionForView(ResolutionHelper.VIEW_PLAYER);
        imageView = new ImageView(getContext());
        imageView.setImageResource(getRandomUserImage());
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(res.getHeight(), res.getHeight(), Gravity.CENTER_HORIZONTAL);
        imageParams.setMargins(0, 3, 0, 3);
        imageView.setLayoutParams(imageParams);
        textView = new TextView(getContext());
        textView.setTypeface(FontUtils.getTypeface(getContext(), FontUtils.FONT_CARTWHEEL));
        textView.setTextColor(getResources().getColor(R.color.white));
        textView.setText(player.getName() + "(" + String.valueOf(player.getCards().size()) + ")");
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0, 3, 0, 3);
        textView.setLayoutParams(textParams);
        addView(imageView);
        addView(textView);
    }
}
