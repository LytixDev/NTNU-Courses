package edu.ntnu.idatt2001.nicolahb;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeckOfCardsTest {

    @Nested
    public class DeckOfCardsConstructorTest {

        @Test
        public void afterInitializingDeckHas52Cards() {
            DeckOfCards deck = new DeckOfCards();
            assertEquals(deck.getCards().size(), 52);
        }

        @Test
        public void dealingHandAlsoRemovesCardsFromDeck() {
            DeckOfCards deck = new DeckOfCards();
            deck.dealHand(2);
            assertNotEquals(deck.getCards().size(), 52);
            assertEquals(deck.getCards().size(), 50);
        }
    }

}
