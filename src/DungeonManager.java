// Programmed by: Jasper Adrada

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Manages assignment of parties to dungeon instances.
 * Reuses dungeon IDs and updates live status accordingly.
 */
public class DungeonManager {
    private int maxDungeons;
    private int minTime, maxTime;
    private Semaphore dungeonSlots;
    private DungeonStatus dungeonStatus;
    private Queue<Integer> availableDungeonIDs = new LinkedList<>();
    private int partyCount = 0;
    private Map<Integer, String> dungeonNames = new HashMap<>();

    /**
     * Constructs a DungeonManager.
     * @param maxDungeons Maximum number of concurrent dungeons.
     * @param minTime Minimum dungeon run time (seconds).
     * @param maxTime Maximum dungeon run time (seconds).
     * @param dungeonStatus Reference to the live status updater.
     */
    public DungeonManager(int maxDungeons, int minTime, int maxTime, DungeonStatus dungeonStatus) {
        this.maxDungeons = maxDungeons;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.dungeonStatus = dungeonStatus;
        this.dungeonSlots = new Semaphore(maxDungeons);

        // Initialize dungeon IDs and assign names using DungeonNameGenerator.
        for (int i = 1; i <= maxDungeons; i++) {
            availableDungeonIDs.add(i);
            String name = DungeonNameGenerator.generateRandomName(3);
            dungeonNames.put(i, name);
            dungeonStatus.setDungeonName(i, name);
            dungeonStatus.updateDungeon(i, false);
        }
    }

    /**
     * Checks if all dungeon slots are free.
     * @return True if all dungeons are available.
     */
    public boolean allDungeonsFree() {
        synchronized (availableDungeonIDs) {
            return availableDungeonIDs.size() == maxDungeons;
        }
    }

    /**
     * Assigns a party to an available dungeon.
     * @param party Array of player strings forming the party.
     */
    public void assignPartyToInstance(String[] party) {
        try {
            dungeonSlots.acquire();
            int dungeonID;
            synchronized (availableDungeonIDs) {
                if (availableDungeonIDs.isEmpty()) {
                    System.out.println("No available dungeons! Waiting...");
                    return;
                }
                dungeonID = availableDungeonIDs.poll();
            }

            // Generate unique party ID and update party inside status.
            int currentPartyID;
            synchronized (this) {
                partyCount++;
                currentPartyID = partyCount;
            }
            dungeonStatus.setPartyInside(dungeonID, "Party " + currentPartyID);

            DungeonInstance dungeon = new DungeonInstance(dungeonID, minTime, maxTime);
            dungeon.start();

            dungeonStatus.updateDungeon(dungeonID, true);
            dungeonStatus.incrementPartiesServed(dungeonID);

            new Thread(() -> {
                try {
                    dungeon.join();
                    dungeonSlots.release();
                    synchronized (availableDungeonIDs) {
                        availableDungeonIDs.add(dungeonID);
                    }
                    int clearTime = dungeon.getClearTime();
                    dungeonStatus.updateDungeon(dungeonID, false);
                    dungeonStatus.addTimeServed(dungeonID, clearTime);
                    dungeonStatus.clearPartyInside(dungeonID);
                    dungeonStatus.addPartyStatus(currentPartyID, dungeonID, clearTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
