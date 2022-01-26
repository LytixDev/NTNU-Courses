package com.company;

import java.time.LocalTime;

/**
 * Class LongJump.
 * This class represents a long jump performed by an athlete.
 * I have made accessor methods for all fields to adhere to the principle of encapsulation.
 * I have no mutator methods because all the fields are final. Therefore, they can't be changed. This is because once
 * a long jump has been performed and the measurements taken, there is nothing that will change about the event.
 * @author <censored>
 */
public class LongJump {
    // since the task says the start number CAN be a surname, this field has to be a String
    private final String startNr;
    private final String athleteName;
    private final double result;
    private final boolean isFaul;
    private final LocalTime time;

    /**
     * Constructor for the class.
     * Creates a new instance of the class.
     * @param startNr String, has to be a String since the task says it can be an athletes surname. Can't be empty.
     * @param athleteName String, the name of the athlete in the format: 'firstname surname'. Can't be empty.
     * @param result double, the measurement of the length of the long jump. Can't be a negative number or zero.
     *               This field is of type double as it gives more precision than a float. Generally you would want the
     *               maximum precision possible in sporting events for measurements. For this reason, double is chosen
     *               instead of float.
     * @param isFaul boolean, true if the result is a foul, else false.
     * @param time LocalTime, the time of when the long jump was performed.
     * @throws IllegalArgumentException if startNr or athleteName are blank (empty). If result is not greater than zero.
     *                                  If LocalTime is null.
     */
    public LongJump(String startNr, String athleteName, double result, boolean isFaul, LocalTime time) throws
            IllegalArgumentException {
        if (startNr.isBlank())
            throw new IllegalArgumentException("Start number can't be empty.");
        if (athleteName.isBlank())
            throw new IllegalArgumentException("Athlete name can't be blank.");
        if (result <= 0)
            throw new IllegalArgumentException("Result has to be greater than zero.");
        if (time == null)
            throw new IllegalArgumentException("Time has to be set.");
        this.startNr = startNr;
        this.athleteName = athleteName;
        this.result = result;
        this.isFaul = isFaul;
        this.time = time;
    }

    /**
     * Second constructor for the class.
     * Takes in an already existing instance of LongJump and makes a deep copy of it.
     * Checking for valid input inside the LongJump is omitted, because if the instance of LongJump exists, we know
     * the input must be valid.
     * @param toCopy LongJump.
     * @throws IllegalArgumentException if LongJump is null.
     */
    public LongJump(LongJump toCopy) throws IllegalArgumentException {
        if (toCopy == null)
            throw new IllegalArgumentException("Can't make a copy of null instance of LongJump");
        this.startNr = toCopy.getStartNr();
        this.athleteName = toCopy.getAthleteName();
        this.result = toCopy.getResult();
        this.isFaul = toCopy.getIsFaul();
        this.time = toCopy.getTime();
    }

    /**
     * Gets startNr.
     * @return String.
     */
    public String getStartNr() {
        return startNr;
    }

    /**
     * Gets athleteName.
     * @return String.
     */
    public String getAthleteName() {
        return athleteName;
    }

    /**
     * Gets result.
     * @return double.
     */
    public double getResult() {
        return result;
    }

    /**
     * Gets isFaul.
     * @return boolean.
     */
    public boolean getIsFaul() {
        return isFaul;
    }

    /**
     * Gets time.
     * @return LocalTime.
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Equals method.
     * This method checks for equality between two objects of the LongJump class.
     * I have decided that startNr, athleteName and time make up the uniqueness of an instance of LongJump.
     * Strictly speaking you could only use time, as during an event two athletes do not jump at the same time, however
     * this might result in problems in the case that the time is the same. Therefore, I have chosen three fields to
     * guarantee uniqueness.
     * @param o Object, should be of type LongJump.
     * @return boolean, true if equality, false if not.
     */
    @Override
    public boolean equals(Object o) {
        // check for reference
        if (this == o) return true;
        // check for null and/or class type
        if (o == null || getClass() != o.getClass()) return false;
        LongJump that = (LongJump) o;
        // check for startNr, athleteName and time.
        return this.startNr.equals(that.getStartNr()) && this.athleteName.equals(that.getAthleteName()) &&
                this.time.equals(that.getTime());
    }

    /**
     * toString method.
     * @return String, representation of the object as a String literal.
     */
    @Override
    public String toString() {
        return startNr + " - " + athleteName + " - " + result + " - " + isFaul + " - " + time + "\n";
    }
}
