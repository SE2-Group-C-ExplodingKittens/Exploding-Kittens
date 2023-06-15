package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;

public class AttackCard implements Card {

    public static final int ATTACK_CARD_ID = 1;

    public AttackCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.attackcard;
    }

    @Override
    public int getCardID() {
        return ATTACK_CARD_ID;
    }

    public void handleAttackActions(Player player, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager) {
        if (player != null) {
            //player is null if this card is played on another client, on the local client or the sever this contains the respective object
            int remainingTurns = player.getPlayerTurns();
            //End your turn(s) without drawing
            player.setPlayerTurns(0);
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(ATTACK_CARD_ID));
            GameLogic.finishTurn(player, networkManager, remainingTurns + 1, turnManager);
            GameManager.sendNopeEnabled(networkManager);
        }
        discardPile.putCard(this);
    }
}
