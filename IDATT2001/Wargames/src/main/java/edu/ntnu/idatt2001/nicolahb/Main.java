package edu.ntnu.idatt2001.nicolahb;
import edu.ntnu.idatt2001.nicolahb.units.*;

import java.util.ArrayList;

/**
 * Class Main
 * Simulates a battle.
 * @author Nicolai Brand.
 */
public class Main {
    static ArrayList<Unit> humanUnits = new ArrayList<>();
    static ArrayList<Unit> orcishUnits = new ArrayList<>();

    public static void main(String[] args) {
        fillArmies();
        Army humanArmy = new Army("Human army", humanUnits);
        Army orcishArmy = new Army("Orcish horde", orcishUnits);

        Battle battle = new Battle(humanArmy, orcishArmy);
        System.out.println(battle.simulate());
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
