package com.example.se2_exploding_kittens.game_logic.cards;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.TopThreeCardsViewHolder;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.Player;

import java.util.ArrayList;

public class SeeTheFutureCard implements Card {

    public static final int SEE_THE_FUTURE_CARD_ID = 11;

    public SeeTheFutureCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.seethefuturecard;
    }

    @Override
    public int getCardID() {
        return SEE_THE_FUTURE_CARD_ID;
    }

    public void handleFutureActions(Player player, NetworkManager networkManager, DiscardPile discardPile, Deck deck, Context context) {
        if (player != null) {
            //player is null if this card is played on another client, on the local client or the sever this contains the respective object
            if (context != null) {
                //context is null if the server broadcasts this method
                //it has to be null in order not to call this method
                showTopThreeCards(deck, context);
            }
            GameManager.showTopThreeCards(player.getPlayerId(), networkManager);
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(SEE_THE_FUTURE_CARD_ID));
            GameManager.sendNopeEnabled(networkManager);
        }
        discardPile.putCard(this);
    }

    private void showTopThreeCards(Deck deck, Context context) {
        ArrayList<Integer> threeCards = deck.getNextThreeCards();

        // Set Image resources
        int cardOne = threeCards.get(0);
        int cardTwo = threeCards.get(1);
        int cardThree = threeCards.get(2);

        showPopUp(cardOne, cardTwo, cardThree, context);
    }

    private void showPopUp(int cardOne, int cardTwo, int cardThree, Context context) {
        TopThreeCardsViewHolder topThreeCardsViewHolder = new TopThreeCardsViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.three_cards_layout, null));

        topThreeCardsViewHolder.bindData(cardOne, cardTwo, cardThree);
        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setContentView(topThreeCardsViewHolder.itemView);
        popupWindow.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(((Activity) context).getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);

        //Timer
        TextView timerTextView = topThreeCardsViewHolder.itemView.findViewById(R.id.textViewCounter);
        timerTextView.setText("5");
        Handler handler = new Handler();

        handleThread(handler, popupWindow, timerTextView);
    }

    private void handleThread(Handler handler, PopupWindow popupWindow, TextView timerTextView) {
        // Delay between each update in milliseconds
        final int delay = 1000;
        handler.postDelayed(new Runnable() {
            // Total time in milliseconds
            int remainingTime = 5000;

            @Override
            public void run() {
                if (popupWindow.isShowing()) {
                    // Subtract the delay from the remaining time
                    remainingTime -= delay;
                    // Calculate seconds
                    int seconds = remainingTime / 1000;

                    if (seconds > 0) {
                        timerTextView.setText(String.valueOf(seconds));
                        // Schedule the next update
                        handler.postDelayed(this, delay);
                    } else {
                        // Dismiss the PopupWindow after 5000ms
                        popupWindow.dismiss();
                    }
                }
            }
        }, delay);
    }
}
