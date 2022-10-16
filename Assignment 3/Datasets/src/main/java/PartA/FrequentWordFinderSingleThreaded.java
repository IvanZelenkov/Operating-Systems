package PartA;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FrequentWordFinderSingleThreaded {

    public static void main(String[] args) {
        Map<String, Integer> words = new HashMap<>();
        String filename = "SMS_Spam.txt";
        fileReader(filename, words);
    }

    private static void fileReader(String filename, Map<String, Integer> words) {
        Path path = Paths.get("");
        try (Scanner scanner = new Scanner(new FileReader(String.format(path.toAbsolutePath() + "/src/main/resources/PartA/" + filename, filename)))) {
            while (scanner.hasNext()) {
                String word = scanner.next().toLowerCase().replaceAll("[^a-z]", "");
                if (!word.matches("[a-z]{5,}"))
                    continue;

                Integer counter = words.get(word);

                if (counter != null)
                    counter++;
                else
                    counter = 1;

                words.put(word, counter);
            }
//            while (scanner.hasNext()) {
//                String[] tokens = scanner.next().split("[^a-z]");
//                for (String token : tokens) {
//                    String word = token.trim().toLowerCase().replaceAll("[^a-z]", "");
//
//                    if (word.length() < 5)
//                        continue;
//
//                    Integer counter = words.get(word);
//
//                    if (counter != null)
//                        counter++;
//                    else
//                        counter = 1;
//
//                    words.put(word, counter);
//                }
//            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: file not found.");
        }
        getMaxOccurrence(filename, words);
    }

    private static void getMaxOccurrence(String filename, Map<String, Integer> words) {
        int max = 1;
        int sum = 0;
        String maxOccurredWord = "";

        for (Map.Entry<String, Integer> word : words.entrySet())
            if (word.getValue() > max)
                max = word.getValue();

        for (Map.Entry<String, Integer> word : words.entrySet())
            if (word.getValue() == max)
                maxOccurredWord = word.getKey();

        for (Map.Entry<String, Integer> map : words.entrySet())
            if (map.getKey().contains(maxOccurredWord) && maxOccurredWord.length() >= 4)
                sum += map.getValue();

        for (Map.Entry<String, Integer> word : words.entrySet()) {
            if (word.getValue() == max) {
                words.put(word.getKey(), sum);
                System.out.println(filename + ": " + "'" + word.getKey() + "'" + " was found " + word.getValue() + " times.");
            }
        }
    }
}