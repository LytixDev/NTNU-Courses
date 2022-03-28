package edu.ntnu.idatt2001.nicolahb;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * The type Main controller.
 * @author Nicolai H. Brand.
 */
public class MainController {

    /**
     * The Hand.
     */
    HandOfCards hand;

    @FXML
    private Text currentHand;

    @FXML
    private Text hasFlushField;

    @FXML
    private Text hasHeartsField;

    @FXML
    private Text hasQueenField;

    @FXML
    private Text hasSumField;

    /**
     * Check hand.
     *
     * @param event the event
     */
    @FXML
    void checkHand(ActionEvent event) {
        if (hand.hasFlush())
            hasFlushField.setText("True");
        else
            hasFlushField.setText("False");

        if (hand.hasQueenOfSpaces())
            hasQueenField.setText("True");
        else
            hasQueenField.setText("False");

        hasSumField.setText(String.format("%d", hand.sumOfFaces()));

        ArrayList<PlayingCard> hearts = hand.cardsOfXSuit('H');

        StringBuilder str = new StringBuilder("[");
        hearts.stream().forEach(x -> str.append(x.getAsString()).append(","));
        /* delete trailing ',' */
        try {
            str.delete(str.lastIndexOf(","), str.lastIndexOf(",") + 1);
        } catch (Exception e) {
            // begone
        }
        str.append("]");
        hasHeartsField.setText(str.toString());
    }

    /**
     * Refresh info.
     */
    void refreshInfo() {
        hasFlushField.setText("None");
        hasHeartsField.setText("None");
        hasQueenField.setText("None");
        hasSumField.setText("None");
    }

    /**
     * Gets new hand.
     *
     * @param event the event
     */
    @FXML
    void getNewHand(ActionEvent event) {
        DeckOfCards deck = new DeckOfCards();
        HandOfCards hand = new HandOfCards(deck.dealHand(5));
        this.hand = hand;

        currentHand.setText(hand.toString());
        /* set the info to "None" as previous info is outdated */
        refreshInfo();
    }
}