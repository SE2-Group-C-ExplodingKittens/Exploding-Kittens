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

import com.example.se2_exploding_kittens.ChoosePlayerViewHolder;
import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.PlayerManager;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;

import java.util.ArrayList;
import java.util.Random;

public class FavorCard implements Card, ChoosePlayerViewHolder.OnPlayerSelectedListener, ChoosePlayerViewHolder.OnNoPlayerSelectedListener {

    public static final int FAVOR_CARD_ID = 9;
    private ArrayList<String> playerIDs;
    private NetworkManager networkManager;
    private int playerID;

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

    public void handleActions(Player player, NetworkManager networkManager, DiscardPile discardPile, Context context) {
        if (player != null) {
            //player is null if this card is played on another client, on the local client or the sever this contains the respective object
            if (context != null) {
                //context is null if the server broadcasts this method
                //it has to be null in order not to call this method

                //so we can get the connection when the button gets (not) pressed
                setNetworkManager(networkManager);
                setPlayerID(player.getPlayerId());
                showChoosePlayerLayout(player.getPlayerId(), networkManager, context);
            }
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(FAVOR_CARD_ID));
            GameManager.sendNopeEnabled(networkManager);
        }
        discardPile.putCard(this);
    }

    private int getPlayerID() {
        return playerID;
    }

    private void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    private void showChoosePlayerLayout(int playerID, NetworkManager networkManager, Context context) {
        if (NetworkManager.isServer(networkManager)) {
            playerIDs = PlayerManager.getInstance().getPlayersIDs();
        } else if (networkManager.getConnectionRole() == TypeOfConnectionRole.CLIENT) {
            playerIDs = GameLogic.getPlayerIDList();
        }

        // Remove own playerID
        playerIDs.remove(Integer.toString(playerID));

        String playerOneID = playerIDs.get(0);
        String playerTwoID = playerIDs.get(1);
        String playerThreeID = playerIDs.get(2);
        String playerFourID = playerIDs.get(3);

        showPopUp(playerOneID, playerTwoID, playerThreeID, playerFourID, context);
    }

    private void showPopUp(String playerOneID, String playerTwoID, String playerThreeID, String playerFourID, Context context) {
        ChoosePlayerViewHolder choosePlayerViewHolder = new ChoosePlayerViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.favor_card_choose_player_layout, null));

        PopupWindow popupWindow = new PopupWindow(context);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setContentView(choosePlayerViewHolder.itemView.getRootView());
        popupWindow.setFocusable(true);
        // must be match_parent to be "fullscreen"
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.showAtLocation(((Activity) context).getWindow().getDecorView().getRootView(), Gravity.CENTER, 0, 0);
        choosePlayerViewHolder.bindData(playerOneID, playerTwoID, playerThreeID, playerFourID, popupWindow, this);

        // Display popup
        choosePlayerViewHolder.run(new Handler(), popupWindow, this);
    }

    @Override
    public void onPlayerSelected(String playerID) {
        //handle when Button got pressed in time
        GameManager.sendGiveAwayCard(getPlayerID() + ":" + playerID, getNetworkManager());
    }

    @Override
    public void onNoPlayerSelected() {
        //handle when time ran out
        GameManager.sendGiveAwayCard(getPlayerID() + ":" + getRandomPlayer(playerIDs), getNetworkManager());
    }

    private String getRandomPlayer(ArrayList<String> playerIDs) {
        // remove null values
        ArrayList<String> nonNullPlayers = new ArrayList<>();
        for (String playerID : playerIDs) {
            if (playerID != null) {
                nonNullPlayers.add(playerID);
            }
        }

        if (nonNullPlayers.isEmpty()) {
            return null;
        }

        // get random index of non null players
        Random random = new Random();
        int randomIndex = random.nextInt(nonNullPlayers.size());

        return nonNullPlayers.get(randomIndex);
    }

    private void setNetworkManager(NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    private NetworkManager getNetworkManager() {
        return this.networkManager;
    }
}
