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
            player.setPlayerTurns(player.getPlayerTurns() - 1);
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(ATTACK_CARD_ID));
            // If the victim of an Attack Card plays an Attack Card on any of their turns,
            // the new target must take any remaining turns plus the number of attacks on
            // the Attack Card just played (e.g. 4 turns, then 6, and so on).
            GameLogic.finishTurn(player, networkManager, player.getPlayerTurns() + 2, turnManager);
            GameManager.sendNopeEnabled(networkManager);
        }
        discardPile.putCard(this);
    }
}
