package PartB;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FrequentWordFinderSingleThreaded {

    private static Map<String, Integer> words = new HashMap<>();

    public static void main(String[] args) {
        fileFinder();
    }

    private static void fileFinder() {
        Path path = Paths.get("");
        try {
            Stream<Path> files = Files.list(Paths.get(path.toAbsolutePath() + "/src/main/resources/PartB/"));

            Set<String> set = files
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());

            for (String filename : set)
                fileReader(filename);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fileReader(String filename) {
        Path path = Paths.get("");
        try (Scanner scanner = new Scanner(new FileReader(String.format(path.toAbsolutePath() + "/src/main/resources/PartB/" + filename, filename)))) {
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (word.length() < 5 || !word.matches("[a-zA-Z]+"))
                    continue;

                Integer counter = words.get(word);

                if (counter != null)
                    counter++;
                else
                    counter = 1;

                words.put(word, counter);
            }
            getMaxOccurrence(filename, words);
        } catch (FileNotFoundException e) {
            System.out.println("Error: file not found.");
        }
    }

    private static void getMaxOccurrence(String filename, Map<String, Integer> words) {
        int max = 1;

        for (Map.Entry<String, Integer> word : words.entrySet())
            if (word.getValue() > max)
                max = word.getValue();

        for (Map.Entry<String, Integer> word : words.entrySet())
            if (word.getValue() == max)
                System.out.println(filename + ": " + word.getKey());
    }
}