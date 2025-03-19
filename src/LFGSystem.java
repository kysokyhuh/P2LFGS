// Programmed by: Jasper Adrada

/**
 * Main class that ties together the dungeon queuing system.
 * It collects user input, starts live status updates, processes matchmaking,
 * displays leftover player counts, and stops live updates when processing is complete.
 */
public class LFGSystem {
    public static void main(String[] args) {
        QueueManager queue = new QueueManager();

        // Collect user input (players and dungeon settings)
        UserInput.collectPlayerData(queue);

        int maxDungeons = UserInput.getNumDungeons();
        int minTime = UserInput.getMinTime();
        int maxTime = UserInput.getMaxTime();

        // Start the live-updating DungeonStatus thread.
        DungeonStatus dungeonStatus = new DungeonStatus();
        dungeonStatus.start();

        DungeonManager dungeonManager = new DungeonManager(maxDungeons, minTime, maxTime, dungeonStatus);

        // Process matchmaking: form parties and assign them to dungeons.
        while (queue.canFormParty()) {
            String[] party = queue.getParty();
            dungeonManager.assignPartyToInstance(party);
        }

        // Print leftover players (only counts)
        System.out.println("\n=== Leftover Players ===");
        System.out.println(queue.getLeftoverPlayers());

        // Wait until all dungeons are free.
        while (!dungeonManager.allDungeonsFree()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Extra wait to ensure final party statuses are updated.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Update leftover player info in DungeonStatus.
        dungeonStatus.setLeftoverInfo(queue.getLeftoverPlayers());

        // Final wait to let the leftover info be seen, then stop live updates.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dungeonStatus.stopUpdating();
    }
}
