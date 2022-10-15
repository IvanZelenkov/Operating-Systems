package PartA;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

public class FrequentWordFinderSingleThreaded {

    private static Map<String, Integer> words = new HashMap<>();

    public static void main(String[] args) {
        String filename = "SMS_Spam.txt";
        fileReader(filename);
        int max = getMaxOccurrence(words);

        String maxOccurredWord = "";
        for (Map.Entry<String, Integer> word : words.entrySet()) {
            if (word.getValue() == max) {
                maxOccurredWord = word.getKey();
                System.out.println(filename + ": " + word.getKey() + word.getValue());
            }
        }


        for (Map.Entry<String, Integer> map : words.entrySet()) {
            if (map.getKey().contains(maxOccurredWord)) {
                System.out.println(map.getKey());
            }
        }
    }

    private static void fileReader(String filename) {
        Path path = Paths.get("");
        int c = 1;
        try (Scanner scanner = new Scanner(new FileReader(String.format(path.toAbsolutePath() + "/src/main/resources/PartA/" + filename, filename)))) {
            while (scanner.hasNext()) {
//                String word2 = scanner.next().toLowerCase();
//                if (word2.contains("there")) {
//                    word2 = word2.split("[^a-zA-Z]", 2)[0].replaceAll("[^a-zA-Z]", "");
//                    if (word2.matches("[a-z]{5,}")) {
//                        System.out.println("Exact: " + c++ + ": " + word2);
//                    }
//                }

                String word = scanner.next().replaceAll("[^a-zA-Z]", "").toLowerCase();
                if (word.length() < 5)
                    continue;

                Integer counter = words.get(word);

                if (counter != null)
                    counter++;
                else
                    counter = 1;

                words.put(word, counter);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: file not found.");
        }
    }

    private static int getMaxOccurrence(Map<String, Integer> words) {
        int max = 1;

        for (Map.Entry<String, Integer> word : words.entrySet())
            if (word.getValue() > max)
                max = word.getValue();

        return max;
    }
}