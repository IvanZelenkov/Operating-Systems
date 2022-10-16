package PartA;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FrequentWordFinderMultiThreaded implements Runnable {

    public static void main(String[] args) {
        Runnable runnable = new FrequentWordFinderMultiThreaded();
        Thread thread0 = new Thread(runnable);
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        Thread thread3 = new Thread(runnable);
        thread0.start();
        thread1.start();
        thread2.start();
        thread3.start();
    }

    @Override
    public void run() {
        String filename = "SMS_Spam.txt";
        Map<String, Integer> words = new HashMap<>();
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
                synchronized (FrequentWordFinderMultiThreaded.class) {
                    Thread.currentThread().interrupt();
                    System.out.println(filename + ": " + "'" + word.getKey() + "'" + " was found " + word.getValue() + " times.");
                    Runtime.getRuntime().halt(0);
                }
            }
        }
    }
}