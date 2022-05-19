package edu.ntnu.idatt2001.nicolahb.gui.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * The class Logger.
 * Used for logging information about attacks inside a battle, but
 * is implemented in a modular fashion and can be used for whatever.
 * Stores the logs inside an ObservableList so GUI components can
 * easily update when a new item is added to the log.
 *
 * @author Nicolai H. Brand.
 * @version 19.05.2022.
 */
public class Logger {
    ObservableList<String> log;
    /* Will add new log items to index 0 of the array if reverse flag is set to true */
    final boolean REVERSE_FLAG;

    /**
     * Constructor for Logger.
     * Sets the reverse flag to false, meaning new log items are added to the end of the list.
     */
    public Logger() {
        log = FXCollections.observableArrayList();
        REVERSE_FLAG = false;
    }

    /**
     * Constructor for Logger.
     *
     * @param reverse boolean, if items are pushed at index 0 or index len.
     */
    public Logger(boolean reverse) {
        log = FXCollections.observableArrayList();
        this.REVERSE_FLAG = reverse;
    }

    /**
     * Add a log item to the list.
     *
     * @param item String, the new item to be added to the log.
     */
    public void addLogItem(String item) {
        if (REVERSE_FLAG)
            log.add(0, item);
        else
            log.add(item);
    }

    /**
     * Clear log.
     * Removes all items inside the log.
     */
    public void clearLog() {
        log.clear();
    }

    // getter
    public ObservableList<String> getLog() {
        return log;
    }
}
