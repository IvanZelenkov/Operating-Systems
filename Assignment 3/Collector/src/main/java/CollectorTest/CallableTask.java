package CollectorTest;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class CallableTask implements Callable<Map<String, Integer>> {

    private final String fileName;

    public CallableTask(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Map<String,Integer> call() throws Exception {
        Map<String,Integer> map = new HashMap<>();
        Stream<String> stream = Files.lines(Paths.get(fileName));
        stream.forEach(
                line-> {
                    String[] tokens = line.split("[^a-zA-Z]");
                    for (String token : tokens)
                        if (map.containsKey(token.trim()))
                            map.put(token.trim(), map.get(token.trim()) + 1);
                        else
                            map.put(token.trim(), 1);
                }
        );
        return map;
    }
}
