import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class Deadlock {

    private static final RAG rag = new RAG();

    public static void main(String[] args) {
        String filename = "input2.txt";
        fileReader(filename);
        System.out.println("EXECUTION COMPLETED: No deadlock encountered.");

//        List<Vertex<String>> vertices = rag.getVertices();
//        for (Vertex<String> vertex : vertices) {
//            System.out.println(vertex.getId() + " : " + vertex.getType());
//            List<Vertex<String>> adjacencyList = vertex.getAdjacencyList();
//            for (Vertex<String> v : adjacencyList) {
//                System.out.println("    |");
//                System.out.println("     -- " + v.getId() + " : " + v.getType());
//            }
//            System.out.println();
//        }
    }

    private static void fileReader(String filename) {
        Path path = Paths.get("");
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(String.format(path.toAbsolutePath() + "/src/main/resources/" + filename, filename)))) {
            while ((line = reader.readLine()) != null) {
                construct(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: file not found.");
        } catch (IOException e) {
            System.out.println("Error: operating system can't complete the appropriate Input/Output.");
        }
    }

    public static void construct(String line) {
        String[] tokens = line.trim().split("\\s+");

        Vertex<String> process = new Vertex<>(tokens[0], "process");
        Vertex<String> resource = new Vertex<>(tokens[2], "resource");

        // if there is already such a vertex then just return its data
        Vertex<String> existingVertex = rag.hasVertex(process);
        if (existingVertex == null) {
            rag.addVertex(process);
        } else {
            process = existingVertex;
        }

        existingVertex = rag.hasVertex(resource);
        if (existingVertex == null) {
            rag.addVertex(resource);
        } else {
            resource = existingVertex;
        }

        if (tokens[1].equalsIgnoreCase("w")) {
            // if the process is already waiting on other resources
            if (process.isWaiting()) {
                System.out.println(process.getType() + " " + process.getId() + " is already waiting. Do nothing.");
            }
            // if the resource vertex is already connected to another vertex (i.e., another process), the process must wait
            else if (resource.isConnected()) {
                rag.addEdge(process, resource);
                resource.addWaitingProcess(process);
                process.setWaiting(true);
                System.out.println(
                        process.getType() + " " + process.getId() + " wants " +
                        resource.getType() + " " + resource.getId() + " - " +
                        process.getType() + " " + process.getId() + " must wait."
                );
                System.out.println("Adjacency List of Process " + process.getId() + ": " + process.getAdjacencyList());
                System.out.println("Adjacency List of Resource " + resource.getId() + ": " + resource.getAdjacencyList());
                System.out.println("Processes Waiting For Resource " + resource.getId() + ": "+ resource.getProcessesWaitingForResource() + "\n");
            }
            // if the resource is not connected to anything (i.e., it is free), then add an edge from the resource to process
            else {
                rag.addEdge(process, resource);
                System.out.println(
                        process.getType() + " " + process.getId() + " wants " +
                                resource.getType() + " " + resource.getId() + " - " +
                                resource.getType() + " " + resource.getId() + " is allocated to " +
                                process.getType() + " " + process.getId()
                );
                System.out.println("Adjacency List of Process " + process.getId() + ": " + process.getAdjacencyList());
                System.out.println("Adjacency List of Resource " + resource.getId() + ": " + resource.getAdjacencyList());
                System.out.println("Processes Waiting For Resource " + resource.getId() + ": "+ resource.getProcessesWaitingForResource() + "\n");
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
                    System.out.println(
                            process.getType() + " " + process.getId() + " releases " +
                            resource.getType() + " " + resource.getId() + " - " +
                            resource.getType() + " " + resource.getId() + " is allocated to " +
                            waitingProcess.getType() + " " + waitingProcess.getId()
                    );
                    System.out.println("Adjacency List of Process " + process.getId() + ": " + process.getAdjacencyList());
                    System.out.println("Adjacency List of Resource " + resource.getId() + ": " + resource.getAdjacencyList());
                    System.out.println("Processes Waiting For Resource " + resource.getId() + ": "+ resource.getProcessesWaitingForResource() + "\n");
                    break;
                }
            }
            // if the resource's processesWaitingForResource list is empty, then remove an existing edge between the resource and process to make a resource free
            else {
                rag.removeEdge(process, resource);
                System.out.println(
                        process.getType() + " " + process.getId() + " releases " +
                        resource.getType() + " " + resource.getId() + " - " +
                        resource.getType() + " " + resource.getId() + " is now free."
                );
                System.out.println("Adjacency List of Process " + process.getId() + ": " + process.getAdjacencyList());
                System.out.println("Adjacency List of Resource " + resource.getId() + ": " + resource.getAdjacencyList());
                System.out.println("Processes Waiting For Resource " + resource.getId() + ": "+ resource.getProcessesWaitingForResource() + "\n");
            }
        }
        for (int i = 0; i < rag.getVertices().size(); i++) {
            if (rag.hasCycle(rag.getVertices().get(i))) {
                Collections.sort(rag.getDeadlockPath().get("process"));
                Collections.sort(rag.getDeadlockPath().get("resource"));

                StringBuilder sb = new StringBuilder();
                sb.append("DEADLOCK DETECTED: Processes ");

                for (int j = 0; j < rag.getDeadlockProcesses().size(); j++) {
                    if (j == rag.getDeadlockProcesses().size() - 1)
                        sb.append(rag.getDeadlockProcesses().get(j));
                    else
                        sb.append(rag.getDeadlockProcesses().get(j)).append(", ");
                }

                sb.append(" and ");

                for (int j = 0; j < rag.getDeadlockResources().size(); j++) {
                    if (j == rag.getDeadlockResources().size() - 1)
                        sb.append(rag.getDeadlockResources().get(j));
                    else
                        sb.append(rag.getDeadlockResources().get(j)).append(", ");
                }

                System.out.println(sb);

                Runtime.getRuntime().halt(0);
            }
        }
    }
}
