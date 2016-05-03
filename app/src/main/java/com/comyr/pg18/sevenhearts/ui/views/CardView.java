package com.comyr.pg18.sevenhearts.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.comyr.pg18.sevenhearts.game.resources.Card;
import com.comyr.pg18.sevenhearts.ui.utils.CustomResolution;
import com.comyr.pg18.sevenhearts.ui.utils.helper.ResolutionHelper;

/**
 * Created by pranav on 5/1/16.
 */
public class CardView extends Button {
    private final String TAG = "CardView";
    private Card card;
    private String bgResName = "";
    private int heightInPixels, widthInPixels, heightInDp, widthInDp;

    public CardView(Context context) {
        super(context);
    }

    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setCard(Card card, boolean isTableCard) {
        this.card = card;
        this.bgResName = card.getBackgroundResourceName();
        ResolutionHelper.getInstance().loadScreenResolution(getContext());
        CustomResolution res = ResolutionHelper.getInstance().getResolutionForView(ResolutionHelper.VIEW_CARD);
        heightInPixels = (int) ((double) res.getHeight() / 1.2);
        widthInPixels = (int) ((double) res.getWidth() / 1.2);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthInPixels, heightInPixels);
        params.setMargins(0, 10, 0, 0);
        setLayoutParams(params);
        setBackgroundResource(getResources().getIdentifier(card.getBackgroundResourceName(), "drawable", getContext().getPackageName()));
    }

    /**
     * used to overlap the card over another card inside
     * a linear layout
     */
    public void setNegativeMargin() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthInPixels, heightInPixels);
        int marginInDp = ResolutionHelper.getInstance().convertPixelsToDp((float) (widthInPixels / 1.1), getContext());
        params.setMargins(marginInDp * -1, 0, 0, 0);
        setLayoutParams(params);
    }

    public Card getCard() {
        return card;
    }

    /**
     * links a card object along with the view
     *
     * @param card {@link Card}
     */
    public void setCard(Card card) {
        this.card = card;
        this.bgResName = card.getBackgroundResourceName();
        ResolutionHelper.getInstance().loadScreenResolution(getContext());
        CustomResolution res = ResolutionHelper.getInstance().getResolutionForView(ResolutionHelper.VIEW_CARD);
        heightInPixels = res.getHeight();
        widthInPixels = res.getWidth();

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(res.getWidth(), res.getHeight());
        setLayoutParams(params);
        setBackgroundResource(getResources().getIdentifier(card.getBackgroundResourceName(), "drawable", getContext().getPackageName()));
    }

}
