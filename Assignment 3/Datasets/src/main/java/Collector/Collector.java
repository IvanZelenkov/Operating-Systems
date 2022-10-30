package Collector;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author Ivan Zelenkov
 * Reading files in folder with executor service and counting words with count.
 */
public class Collector {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        double startTime = System.currentTimeMillis();
        System.out.println("Reading in progress...\n");

        // Create a thread pool with # threads
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        List<CallableTask> list = new ArrayList<>();

        // Future represents the result of an asynchronous computation.
        // Methods are provided to check if the computation is complete,
        // to wait for its completion, and to retrieve the result of the computation.
        List<Future<Map<String,Integer>>> result = new ArrayList<>();

        Map<String,Integer> finalResult = new HashMap<>();

        // Add files to list
        Path path = Paths.get("");
        File file = new File(path.toAbsolutePath() + "/src/main/resources/PartA/");
        File[] listOfFiles = {};
        if (!file.isFile())
            listOfFiles = file.listFiles();

        // Iterate through the list of files, create an instance of a class
        // that implements a Callable interface and add it to a list
        assert listOfFiles != null;
        for (File f : listOfFiles)
            list.add(new CallableTask(f.getAbsolutePath()));

        // Submit a Callable task to an ExecutorService and return
        // a result of type Future that is added to a result list.
        for (CallableTask callableTask : list)
            result.add(executorService.submit(callableTask));

        // Calculate final result.
        for (Future<Map<String, Integer>> mapFuture : result) {
            // mapFuture.get() - retrieve the result of the computation.
            // forEach() - iterate through the resulting future map and build the final result map.
            mapFuture.get().forEach(
                    (k, v) -> {
                        if (finalResult.containsKey(k)) {
                            finalResult.put(k, finalResult.get(k) + v);
                        } else {
                            finalResult.put(k, v);
                        }
                    }
            );
        }

        System.out.println(finalResult);
        executorService.shutdown();

        double endTime = System.currentTimeMillis();
        double executionTotalTime = (endTime - startTime) / 1000;
        System.out.println("\nExecution time: " + executionTotalTime + " seconds");
    }
}