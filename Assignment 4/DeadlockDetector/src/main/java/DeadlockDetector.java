import java.util.Collections;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The DeadlockDetector class builds a RAG (Resource Allocation Graph) and on every action when a process
 * wants or releases resources, it checks if there is a cycle in the graph, which means there is a deadlock.
 * The result will be output to the txt file.
 * @author Ivan Zelenkov
 */
public class DeadlockDetector {

    private static final RAG rag = new RAG();

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter a filename to read.\n>>> ");
        String readFilename = reader.readLine();
        System.out.print("Enter a filename where the output will be saved.\n>>> ");
        String writeFilename = reader.readLine();
        System.out.println();

        while (!fileReaderWriter(readFilename, writeFilename)) {
            System.out.print(">>> (filename): ");
            readFilename = reader.readLine();
            System.out.println();
        }
    }

    /**
     * Reads the file line by line, splits each line into words, and then collects each word.
     * @param readFilename file that the program will read.
     * @param writeFilename file where the program's output will be saved.
     * @return ...
     */
    public static boolean fileReaderWriter(String readFilename, String writeFilename) {
        boolean isRead = false;
        Path path = Paths.get("");
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(String.format(path.toAbsolutePath() + "/src/main/resources/input/%s.txt", readFilename)));
             BufferedWriter writer = new BufferedWriter(new FileWriter(getWriteFile(writeFilename)))) {
            while ((line = reader.readLine()) != null) {
                connector(line, writer);
                isRead = true;
            }
            writer.write("EXECUTION COMPLETED: No deadlock encountered.");
            System.out.println("EXECUTION COMPLETED: No deadlock encountered.\n");
            System.out.println("The output was successfully written to the file.");
        } catch (FileNotFoundException e) {
            System.out.print("Invalid file name, please try again.\n");
        } catch (IOException e) {
            System.out.println("Error: operating system can't complete the appropriate Input/Output.");
        }
        return isRead;
    }

    /**
     * Creates a connection between process and resource based on the current state of the graph.
     * @param line contains process, action, and resource that will be used in graph.
     */
    private static void connector(String line, BufferedWriter writer) throws IOException {
        // tokens[0] => process, tokens[1] = W(wants) or R(releases), tokens[2] => resource
        String[] tokens = line.trim().split("\\s+");

        Vertex<String> process = new Vertex<>(tokens[0], "process");
        Vertex<String> resource = new Vertex<>(tokens[2], "resource");

        // if there is already such a process then just return its data
        Vertex<String> existingProcess = rag.hasVertex(process);
        if (existingProcess == null)
            rag.addVertex(process);
        else
            process = existingProcess;

        // if there is already such a resource then just return its data
        Vertex<String> existingResource = rag.hasVertex(resource);
        if (existingResource == null)
            rag.addVertex(resource);
        else
            resource = existingResource;

        // if the process wants the resource
        if (tokens[1].equalsIgnoreCase("w")) {
            // if the process is already waiting on other resources
            if (process.isWaiting()) {
                writer.write(
                        process.getType() + " " + process.getId() + " is already waiting. " +
                        "Process " + process.getId() + " wants " +
                        resource.getType() + " " + resource.getId() + " action is ignored.\n");
                System.out.println(
                        process.getType() + " " + process.getId() + " is already waiting. " +
                        "Process " + process.getId() + " wants " +
                        resource.getType() + " " + resource.getId() + " action is ignored.");
            }
            // if the resource vertex is already connected to another vertex (i.e., another process), the process must wait
            else if (resource.isConnected()) {
                rag.addEdge(process, resource);
                resource.addWaitingProcess(process);
                process.setWaiting(true);
                writer.write(process.getType() + " " + process.getId() + " wants " +
                        resource.getType() + " " + resource.getId() + " - " +
                        process.getType() + " " + process.getId() + " must wait.\n"
                );
                System.out.println(
                        process.getType() + " " + process.getId() + " wants " +
                        resource.getType() + " " + resource.getId() + " - " +
                        process.getType() + " " + process.getId() + " must wait."
                );
                // Debug output
                // System.out.println("Adjacency List of Process " + process.getId() + ": " + process.getAdjacencyList());
                // System.out.println("Adjacency List of Resource " + resource.getId() + ": " + resource.getAdjacencyList());
                // System.out.println("Processes Waiting For Resource " + resource.getId() + ": "+ resource.getProcessesWaitingForResource() + "\n");
            }
            // if the resource is not connected to anything (i.e., it is free), then add an edge from the resource to process
            else {
                rag.addEdge(process, resource);
                writer.write(process.getType() + " " + process.getId() + " wants " +
                        resource.getType() + " " + resource.getId() + " - " +
                        resource.getType() + " " + resource.getId() + " is allocated to " +
                        process.getType() + " " + process.getId() + "\n"
                );
                System.out.println(
                        process.getType() + " " + process.getId() + " wants " +
                        resource.getType() + " " + resource.getId() + " - " +
                        resource.getType() + " " + resource.getId() + " is allocated to " +
                        process.getType() + " " + process.getId()
                );
                // Debug output
                // System.out.println("Adjacency List of Process " + process.getId() + ": " + process.getAdjacencyList());
                // System.out.println("Adjacency List of Resource " + resource.getId() + ": " + resource.getAdjacencyList());
                // System.out.println("Processes Waiting For Resource " + resource.getId() + ": "+ resource.getProcessesWaitingForResource() + "\n");
            }
        }
        else if (tokens[1].equalsIgnoreCase("r")) {
            if (!resource.getProcessesWaitingForResource().isEmpty()) {
                for (Vertex<String> waitingProcess : resource.getProcessesWaitingForResource()) {
                    // revert an edge
                    rag.removeEdge(process, resource);
                    rag.removeEdge(waitingProcess, resource);
                    resource.removeWaitingProcess(waitingProcess);
                    rag.addEdge(waitingProcess, resource);
                    writer.write(process.getType() + " " + process.getId() + " releases " +
                            resource.getType() + " " + resource.getId() + " - " +
                            resource.getType() + " " + resource.getId() + " is allocated to " +
                            waitingProcess.getType() + " " + waitingProcess.getId() + "\n"
                    );
                    System.out.println(
                            process.getType() + " " + process.getId() + " releases " +
                            resource.getType() + " " + resource.getId() + " - " +
                            resource.getType() + " " + resource.getId() + " is allocated to " +
                            waitingProcess.getType() + " " + waitingProcess.getId()
                    );
                    // Debug output
                    // System.out.println("Adjacency List of Process " + process.getId() + ": " + process.getAdjacencyList());
                    // System.out.println("Adjacency List of Resource " + resource.getId() + ": " + resource.getAdjacencyList());
                    // System.out.println("Processes Waiting For Resource " + resource.getId() + ": "+ resource.getProcessesWaitingForResource() + "\n");
                    break;
                }
            }
            // if the resource's processesWaitingForResource list is empty, then remove an existing edge between the resource and process to make a resource free
            else {
                rag.removeEdge(process, resource);
                writer.write(process.getType() + " " + process.getId() + " releases " +
                        resource.getType() + " " + resource.getId() + " - " +
                        resource.getType() + " " + resource.getId() + " is now free.\n"
                );
                System.out.println(
                        process.getType() + " " + process.getId() + " releases " +
                        resource.getType() + " " + resource.getId() + " - " +
                        resource.getType() + " " + resource.getId() + " is now free."
                );
                // Debug output
                // System.out.println("Adjacency List of Process " + process.getId() + ": " + process.getAdjacencyList());
                // System.out.println("Adjacency List of Resource " + resource.getId() + ": " + resource.getAdjacencyList());
                // System.out.println("Processes Waiting For Resource " + resource.getId() + ": "+ resource.getProcessesWaitingForResource() + "\n");
            }
        }

        // Debug graph building
        // List<Vertex<String>> vertices = rag.getVertices();
        // for (Vertex<String> vertex : vertices) {
        //     System.out.println(vertex.getId() + " : " + vertex.getType());
        //     List<Vertex<String>> adjacencyList = vertex.getAdjacencyList();
        //     for (Vertex<String> v : adjacencyList) {
        //         System.out.println("    |");
        //         System.out.println("     -- " + v.getId() + " : " + v.getType());
        //     }
        //     System.out.println();
        // }

        checkCycle(writer);
    }

    /**
     * Checks for the presence of a cycle (deadlock) in the graph. If there is, print a message
     * to the user and halt the JVM. Otherwise, output to the user that there is no deadlock in the graph.
     */
    private static void checkCycle(BufferedWriter writer) throws IOException {
        for (int i = 0; i < rag.getVertices().size(); i++) {
            if (rag.hasCycle(rag.getVertices().get(i))) {
                Collections.sort(rag.getDeadlockPath().get("process"));
                Collections.sort(rag.getDeadlockPath().get("resource"));

                StringBuilder sb = new StringBuilder();
                sb.append("DEADLOCK DETECTED: Processes ");

                for (int j = 0; j < rag.getDeadlockProcesses().size(); j++)
                    if (j == rag.getDeadlockProcesses().size() - 1)
                        sb.append(rag.getDeadlockProcesses().get(j));
                    else
                        sb.append(rag.getDeadlockProcesses().get(j)).append(", ");

                sb.append(" and ");

                for (int j = 0; j < rag.getDeadlockResources().size(); j++)
                    if (j == rag.getDeadlockResources().size() - 1)
                        sb.append(rag.getDeadlockResources().get(j));
                    else
                        sb.append(rag.getDeadlockResources().get(j)).append(", ");


                sb.append(" are found in cycle.");

                writer.write(String.valueOf(sb));
                System.out.println(sb + "\n");
                System.out.println("The output was successfully written to the file.");

                Runtime.getRuntime().halt(0);
            }
        }
    }

    /**
     * Method writes resources to the classpath.
     * @return loaded resources from the classpath.
     */
    private static String getWriteFile(String filename) {
        Path path = Paths.get("");
        try {
            File file = new File(String.format(path.toAbsolutePath() + "/src/main/resources/output/%s.txt", filename));
            if (file.createNewFile())
                System.out.printf("> File %s.txt created\n", filename);
            else
                System.out.println("> File already exists\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.format(path.toAbsolutePath() + "/src/main/resources/output/%s.txt", filename);
    }
}