package PartB;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

/**
 * @author Ivan Zelenkov
 */
public class CallableTask implements Callable<Map<String, Map<String, Integer>>> {

    private final String filename;

    public CallableTask(String filename) {
        this.filename = filename;
    }

    @Override
    public Map<String, Map<String, Integer>> call() throws Exception {
        Map<String, Integer> words = new HashMap<>();
        Map<String, Map<String, Integer>> result = new HashMap<>();
        Map<String, Integer> maxWordData = new HashMap<>();
        String file = String.valueOf(Paths.get(filename).getFileName());
        Stream<String> stream = Files.lines(Paths.get(filename));
        stream.forEach(
            line-> {
                String[] tokens = line.split("[^a-zA-Z]");
                for (String token : tokens) {
                    if (token.length() < 5)
                        continue;
                    if (words.containsKey(token.trim()))
                        words.put(token.trim(), words.get(token.trim()) + 1);
                    else
                        words.put(token.trim(), 1);
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