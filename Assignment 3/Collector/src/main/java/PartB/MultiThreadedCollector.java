package PartB;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Ivan Zelenkov
 * Collector class read files in a folder with executor service, count occurred words,
 * and return an output that will contain the filename, the most occurred
 * word in that file, and the number of times that word was repeated.
 */
public class MultiThreadedCollector {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("Reading in progress...\n");
        double startTime = System.currentTimeMillis();

        // Create a thread pool with #(number) threads
        ExecutorService executorService = Executors.newFixedThreadPool(14);

        List<CallableTask> callableTasks = new ArrayList<>();

        // Future represents the result of an asynchronous computation.
        // Methods are provided to check if the computation is complete,
        // to wait for its completion, and to retrieve the result of the computation.
        List<Future<Map<String, Map<String, Integer>>>> result = new ArrayList<>();

        // Add files to list
        Path path = Paths.get("");
        File file = new File(path.toAbsolutePath() + "/src/main/resources/PartB/");
        File[] listOfFiles = new File[0];
        if (!file.isFile())
            listOfFiles = file.listFiles();

        // Iterate through the list of files, create an instance of a class
        // that implements a Callable interface and add it to a list
        if (listOfFiles != null) {
            for (File f : listOfFiles)
                callableTasks.add(new CallableTask(f.getAbsolutePath()));
        } else {
            System.out.println("The specified directory is empty. There are no files to read.");
            Runtime.getRuntime().halt(0);
        }

        // Submit a Callable task to an ExecutorService and return
        // a result of type Future that is added to a result list.
        for (CallableTask callableTask : callableTasks)
            result.add(executorService.submit(callableTask));

        String filename = "", word = "";
        Integer occurrenceNumber = 0;

        // Calculate the final result.
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