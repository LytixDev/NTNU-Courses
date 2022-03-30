package edu.ntnu.idatt2001.nicolahb.exceptions;

/**
 * The exception Corrupted Army File.
 * Will be thrown when trying to parse an Army from a CSV file, but the input from the file is
 * not on the proper format.
 * @author Nicolai H. Brand.
 * @version 28.03.2022
 */
public class CorruptedArmyFileException extends Exception {

    /**
     * Instantiates a new Corrupted army file.
     *
     * @param message the message
     */
    public CorruptedArmyFileException(String message) {
        super(message);
    }
}
