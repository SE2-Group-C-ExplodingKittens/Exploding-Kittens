package com.example.se2_exploding_kittens.cards;

public class CatCard extends Cards {
    int imageResource;
    @Override
    public int getImageResource() {
        return imageResource;

    }
    public CatCard(int imageResource) {
        super(imageResource);
        this.imageResource = imageResource;
    }

    @Override
    public void cardAction(Player p1, Player p2) {

    }
}
