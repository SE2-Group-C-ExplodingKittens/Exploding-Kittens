package com.example.se2_exploding_kittens.cards;

public class CardFactory {

    public Cards getCard(String cardType) {
        if (cardType == null) {
            return null;
        } else {
            if (cardType.equalsIgnoreCase("ATTACKCARD")) {
                return new AttackCard();
            } else if (cardType.equalsIgnoreCase("CATCARD")) {
                return new CatCard();
            } else if (cardType.equalsIgnoreCase("FUNNYPINGO")) {
                return new FunnyPingoCard();
            } else if (cardType.equalsIgnoreCase("DEFUSECARD")) {
                return new DefuseCard();
            } else if (cardType.equalsIgnoreCase("FAVORCARD")) {
                return new FavorCard();
            } else if (cardType.equalsIgnoreCase("NOPECARD")) {
                return new NopeCard();
            } else if (cardType.equalsIgnoreCase("SHUFFLECARD")) {
                return new ShuffleCard();
            } else if (cardType.equalsIgnoreCase("SKIPCARD")) {
                return new SkipCard();
            }else if (cardType.equalsIgnoreCase("SEETHEFUTURECARD")) {
                return new SeeTheFutureCard();
            } else if (cardType.equalsIgnoreCase("BOMBCARD")) {
                return new BombCard();
            }
        }
        return null;
    }
}

