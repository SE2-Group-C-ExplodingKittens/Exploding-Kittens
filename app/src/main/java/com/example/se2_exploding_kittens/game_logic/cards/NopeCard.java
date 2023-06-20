package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;

public class NopeCard implements Card {

    public static final int NOPE_CARD_ID = 10;

    public NopeCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.nopecard;
    }

    @Override
    public int getCardID() {
        return NOPE_CARD_ID;
    }

    public void handleActions(Player player, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager, Deck deck) {
        if (player != null) {
            //player is null if this card is played on another client, on the local client or the sever this contains the respective object
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(NOPE_CARD_ID));
            GameManager.sendNopeEnabled(networkManager);
            if (NetworkManager.isServer(networkManager)) {
                if (GameLogic.lastCardPlayedExceptNope instanceof ShuffleCard) {
                    deck.undoShuffle();
                    GameManager.distributeDeck(networkManager, deck);
                } else if (GameLogic.lastCardPlayedExceptNope instanceof AttackCard) {
                    turnManager.resumePreviousGameState();
                } else if (GameLogic.lastCardPlayedExceptNope instanceof SkipCard) {
                    turnManager.resumePreviousGameState();
                }
            }
        }
        discardPile.putCard(this);
    }
}
