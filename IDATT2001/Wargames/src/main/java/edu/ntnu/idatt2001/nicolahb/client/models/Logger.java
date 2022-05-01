package edu.ntnu.idatt2001.nicolahb.client.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * The class Logger.
 * Used for logging information about attacks inside a battle, but
 * is implemented in a modular fashion and can be used for whatever.
 * Stores the logs inside an ObservableList so GUI components can
 * easily update when a new item is added to the log.
 *
 * @author Nicolai H. Brand
 * @version 01.05.2022
 */
public class Logger {
    ObservableList<String> log;
    boolean reverse = false;

    /**
     * Constructor for Logger
     */
    public Logger() {
        log = FXCollections.observableArrayList();
    }

    /**
     * Constructor for Logger.
     *
     * @param reverse boolean, if items are pushed at index 0 or index len
     */
    public Logger(boolean reverse) {
        log = FXCollections.observableArrayList();
        this.reverse = reverse;
    }

    /**
     * Add a log item
     *
     * @param item String, the new item to be added to the log
     */
    public void addLogItem(String item) {
        if (reverse)
            log.add(0, item);
        else
            log.add(item);
    }

    /**
     * Clear log.
     * Removes all items inside the the log.
     */
    public void clearLog() {
        log.clear();
    }

    // getter
    public ObservableList<String> getLog() {
        return log;
    }
}
