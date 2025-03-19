// Programmed by: Jasper Adrada

import java.util.LinkedList;
import java.util.Queue;

/**
 * Manages player queues for Tanks, Healers, and DPS.
 * Forms a party when there is at least 1 Tank, 1 Healer, and 3 DPS.
 */
public class QueueManager {
    private Queue<String> tanks = new LinkedList<>();
    private Queue<String> healers = new LinkedList<>();
    private Queue<String> dps = new LinkedList<>();

    /**
     * Adds a player to the corresponding role queue.
     * @param role Player role ("Tank", "Healer", "DPS").
     * @param id Player identifier.
     */
    public void addPlayer(String role, int id) {
        if (role.equals("Tank")) {
            tanks.add("Tank-" + id);
        } else if (role.equals("Healer")) {
            healers.add("Healer-" + id);
        } else if (role.equals("DPS")) {
            dps.add("DPS-" + id);
        }
    }

    /**
     * Checks if a complete party can be formed.
     * @return True if at least 1 Tank, 1 Healer, and 3 DPS are available.
     */
    public synchronized boolean canFormParty() {
        return tanks.size() >= 1 && healers.size() >= 1 && dps.size() >= 3;
    }

    /**
     * Forms a party from available players.
     * @return An array of player strings forming the party, or null if not enough players.
     */
    public synchronized String[] getParty() {
        if (!canFormParty()) return null;
        return new String[]{
                tanks.poll(), healers.poll(), dps.poll(), dps.poll(), dps.poll()
        };
    }

    /**
     * Returns a formatted string with counts of leftover players.
     * @return A string listing the number of leftover Tanks, Healers, and DPS.
     */
    public synchronized String getLeftoverPlayers() {
        StringBuilder sb = new StringBuilder();
        sb.append("Leftover Tanks: ").append(tanks.size()).append("\n");
        sb.append("Leftover Healers: ").append(healers.size()).append("\n");
        sb.append("Leftover DPS: ").append(dps.size()).append("\n");
        return sb.toString();
    }
}
