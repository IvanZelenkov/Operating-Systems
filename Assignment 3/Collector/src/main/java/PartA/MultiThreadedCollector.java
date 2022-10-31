package PartA;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Ivan Zelenkov
 * MultiThreadedCollector class read file in PartA folder,
 * count occurred words, and return an output that will contain the filename,
 * the most occurred word in that file, and the number of times that word was repeated.
 */
public class MultiThreadedCollector {

    private static List<String> filesList;

    public static void main(String[] args) {
        final int numberOfThreads = 1;
        Thread[] threads = new Thread[numberOfThreads];

        double startTime = System.currentTimeMillis();
        System.out.println("Reading in progress...\n");

        fileFinder();

        final int filesPerThread = filesList.size() / numberOfThreads;
        final int remainingFiles = filesList.size() % numberOfThreads;

        for (int i = 0; i < numberOfThreads; i++) {
            final int thread = i;
            threads[i] = new Thread(() -> runThread(filesList, numberOfThreads, thread, filesPerThread, remainingFiles));
        }

        for (Thread thread : threads)
            thread.start();
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        double endTime = System.currentTimeMillis();
        double executionTotalTime = (endTime - startTime) / 1000;
        System.out.println("\nExecution time: " + executionTotalTime + " seconds");
    }

    /**
     * The method searches for the files in a specified directory and stores filenames in a list.
     */
    private static void fileFinder() {
        Path path = Paths.get("");
        try {
            Stream<Path> files = Files.list(Paths.get(path.toAbsolutePath() + "/src/main/resources/PartA/"));

            filesList = files
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method assigns each thread to process the required number of files.
     * @param files List of files that have to be processed by all threads in order to complete the task.
     * @param numberOfThreads The total number of threads to be created.
     * @param thread Current thread number.
     * @param filesPerThread A fixed number of files will be assigned to the current thread.
     * @param remainingFiles The number of files remaining to process will be assigned additionally to the thread.
     */
    private static void runThread(List<String> files, int numberOfThreads, int thread, int filesPerThread, int remainingFiles) {
        List<String> inFiles = new ArrayList<>();

        for (int i = thread * filesPerThread; i < (thread + 1) * filesPerThread; i++)
            inFiles.add(files.get(i));

        if (thread == numberOfThreads - 1 && remainingFiles > 0)
            for (int i = files.size() - remainingFiles; i < files.size(); i++)
                inFiles.add(files.get(i));

        for (String file : inFiles) {
            System.out.println("Processing " + file + " in thread " + Thread.currentThread().getName());
            fileReader(file, new HashMap<>());
        }
    }

    /**
     * The method reads the file line by line, splits each line into words, and then collects each word.
     * @param filename The current file that the program is processing.
     * @param words The map will contain all occurring words from the file.
     */
    private static void fileReader(String filename, Map<String, Integer> words) {
        Path path = Paths.get("");
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(String.format(path.toAbsolutePath() + "/src/main/resources/PartA/" + filename, filename)))) {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("[^a-zA-Z]");
                for (String token : tokens) {
                    String word = token.trim().toLowerCase().replaceAll("[^a-z]", "");

                    if (word.length() < 5)
                        continue;

                    synchronized (MultiThreadedCollector.class) {
                        Integer counter = words.get(word);

                        if (counter != null)
                            counter++;
                        else
                            counter = 1;

                        words.put(word, counter);
                    }
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
    private static synchronized void getMostOccurredWord(String filename, Map<String, Integer> words) {
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