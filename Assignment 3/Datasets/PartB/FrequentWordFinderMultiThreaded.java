package PartB;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FrequentWordFinderMultiThreaded {
    private static Set<String> filesSet;

    public static void main(String[] args) {
        double startTime = System.currentTimeMillis();
        fileFinder();
        System.out.println("Reading in progress...\n");

        int numberOfThreads = 14;
        Thread[] threads = new Thread[numberOfThreads];

        final int filesPerThread = filesSet.size() / numberOfThreads;
        final int remainingFiles = filesSet.size() % numberOfThreads;

        List<String> files = new ArrayList<>(filesSet);

        for (int i = 0; i < numberOfThreads; i++) {
            final int thread = i;
            threads[i] = new Thread() {
                @Override
                public void run() {
                    Map<String, Integer> words = new HashMap<>();
                    runThread(files, words, numberOfThreads, thread, filesPerThread, remainingFiles);
                }
            };
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

    private static void runThread(List<String> files, Map<String, Integer> words, int numberOfThreads, int thread, int filesPerThread, int remainingFiles) {
        List<String> inFiles = new ArrayList<>();

        for (int i = thread * filesPerThread; i < (thread + 1) * filesPerThread; i++)
            inFiles.add(files.get(i));

        if (thread == numberOfThreads - 1 && remainingFiles > 0)
            for (int i = files.size() - remainingFiles; i < files.size(); i++)
                inFiles.add(files.get(i));

        for (String file : inFiles) {
//            System.out.println("Processing " + file + " in thread " + Thread.currentThread().getName());
            fileReader(file, words);
        }
    }

    private static void fileFinder() {
        Path path = Paths.get("");
        try {
            Stream<Path> files = Files.list(Paths.get(path.toAbsolutePath() + "/src/main/resources/PartB/"));

            filesSet = files
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void fileReader(String filename, Map<String, Integer> words) {
        Path path = Paths.get("");
        String line = "";
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
            e.printStackTrace();
        }
        getMaxOccurrence(filename, words);
    }

    private static synchronized void getMaxOccurrence(String filename, Map<String, Integer> words) {
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
                System.out.println(filename + ": " + "'" + word.getKey() + "'" + " was found " + word.getValue() + " times.");
            }
        }
    }
}
