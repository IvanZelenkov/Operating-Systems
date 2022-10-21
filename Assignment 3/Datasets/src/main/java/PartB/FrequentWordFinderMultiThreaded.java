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

public class FrequentWordFinderMultiThreaded implements Runnable {

    private static final Map<String, Integer> words = Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {
        Runnable runnable = new PartB.FrequentWordFinderMultiThreaded();

        Thread thread0 = new Thread(runnable);
        Thread thread1 = new Thread(runnable);
        Thread thread2 = new Thread(runnable);
        Thread thread3 = new Thread(runnable);
        thread0.start();
//        thread1.start();
//        thread2.start();
//        thread3.start();
    }

    @Override
    public void run() {
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

//            for (String filename : set)
            fileReader("CarAds.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fileReader(String filename) {
        Path path = Paths.get("");
        try (Scanner scanner = new Scanner(new FileReader(String.format(path.toAbsolutePath() + "/src/main/resources/PartB/" + filename, filename)))) {
            while (scanner.hasNext()) {
                String[] tokens = scanner.next().split("[^a-z]");
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
        }
        getMaxOccurrence(filename, words);
    }

    private static void getMaxOccurrence(String filename, Map<String, Integer> words) {
        int max = 1;

        for (Map.Entry<String, Integer> word : words.entrySet())
            if (word.getValue() > max)
                max = word.getValue();


        for (Map.Entry<String, Integer> word : words.entrySet())
            if (word.getValue() == max) {
                synchronized (PartA.FrequentWordFinderMultiThreaded.class) {
                    System.out.println(filename + ": " + word.getKey() + word.getValue());
                    Runtime.getRuntime().halt(0);
                }
            }
    }
}
