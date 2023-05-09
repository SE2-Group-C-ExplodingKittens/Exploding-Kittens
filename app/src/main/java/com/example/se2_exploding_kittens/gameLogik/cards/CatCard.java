package com.example.se2_exploding_kittens.gameLogik.cards;

import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.gameLogik.Player;

public class CatCard extends Cards {
    private static int imageResource = R.drawable.uglykittencard;
    private static String cardName = "CatCard";
    @Override
    public int getImageResource() {
        return imageResource;
    }

    public static String getCardName() {
        return cardName;
    }
    public CatCard() {

    }

    @Override
    public void cardAction(Player p1, Player p2) {

    }
}
