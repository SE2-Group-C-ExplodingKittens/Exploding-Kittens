package com.example.se2_exploding_kittens.cards;

import com.example.se2_exploding_kittens.R;

public class AttackCard extends Cards {

    public AttackCard() {

    }
    private static int imageResource = R.drawable.attackcard;
    @Override
    public int getImageResource() {
        return imageResource;
    }

    @Override
    public void cardAction(Player p1, Player p2) {

    }
}
