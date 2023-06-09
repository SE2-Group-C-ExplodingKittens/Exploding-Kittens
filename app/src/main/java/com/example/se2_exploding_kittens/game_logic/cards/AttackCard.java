package com.example.se2_exploding_kittens.game_logic.cards;

import com.example.se2_exploding_kittens.NetworkManager;
import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.TurnManager;
import com.example.se2_exploding_kittens.game_logic.DiscardPile;
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

    @Override
    public void handleActions(Player player, NetworkManager networkManager, DiscardPile discardPile, TurnManager turnManager) {

    }
}
