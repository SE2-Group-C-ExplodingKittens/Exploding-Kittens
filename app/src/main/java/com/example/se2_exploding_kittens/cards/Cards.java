package com.example.se2_exploding_kittens.cards;

public abstract class Cards {
    int id;
    boolean bomb;
    String cardPath;
    public static String cardName;

    public int getId() {
        return id;
    }

    public boolean isBomb() {
        return bomb;
    }

    public void setBomb(boolean bomb) {
        this.bomb = bomb;
    }

    public abstract void cardAction(Player p1, Player p2);

    public static String getCardName() {
        return cardName;
    }

    protected int imageResource;

    public Cards() {

    }

    public abstract int getImageResource();
}


