// Programmed by: Jasper Adrada

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Continuously updates and displays the live status of dungeons and parties.
 * Also logs finished party statuses to a text file.
 */
public class DungeonStatus extends Thread {
    private Map<Integer, String> dungeonState = new ConcurrentHashMap<>();
    private Map<Integer, Integer> partiesServed = new ConcurrentHashMap<>();
    private Map<Integer, Integer> totalTimeServed = new ConcurrentHashMap<>();
    private Map<Integer, String> partyInside = new ConcurrentHashMap<>();
    private List<String> partyStatusList = new ArrayList<>();
    private Map<Integer, String> dungeonNames = new ConcurrentHashMap<>();
    private volatile boolean running = true;
    private String logFileName;
    private String leftoverInfo = "";

    // Define a fixed width for the header display.
    private final int WIDTH = 120;

    /**
     * Constructs a DungeonStatus instance and initializes the log file name.
     */
    public DungeonStatus() {
        logFileName = "party_status_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".txt";
    }

    /**
     * Updates the status (Active/Empty) of a dungeon.
     * @param dungeonID The dungeon identifier.
     * @param isActive True if the dungeon is active; false if empty.
     */
    public synchronized void updateDungeon(int dungeonID, boolean isActive) {
        dungeonState.put(dungeonID, isActive ? "Active" : "Empty");
        if (!isActive) {
            clearPartyInside(dungeonID);
        }
        partiesServed.putIfAbsent(dungeonID, 0);
        totalTimeServed.putIfAbsent(dungeonID, 0);
    }

    /**
     * Sets the name for a given dungeon.
     * @param dungeonID The dungeon identifier.
     * @param name The name to assign.
     */
    public synchronized void setDungeonName(int dungeonID, String name) {
        dungeonNames.put(dungeonID, name);
    }

    /**
     * Increments the number of parties served for a dungeon.
     * @param dungeonID The dungeon identifier.
     */
    public synchronized void incrementPartiesServed(int dungeonID) {
        partiesServed.put(dungeonID, partiesServed.getOrDefault(dungeonID, 0) + 1);
    }

    /**
     * Adds a given time (in seconds) to the total time served for a dungeon.
     * @param dungeonID The dungeon identifier.
     * @param time The time to add.
     */
    public synchronized void addTimeServed(int dungeonID, int time) {
        totalTimeServed.put(dungeonID, totalTimeServed.getOrDefault(dungeonID, 0) + time);
    }

    /**
     * Sets the party (e.g., "Party 1") currently inside a dungeon.
     * @param dungeonID The dungeon identifier.
     * @param partyId The party identifier.
     */
    public synchronized void setPartyInside(int dungeonID, String partyId) {
        partyInside.put(dungeonID, partyId);
    }

    /**
     * Clears the party currently inside a dungeon.
     * @param dungeonID The dungeon identifier.
     */
    public synchronized void clearPartyInside(int dungeonID) {
        partyInside.remove(dungeonID);
    }

    /**
     * Records a finished party's status (keeping only the 5 most recent) and logs it.
     * @param partyID The unique party identifier.
     * @param dungeonID The dungeon identifier.
     * @param time The time (in seconds) the party took.
     */
    public synchronized void addPartyStatus(int partyID, int dungeonID, int time) {
        String name = dungeonNames.getOrDefault(dungeonID, "Unknown");
        String status = String.format("[Party ID: %d] - Finished at Dungeon [%s (ID: %d)]: Time [%d seconds]",
                partyID, name, dungeonID, time);
        if (partyStatusList.size() >= 5) {
            partyStatusList.remove(0);
        }
        partyStatusList.add(status);
        logPartyStatus(status);
    }

    /**
     * Appends a party status entry to the log file.
     * @param status The status message to log.
     */
    private void logPartyStatus(String status) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
            writer.write(status);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the leftover player information to be displayed.
     * @param info A formatted string showing leftover player counts.
     */
    public synchronized void setLeftoverInfo(String info) {
        leftoverInfo = info;
    }

    /**
     * Stops the live update loop.
     */
    public void stopUpdating() {
        running = false;
    }

    /**
     * Continuously refreshes and prints the live status table.
     */
    @Override
    public void run() {
        while (running) {
            clearConsole();
            printStatusTable();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Clears the console using ANSI escape codes.
     */
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.println();
    }

    /**
     * Helper method to center a string within a specified width.
     * @param width The total width.
     * @param s The string to center.
     * @return The centered string.
     */
    private String centerString(int width, String s) {
        if (s.length() >= width) return s;
        int leftPadding = (width - s.length()) / 2;
        return " ".repeat(leftPadding) + s;
    }

    /**
     * Prints the live status table including Dungeon Status, Party Status, and Leftover Players.
     */
    private synchronized void printStatusTable() {
        String line = "=".repeat(WIDTH);
        // Print centered header for Dungeon Status
        System.out.println(line);
        System.out.println(centerString(WIDTH, "DUNGEON STATUS"));
        System.out.println(line);

        // Print table headers
        String header = String.format("%-20s | %-10s | %-15s | %-20s | %-20s | %-15s",
                "Dungeon Name", "Dungeon ID", "Party Served", "Total Time Served", "Party Inside", "Status");
        System.out.println(header);
        System.out.println(line);

        if (dungeonState.isEmpty()) {
            System.out.println("No Dungeon Data Available");
        } else {
            for (Integer dungeonID : dungeonState.keySet()) {
                String name = dungeonNames.getOrDefault(dungeonID, "Unknown");
                int served = partiesServed.getOrDefault(dungeonID, 0);
                int totalTime = totalTimeServed.getOrDefault(dungeonID, 0);
                String party = partyInside.getOrDefault(dungeonID, "None");
                String status = dungeonState.getOrDefault(dungeonID, "Unknown");
                String row = String.format("%-20s | %-10d | %-15d | %-20d | %-20s | %-15s",
                        name, dungeonID, served, totalTime, party, status);
                System.out.println(row);
            }
        }
        System.out.println(line);
        System.out.println();

        // Print centered header for Party Status
        System.out.println(line);
        System.out.println(centerString(WIDTH, "PARTY STATUS (Recent 5)"));
        System.out.println(line);
        if (partyStatusList.isEmpty()) {
            System.out.println("No Party Data Available");
        } else {
            for (String ps : partyStatusList) {
                System.out.println(ps);
            }
        }
        System.out.println(line);
        System.out.println();

        // Print centered header for Leftover Players
        System.out.println(line);
        System.out.println(centerString(WIDTH, "LEFTOVER PLAYERS"));
        System.out.println(line);
        System.out.println(leftoverInfo);
        System.out.println(line);
    }
}
