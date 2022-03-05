package edu.ntnu.idatt2001.nicolahb;
import edu.ntnu.idatt2001.nicolahb.units.*;

import java.util.ArrayList;

/**
 * Class Main
 * Runs a battle simulation.
 * Only for testing purposes.
 * @author Nicolai Brand.
 */
public class Main {
    static ArrayList<Unit> humanUnits = new ArrayList<>();
    static ArrayList<Unit> orcishUnits = new ArrayList<>();
    static Battle battle;

    public static void main(String[] args) {
        /* Fill the armies with units and create a battle instance */
        try {
            fillArmies();
            Army humanArmy = new Army("Human army", humanUnits);
            Army orcishArmy = new Army("Orcish horde", orcishUnits);
            battle = new Battle(humanArmy, orcishArmy);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        /* Run the battle simulation */
        try {
            Army winner = battle.simulate();
            System.out.println("Winning army is " + winner.getName());
        } catch (UnsupportedOperationException e) {
            System.out.println("Battle has already been played out");
        }
    }

    public static void fillArmies() {
        int i;
        for (i = 0; i < 500; i++) {
            humanUnits.add(new InfantryUnit("Footman", 100));
            orcishUnits.add(new InfantryUnit("Grunt", 100));
        }

        for (i = 0; i < 100; i++) {
            humanUnits.add(new CavalryUnit("Knight", 100));
            orcishUnits.add(new CavalryUnit("Raider", 100));
        }

        for (i = 0; i < 200; i++) {
            humanUnits.add(new RangedUnit("Archer", 100));
            orcishUnits.add(new RangedUnit("Spearman", 100));
        }

        humanUnits.add(new CommanderUnit("Mountain King", 180));
        orcishUnits.add(new CommanderUnit("Gul'dan", 180));
    }
}
