package com.example.se2_exploding_kittens.cards;

public class SkipCard extends Cards {
    public SkipCard(int imageResource) {
        super(imageResource);
        this.imageResource = imageResource;
    }

    int imageResource;
    @Override
    public int getImageResource() {
        return imageResource;
    }



    @Override
    public void cardAction(Player p1, Player p2) {

    }
}