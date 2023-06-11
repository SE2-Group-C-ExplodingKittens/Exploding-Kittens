package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.GameActivity;
import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
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

    public void handleFutureActions(Player player, NetworkManager networkManager, DiscardPile discardPile, Deck deck) {
        if (player != null) {
            GameManager.showTopThreeCards(player.getPlayerId(), networkManager);
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(SEE_THE_FUTURE_CARD_ID));
            GameManager.sendNopeEnabled(networkManager);
        }
        discardPile.putCard(this);
    }
}
