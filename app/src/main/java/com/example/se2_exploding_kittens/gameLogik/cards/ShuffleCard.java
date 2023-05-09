package com.example.se2_exploding_kittens.gameLogik.cards;

import com.example.se2_exploding_kittens.R;
import com.example.se2_exploding_kittens.gameLogik.Player;

public class ShuffleCard extends Cards {
    public ShuffleCard() {

    }
    private static int imageResource = R.drawable.shufflecard;
    private static String cardName = "ShuffleCard";
    @Override
    public int getImageResource() {

        return imageResource;
    }
    public static String getCardName() {
        return cardName;
    }


    @Override
    public void cardAction(Player p1, Player p2) {

    }
}
