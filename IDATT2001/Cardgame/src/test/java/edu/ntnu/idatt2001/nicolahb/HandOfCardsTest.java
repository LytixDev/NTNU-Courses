package edu.ntnu.idatt2001.nicolahb;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class HandOfCardsTest {

    @Nested
    public class HasFlush {

        @Test
        public void handOfCardHasFlush() {
            ArrayList<PlayingCard> handList = new ArrayList<>();
            for (int i = 1; i < 6; i++)
                handList.add(new PlayingCard('C', i));

            HandOfCards hand = new HandOfCards(handList);
            assertTrue(hand.hasFlush());
        }

        @Test
        public void handOfCardDoesNotHaveFlus() {
            ArrayList<PlayingCard> handList = new ArrayList<>();
            for (int i = 1; i < 5; i++)
                handList.add(new PlayingCard('C', i));

            handList.add(new PlayingCard('S', 1));
            HandOfCards hand = new HandOfCards(handList);
            assertFalse(hand.hasFlush());
        }
    }

    @Nested
    public class CorrectSum {

        @Test
        public void handCalculatesCorrectSum() {
            /* Create five cards all with face 1. 5*1 = 5 */
            int expectedSum = 5;
            ArrayList<PlayingCard> handList = new ArrayList<>();
            for (int i = 1; i < 6; i++)
                handList.add(new PlayingCard('C', 1));

            HandOfCards hand = new HandOfCards(handList);
            assertEquals(expectedSum, hand.sumOfFaces());
        }
    }

    @Nested
    public class HandContainsQueenOfSpades {

        @Test
        public void handContainsQueensOfSpades() {
            ArrayList<PlayingCard> handList = new ArrayList<>();
            handList.add(new PlayingCard('S', 12));

            HandOfCards hand = new HandOfCards(handList);
            assertTrue(hand.hasQueenOfSpaces());
        }

        @Test
        public void handDoesNotContainsQueensOfSpades() {
            ArrayList<PlayingCard> handList = new ArrayList<>();
            handList.add(new PlayingCard('S', 1));

            HandOfCards hand = new HandOfCards(handList);
            assertFalse(hand.hasQueenOfSpaces());
        }
    }

    @Nested
    public class GetHeartsFromHand {

        @Test
        public void getHeartsFromHand() {
            ArrayList<PlayingCard> handList = new ArrayList<>();
            handList.add(new PlayingCard('H', 12));
            handList.add(new PlayingCard('S', 12));

            HandOfCards hand = new HandOfCards(handList);
            assertEquals(1, hand.cardsOfXSuit('H').size());
        }
    }
}
