package PartB;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ivan Zelenkov
 * SingleThreadedCollector class read files in PartB folder,
 * count occurred words, and return an output that will contain the filename,
 * the most occurred word in that file, and the number of times that word was repeated.
 */
public class SingleThreadedCollector {

    public static void main(String[] args) {
        double startTime = System.currentTimeMillis();
        System.out.println("Reading in progress...\n");

        for (String filename : fileFinder())
            fileReader(filename, new HashMap<>());

        double endTime = System.currentTimeMillis();
        double executionTotalTime = (endTime - startTime) / 1000;
        System.out.println("\nExecution time: " + executionTotalTime + " seconds");
    }

    /**
     * The method searches for the files in a specified directory and stores filenames in a list.
     */
    private static List<String> fileFinder() {
        Path path = Paths.get("");
        try {
            Stream<Path> files = Files.list(Paths.get(path.toAbsolutePath() + "/src/main/resources/PartB/"));

            return files
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * The method reads the file line by line, splits each line into words, and then collects each word.
     * @param filename The current file that the program is processing.
     * @param words The map will contain all occurring words from the file.
     */
    private static void fileReader(String filename, Map<String, Integer> words) {
        Path path = Paths.get("");
        String line;
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
            System.out.println("Error: operating system can't complete the appropriate Input/Output.");
        }
        getMostOccurredWord(filename, words);
    }

    /**
     * The method iterates over the map several times to determine the most frequently occurring word in the file.
     * @param filename The current file that the program is processing.
     * @param words The resulting map containing all collected occurring words from the file.
     */
    private static void getMostOccurredWord(String filename, Map<String, Integer> words) {
        int max = 1;
        int sum = 0;
        String maxOccurredWord = "";

        for (Map.Entry<String, Integer> word : words.entrySet()) {
            if (word.getValue() > max) {
                max = word.getValue();
                maxOccurredWord = word.getKey();
            }
        }

        // Look for the words that contain already found most occurred word.
        // For example, "bthere" and "buttheres" mean that the use of the word "there"
        // was actually meant, and therefore I count it as an occurrence.
        for (Map.Entry<String, Integer> map : words.entrySet())
            if (map.getKey().contains(maxOccurredWord) && maxOccurredWord.length() > 4)
                sum += map.getValue();

        for (Map.Entry<String, Integer> word : words.entrySet()) {
            if (word.getValue() == max) {
                words.put(word.getKey(), sum);
                System.out.println(filename + ": " + "'" + word.getKey() + "'" + " was found " + word.getValue() + " times.");
            }
        }
    }
}