// Programmed by: Jasper Adrada

import java.util.Random;

/**
 * Generates random, pronounceable dungeon names using a syllable-based approach.
 */
public class DungeonNameGenerator {

    private static final String[] VOWELS = {"a", "e", "i", "o", "u"};
    private static final String[] CONSONANTS = {"b", "c", "d", "f", "g", "h", "j", "k", "l", "m",
            "n", "p", "r", "s", "t", "v", "w", "z"};

    /**
     * Generates a simple syllable (consonant + vowel).
     * @param random Random instance used for generation.
     * @return A syllable as a String.
     */
    public static String generateSyllable(Random random) {
        String consonant = CONSONANTS[random.nextInt(CONSONANTS.length)];
        String vowel = VOWELS[random.nextInt(VOWELS.length)];
        return consonant + vowel;
    }

    /**
     * Generates a random name composed of the specified number of syllables.
     * @param syllableCount Number of syllables to include.
     * @return A randomly generated dungeon name.
     * @throws IllegalArgumentException if syllableCount is less than 1.
     */
    public static String generateRandomName(int syllableCount) {
        if (syllableCount < 1) {
            throw new IllegalArgumentException("There must be at least one syllable.");
        }
        Random random = new Random();
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < syllableCount; i++) {
            String syllable = generateSyllable(random);
            if (i == 0) {
                syllable = Character.toUpperCase(syllable.charAt(0)) + syllable.substring(1);
            }
            name.append(syllable);
        }
        return name.toString();
    }

    /**
     * Main method for testing the dungeon name generator.
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        String randomName = generateRandomName(3);
        System.out.println("Generated Name: " + randomName);
    }
}
