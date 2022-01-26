package com.company;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Class LongJumpRegister.
 * Holds a collection of one to many long jumps.
 * I have chosen to use an ArrayList to store the long jumps. Using a regular array/table is unfavorable as its size is
 * fixed, and therefore I would need to create a new variable with an increased size when adding new instances of longJump.
 * I have chosen ArrayList because it's not bound by the aforementioned problem. It is also favored over HashMap and
 * HashSet, because those types need a unique identifier field. None of the fields in LongJump provide a guaranteed
 * unique hashable field.
 * @author <censored>
 */
public class LongJumpRegister {
    private ArrayList<LongJump> longJumpRegister;

    /**
     * Constructor for LongJumpRegister.
     * Creates a new instance of LongJumpRegister.
     */
    public LongJumpRegister() {
        longJumpRegister = new ArrayList<>();
    }

    /**
     * Adds a new LongJump to longJumpRegister only if it already doesn't contain it.
     * @param toAdd LongJump.
     * @return boolean, true if successfully added new LongJump, false if else.
     */
    public boolean addNewLongJump(LongJump toAdd) {
        if (longJumpRegister.contains(toAdd))
            return false;
        try {
            longJumpRegister.add(toAdd);
        } catch (IllegalArgumentException e) {
            // e.printStackTrace();
            // here one could do the above line to find out information about where the exception occurs, but
            // this is omitted since it's not valuable for the client
            return false;
        }
        return true;
    }

    /**
     * Returns all the LongJump instances.
     * Since I am using composition I return a deep copy of the register.
     * @return ArrayList<LongJump>
     */
    public ArrayList<LongJump> listAll() {
        return deepCopyRegister(longJumpRegister);
    }

    /**
     * Creates a deep copy of all the elements inside the passed ArrayList<LongJump>
     * The try catch block will be redundant in most cases, as we already know that the LongJump that is being copied
     * has to be valid. However, for sake of being robust its included.
     * @param toCopy ArrayList<LongJump>
     * @return ArrayList<LongJump>
     */
    private ArrayList<LongJump> deepCopyRegister(ArrayList<LongJump> toCopy) {
        ArrayList<LongJump> deepCopy = new ArrayList<>();
        for (LongJump longJump : toCopy) {
            try {
                deepCopy.add(new LongJump(longJump));
            } catch (IllegalArgumentException e) {
                // e.printStackTrace();
                // here one could do the above line to find out information about where the exception occurs, but
                // this is omitted since it's not valuable for the client
                continue;
            }
        }

        return deepCopy;
    }

    /**
     * Finds all the LongJump given the name of an athlete.
     * I use toLowerCase() on both Strings in case the user forgot to capitalize certain letters.
     * Since composition is being used, we take a deep copy of the ArrayList elements.
     * @param athleteName String, name of athlete.
     * @return ArrayList<LongJump>, contains all the long jumps to given athlete.
     */
    public ArrayList<LongJump> findAllForAthlete(String athleteName) {
        ArrayList<LongJump> found = new ArrayList<>();
        for (LongJump longJump : longJumpRegister) {
            if (longJump.getAthleteName().toLowerCase(Locale.ROOT).equals(athleteName.toLowerCase(Locale.ROOT)))
                found.add(longJump);
        }

        return deepCopyRegister(found);
    }

    /**
     * Finds the largest result of a valid long jump.
     * Here I assume the task wants me to return the largest result, and not the entire object containing the largest
     * result.
     * @return double.
     */
    public double findBestResult() {
        // know every measurement is greater than zero. Could also set to DOUBLE_MIN.
        double max = 0;
        for (LongJump longJump : longJumpRegister) {
            if (longJump.getResult() > max && !longJump.getIsFaul())
                max = longJump.getResult();
        }

       return max;
    }

    /**
     * Finds the average result of all valid long jumps.
     * If you want to include the invalid jump, the only difference would be that I would remove the if check and
     * divide the total by longJumpRegister.size() instead.
     * @return double.
     */
    public double findAverageResult() {
        double total = 0;
        double validMeasurements = 0;
        for (LongJump longJump : longJumpRegister) {
            if (!longJump.getIsFaul()) {
                total += longJump.getResult();
                validMeasurements++;
            }
        }

        return total / validMeasurements;
    }

    /**
     * toString method.
     * @return String, representation off all objects as one string.
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (LongJump longJump : longJumpRegister) {
            out.append(longJump.toString()).append("\n");
        }
        return out.toString();
    }
}
