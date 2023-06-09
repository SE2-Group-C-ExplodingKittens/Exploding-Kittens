package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;

public class ShuffleCard implements Card {

    public static final int SHUFFLE_CARD_ID = 12;

    public ShuffleCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.shufflecard;
    }

    @Override
    public int getCardID() {
        return SHUFFLE_CARD_ID;
    }

    public void handleShuffleActions(Player player, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager, Deck deck) {
        if (player != null) {
            //player is null if this card is played on another client, on the local client or the sever this contains the respective object
            player.setPlayerTurns(player.getPlayerTurns()-1);
            deck.shuffleDeck();
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(SHUFFLE_CARD_ID));
            if (player.getPlayerTurns() == 0 && networkManager.getConnectionRole() == TypeOfConnectionRole.SERVER) {
                GameLogic.finishTurn(player, networkManager, 1, turnManager);
                GameManager.sendNopeEnabled(networkManager);
            }
        }
        discardPile.putCard(this);
    }
}
