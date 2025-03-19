// Programmed by: Jasper Adrada

import java.util.concurrent.ThreadLocalRandom;

/**
 * Simulates a dungeon instance that runs for a random duration between t1 and t2 seconds.
 */
public class DungeonInstance extends Thread {
    private int dungeonID;  // Unique ID for the dungeon.
    private int t1, t2;     // Minimum and maximum dungeon completion time (in seconds).
    private int clearTime;  // Randomly determined runtime (in seconds).

    /**
     * Constructs a DungeonInstance.
     * @param dungeonID Unique ID for this dungeon.
     * @param t1 Minimum completion time.
     * @param t2 Maximum completion time.
     */
    public DungeonInstance(int dungeonID, int t1, int t2) {
        this.dungeonID = dungeonID;
        this.t1 = t1;
        this.t2 = t2;
    }

    /**
     * Runs the dungeon simulation.
     */
    @Override
    public void run() {
        clearTime = ThreadLocalRandom.current().nextInt(t1, t2 + 1);
        try {
            Thread.sleep(clearTime * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The dungeon ID.
     */
    public int getDungeonID() {
        return dungeonID;
    }

    /**
     * @return The time (in seconds) the dungeon took to complete.
     */
    public int getClearTime() {
        return clearTime;
    }
}
