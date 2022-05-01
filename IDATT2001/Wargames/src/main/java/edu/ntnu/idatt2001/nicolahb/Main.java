package edu.ntnu.idatt2001.nicolahb;
import edu.ntnu.idatt2001.nicolahb.client.App;
import edu.ntnu.idatt2001.nicolahb.filehandling.CSVFileHandler;
import edu.ntnu.idatt2001.nicolahb.units.CommanderUnit;
import edu.ntnu.idatt2001.nicolahb.units.RangedUnit;
import edu.ntnu.idatt2001.nicolahb.units.Unit;


/**
 * Class Main
 * Runs the program and is called on by default when launching the program.
 * @author Nicolai Brand.
 * @version 20.04.2022
 */
public class Main {

    public static void main(String[] args) {
        App.main();
        //testSim();
    }

    public static void testSim() {
        Army army1 = new Army("Chads");
        Army army2 = new Army("Not chads");

        Unit unit1 = new CommanderUnit("Turbo Chad", 100);
        Unit unit2 = new CommanderUnit("Not turbo Chad", 100);
        army1.addUnit(unit1);
        army2.addUnit(unit2);

        for (int i = 0; i < 50; i++) {
            Unit unit_m = new RangedUnit("Mini chad", 10);
            Unit unit_n = new RangedUnit("Mini not chad", 10);
            army1.addUnit(unit_m);
            army2.addUnit(unit_n);
        }

        Battle battle = new Battle(army1, army2,TerrainType.NEUTRAL);

        try {
            Army winner = battle.simulateStep();
            while (winner == null) {
                Thread.sleep(100);
                System.out.println(army1.getUnits().size() + "-" + army2.getUnits().size());
                winner = battle.simulateStep();
            }
            System.out.println(winner);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
