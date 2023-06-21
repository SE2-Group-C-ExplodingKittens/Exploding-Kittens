package com.example.se2_exploding_kittens.game_logic.cards;

import static com.example.se2_exploding_kittens.game_logic.cards.BombCard.BOMB_CARD_ID;

import com.example.se2_exploding_kittens.Network.GameManager;
import com.example.se2_exploding_kittens.Network.TypeOfConnectionRole;
import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.Deck;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
import com.example.se2_exploding_kittens.game_logic.GameLogic;
import com.example.se2_exploding_kittens.game_logic.Player;

public class DefuseCard implements Card {

    public static final int DEFUSE_CARD_ID = 8;

    public DefuseCard() {
        //This class in itself is a datatype, so we don't need to initialize anything else here.
    }

    @Override
    public int getImageResource() {
        return R.drawable.defusecard;
    }

    @Override
    public int getCardID() {
        return DEFUSE_CARD_ID;
    }

    public void handleActions(Player player, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager, Deck deck){
        if(player != null){
            //player is null if this card is played on another client, on the local client or the sever this contains the respective object
            player.setAlive(true);
            player.setHasBomb(false);
            GameManager.sendCardPlayed(player.getPlayerId(), this, networkManager);
            player.removeCardFromHand(Integer.toString(DEFUSE_CARD_ID));
            if(NetworkManager.isServer(networkManager)){
                int insertionIDX = deck.addBombAtRandomIndex();
                GameManager.sendDeckInsetCard(networkManager,BOMB_CARD_ID,insertionIDX);
            }
            if(player.getPlayerTurns() == 0 && NetworkManager.isServer(networkManager)){
                GameLogic.finishTurn(player,networkManager,1, turnManager);
            }
        }
        discardPile.putCard(this);
    }

}
