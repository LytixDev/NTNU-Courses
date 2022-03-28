package edu.ntnu.idatt2001.nicolahb;

import java.util.*;

/**
 * Class DeckOfCards.
 * Holds a standard 52 deck of cards in a HashMap where the key is
 * the string on the form "S2" where the first element is the suit
 * and the last is the face.
 *
 * @author Nicolai H. Brand.
 */
public class DeckOfCards {
    private static final int TOTAL_FACES = 13;
    private static final char[] suit = { 'S', 'H', 'D', 'C' };
    private HashMap<String, PlayingCard> cards = new HashMap<>();
    private Random random;

    /**
     * Instantiates a new Deck of cards.
     */
    public DeckOfCards() {
        random = new Random();

        for (int i = 1; i <= TOTAL_FACES; i++) {
            cards.put(String.format("%c%d", suit[0], i), new PlayingCard(suit[0], i));
            cards.put(String.format("%c%d", suit[1], i), new PlayingCard(suit[1], i));
            cards.put(String.format("%c%d", suit[2], i), new PlayingCard(suit[2], i));
            cards.put(String.format("%c%d", suit[3], i), new PlayingCard(suit[3], i));
        }
    }

    /**
     * Returns n amount of unique cards from a deck of cards.
     *
     * @param n int, amount of cards to deal
     * @return the array list
     * @throws IllegalStateException if n is greater than the size of the deck of cards
     */
    public ArrayList<PlayingCard> dealHand(int n) throws IllegalStateException {
        if (n > cards.size())
            throw new IllegalStateException("Not enough cards");

        ArrayList<PlayingCard> dealtHand = new ArrayList<>();
        List<PlayingCard> valuesList = new ArrayList<>(cards.values());
        Collections.shuffle(valuesList, random);

        for (int i = 0; i < n; i++) {
            PlayingCard playingCard = valuesList.get(i);
            dealtHand.add(playingCard);
            cards.remove(playingCard.getAsString());
        }

        return dealtHand;
    }

    /**
     * Gets cards.
     *
     * @return the cards
     */
    public HashMap<String, PlayingCard> getCards() {
        return cards;
    }
}
