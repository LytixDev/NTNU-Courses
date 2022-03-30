package edu.ntnu.idatt2001.nicolahb.exceptions;

/**
 * Simulation already ran exception.
 * Specifically made for the method Battle.simulate().
 * Will be thrown if said simulate() method is called more than once on the same instance of Battle.
 * @author Nicolai H. Brand.
 * @version 28.03.2022
 */
public class SimulationAlreadyRanException extends Exception {

    /**
     * Instantiates a new Simulation already ran exception.
     *
     * @param message the message to be displayed.
     */
    public SimulationAlreadyRanException(String message) {
        super(message);
    }
}
