package PartB;

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
        System.out.println("Reading in progress...\n");
        double startTime = System.currentTimeMillis();

        // Create a thread pool with #(number) threads
        ExecutorService executorService = Executors.newFixedThreadPool(14);

        List<CallableTask> list = new ArrayList<>();

        // Future represents the result of an asynchronous computation.
        // Methods are provided to check if the computation is complete,
        // to wait for its completion, and to retrieve the result of the computation.
        List<Future<Map<String, Map<String, Integer>>>> result = new ArrayList<>();

        Map<String, Integer> finalResult = new HashMap<>();

        // Add files to list
        Path path = Paths.get("");
        File file = new File(path.toAbsolutePath() + "/src/main/resources/PartB/");
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
        for (CallableTask fileReaderTest : list)
            result.add(executorService.submit(fileReaderTest));

        String filename = "", word = "";
        Integer occurrenceNumber = 0;
        // Calculate final result.
        for (Future<Map<String, Map<String, Integer>>> mapFuture : result) {
            for (Map.Entry<String, Map<String, Integer>> fileData : mapFuture.get().entrySet()) {
                filename = fileData.getKey();
                Map<String, Integer> wordData = fileData.getValue();
                for (Map.Entry<String, Integer> data : wordData.entrySet()) {
                    word = data.getKey();
                    occurrenceNumber = data.getValue();
                }
            }
            System.out.println(filename + ": " + "'" + word + "'" + " was found " + occurrenceNumber + " times.");
        }

        executorService.shutdown();

        double endTime = System.currentTimeMillis();
        double executionTotalTime = (endTime - startTime) / 1000;
        System.out.println("\nExecution time: " + executionTotalTime + " seconds");
    }
}