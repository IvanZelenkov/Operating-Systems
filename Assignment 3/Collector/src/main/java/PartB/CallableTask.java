package PartB;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

/**
 * CallableTask class implements the Callable interface and
 * its method which is counting words and adding them to a result map.
 * @author Ivan Zelenkov
 */
public class CallableTask implements Callable<Map<String, Map<String, Integer>>> {

    private final String filename;

    public CallableTask(String filename) {
        this.filename = filename;
    }

    /**
     * Computes a result of finding words and putting them into a map, or throws an exception if unable to do so.
     * @return The map will contain all occurring words from the file.
     * @throws IOException Throws an I/O exception if unable to compute a result.
     */
    @Override
    public Map<String, Map<String, Integer>> call() throws IOException {
        Map<String, Integer> words = new HashMap<>();
        Map<String, Map<String, Integer>> result = new HashMap<>();
        Map<String, Integer> maxWordData = new HashMap<>();
        String file = String.valueOf(Paths.get(filename).getFileName());
        Stream<String> stream = Files.lines(Paths.get(filename));
        stream.forEach(
                line -> {
                    String[] tokens = line.split("[^a-zA-Z]");
                    for (String token : tokens) {
                        if (token.length() < 5)
                            continue;
                        if (words.containsKey(token.trim().toLowerCase()))
                            words.put(token.trim().toLowerCase(), words.get(token.trim().toLowerCase()) + 1);
                        else
                            words.put(token.trim().toLowerCase(), 1);
                    }
                }
        );

        int maxOccurredNumber = 1;
        String maxOccurredWord = "";
        for (Map.Entry<String, Integer> word : words.entrySet()) {
            if (word.getValue() > maxOccurredNumber) {
                maxOccurredNumber = word.getValue();
                maxOccurredWord = word.getKey();
            }
        }
        maxWordData.put(maxOccurredWord, maxOccurredNumber);
        result.put(file, maxWordData);
        return result;
    }
}