package PartA;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FrequentWordFinderSingleThreaded {
    public static void main(String[] args) {
        fileReader("SMS_Spam.txt", new HashMap<>());
    }

    private static void fileReader(String filename, Map<String, Integer> words) {
        Path path = Paths.get("");
        String line = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(String.format(path.toAbsolutePath() + "/src/main/resources/PartB/" + filename, filename)))) {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("[^a-zA-Z]");
                for (String token : tokens) {
                    String word = token.trim().toLowerCase().replaceAll("[^a-z]", "");

                    if (word.length() < 5)
                        continue;

                    Integer counter = words.get(word);

                    if (counter != null)
                        counter++;
                    else
                        counter = 1;

                    words.put(word, counter);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: file not found.");
        } catch (IOException e) {
            e.printStackTrace();
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