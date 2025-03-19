// Programmed by: Jasper Adrada

import java.util.Scanner;

/**
 * Collects and validates user input for dungeon settings and player counts.
 */
public class UserInput {
    private static final int MAX_TANKS = Integer.MAX_VALUE;
    private static final int MAX_HEALERS = Integer.MAX_VALUE;
    private static final int MAX_DPS = Integer.MAX_VALUE;
    private static final int MAX_DUNGEONS = Integer.MAX_VALUE;
    private static final int MAX_TIME = 15;

    private static int numDungeons;
    private static int minTime;
    private static int maxTime;

    /**
     * Prompts the user for input and adds players to the queue.
     * @param queue The QueueManager instance.
     */
    public static void collectPlayerData(QueueManager queue) {
        Scanner scanner = new Scanner(System.in);

        numDungeons = getValidNumber(scanner, "Enter number of Dungeon Instances: ", MAX_DUNGEONS);
        minTime = getValidNumber(scanner, "Enter minimum dungeon completion time: ", MAX_TIME);
        maxTime = getValidNumber(scanner, "Enter maximum dungeon completion time (<= 15): ", MAX_TIME);

        while (maxTime < minTime) {
            System.out.println("Invalid! Maximum time must be greater than or equal to minimum time.");
            maxTime = getValidNumber(scanner, "Enter maximum dungeon completion time (<= 15): ", MAX_TIME);
        }

        int numTanks = getValidNumber(scanner, "Enter number of Tanks: ", MAX_TANKS);
        int numHealers = getValidNumber(scanner, "Enter number of Healers: ", MAX_HEALERS);
        int numDPS = getValidNumber(scanner, "Enter number of DPS (must be at least 3 for a party): ", MAX_DPS);

        while (numDPS < 3) {
            System.out.println("❌ Invalid! You need at least 3 DPS players for a party.");
            numDPS = getValidNumber(scanner, "Enter number of DPS (must be at least 3 for a party): ", MAX_DPS);
        }

        for (int i = 1; i <= numTanks; i++) {
            queue.addPlayer("Tank", i);
        }
        for (int i = 1; i <= numHealers; i++) {
            queue.addPlayer("Healer", i);
        }
        for (int i = 1; i <= numDPS; i++) {
            queue.addPlayer("DPS", i);
        }

        System.out.println("\n Players and dungeon settings have been added!");
        System.out.println("Dungeons: " + numDungeons);
        System.out.println("Dungeon Completion Time Range: " + minTime + " - " + maxTime + " sec");
        System.out.println("Tanks: " + numTanks);
        System.out.println("Healers: " + numHealers);
        System.out.println("DPS: " + numDPS);

        scanner.close();
    }

    /**
     * Validates and returns an integer from user input.
     * @param scanner The Scanner instance.
     * @param message The prompt message.
     * @param maxLimit The maximum allowed value.
     * @return The validated integer.
     */
    private static int getValidNumber(Scanner scanner, String message, int maxLimit) {
        int number;
        while (true) {
            System.out.print(message);
            if (scanner.hasNextInt()) {
                number = scanner.nextInt();
                if (number >= 0 && number <= maxLimit) {
                    return number;
                } else {
                    System.out.println("Invalid! Number must be between 0 and " + maxLimit);
                }
            } else {
                if (scanner.hasNextDouble()) {
                    System.out.println("Invalid! Decimals are not allowed. Please enter a whole number.");
                } else {
                    System.out.println("Invalid input! Please enter a valid integer.");
                }
                scanner.next(); // Clear invalid input
            }
        }
    }

    /**
     * @return The number of dungeon instances.
     */
    public static int getNumDungeons() {
        return numDungeons;
    }

    /**
     * @return The minimum dungeon completion time.
     */
    public static int getMinTime() {
        return minTime;
    }

    /**
     * @return The maximum dungeon completion time.
     */
    public static int getMaxTime() {
        return maxTime;
    }
}
