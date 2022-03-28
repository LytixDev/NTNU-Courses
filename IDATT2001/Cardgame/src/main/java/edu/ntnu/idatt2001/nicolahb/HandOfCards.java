package edu.ntnu.idatt2001.nicolahb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * The type Hand of cards.
 * @author Nicolai H. Brand.
 */
public class HandOfCards {

    private ArrayList<PlayingCard> hand;

    /**
     * Instantiates a new Hand of cards.
     *
     * @param hand the hand
     */
    public HandOfCards(ArrayList<PlayingCard> hand) {
        this.hand = hand;
    }

    /**
     * Has flush boolean.
     *
     * @return the boolean
     */
    public boolean hasFlush() {
        /* this implementation assumes a hand consists of five cards */
        return hand.stream().map(PlayingCard::getSuit).distinct().count() == 1;
    }

    /**
     * Sum of faces int.
     *
     * @return the int
     */
    public int sumOfFaces() {
        return hand.stream().map(c -> c.getFace()).reduce(0, (a, b) -> a + b);
    }

    /**
     * Has queen of spaces boolean.
     *
     * @return the boolean
     */
    public boolean hasQueenOfSpaces() {
        return hand.contains(new PlayingCard('S', 12));
    }

    /**
     * Cards of x suit array list.
     *
     * @param x the x
     * @return the array list
     */
    public ArrayList<PlayingCard> cardsOfXSuit(char x) {
        return hand.stream()
                .filter(c -> c.getSuit() == x)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (PlayingCard card : hand) {
            str.append(card.getAsString()).append(" ");
        }

        return str.toString();
    }
}
