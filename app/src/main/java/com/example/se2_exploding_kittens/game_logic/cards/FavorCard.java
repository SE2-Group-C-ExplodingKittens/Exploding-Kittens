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

import com.example.se2_exploding_kittens.ChoosePlayerViewHolder;
import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.Player;

import java.util.ArrayList;

public class FavorCard implements Card {

    public static final int FAVOR_CARD_ID = 9;

    public FavorCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.favorcard;
    }

    @Override
    public int getCardID() {
        return FAVOR_CARD_ID;
    }

    public void handleFavorActions(Player player, NetworkManager networkManager, DiscardPile discardPile, Context context) {
        if (player != null) {
            //player is null if this card is played on another client, on the local client or the sever this contains the respective object
            if (context != null) {
                //context is null if the server broadcasts this method
                //it has to be null in order not to call this method
                showChoosePlayerLayout(player.getPlayerId(), context);
                // Move lines when player has (or has not) chosen a player
                int playerID = 0;
                GameManager.sendGiveAwayCard(playerID, networkManager);
            }
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(FAVOR_CARD_ID));
            GameManager.sendNopeEnabled(networkManager);
        }
        discardPile.putCard(this);
    }

    private void showChoosePlayerLayout(int playerID, Context context) {
        PlayerManager playerManager = PlayerManager.getInstance();
        ArrayList<String> playerIDs = playerManager.getPlayersID();
        System.out.println(playerID);
        // Remove own playerID
        playerIDs.remove(Integer.toString(playerID));

        String playerOneID = playerIDs.get(0);
        String playerTwoID = playerIDs.get(1);
        String playerThreeID = playerIDs.get(2);
        String playerFourID = playerIDs.get(3);
        System.out.println(playerIDs);

        showPopUp(playerOneID, playerTwoID, playerThreeID, playerFourID, context);
    }

    private void showPopUp(String playerOneID, String playerTwoID, String playerThreeID, String playerFourID, Context context) {
        ChoosePlayerViewHolder choosePlayerViewHolder = new ChoosePlayerViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.favor_card_choose_player_layout, null));

        choosePlayerViewHolder.bindData(playerOneID, playerTwoID, playerThreeID, playerFourID);
        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setContentView(choosePlayerViewHolder.itemView);
        popupWindow.setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(((Activity) context).getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);

        //Timer
        TextView timerTextView = choosePlayerViewHolder.itemView.findViewById(R.id.textViewCounter);
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
