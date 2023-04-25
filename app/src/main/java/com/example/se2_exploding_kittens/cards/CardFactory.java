package com.example.se2_exploding_kittens.cards;

public class CardFactory {
    public Cards getCard(String cardType){
        if (cardType == null) {
            return null;
        }
        if (cardType.equalsIgnoreCase("ATTACKCARD")) {
            return new AttackCard();
        } else if (cardType.equalsIgnoreCase("CATCARD")) {
            return new BombCard();
        } else if (cardType.equalsIgnoreCase("DEFUSECARD")) {
            return new BombCard();
        }else if (cardType.equalsIgnoreCase("FAVORCARD")) {
            return new BombCard();
        }else if (cardType.equalsIgnoreCase("NOPECARD")) {
            return new BombCard();
        }else if (cardType.equalsIgnoreCase("SHUFFLECARD")) {
            return new BombCard();
        }else if (cardType.equalsIgnoreCase("SKIPCARD")) {
            return new BombCard();
        }else if (cardType.equalsIgnoreCase("BOMBCARD")) {
            return new BombCard();
        } return null;
    }
}
