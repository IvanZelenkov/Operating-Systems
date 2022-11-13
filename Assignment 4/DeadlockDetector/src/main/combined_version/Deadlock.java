import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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
 * The Deadlock class builds a RAG (Resource Allocation Graph) and on every action when a process
 * wants or releases resources, it checks if there is a cycle in the graph, which means there is a deadlock.
 * The result will be output to the txt file.
 * @author Ivan Zelenkov
 */
public class Deadlock {

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
        boolean isDeadlock = false;
        Path path = Paths.get("");
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(String.format(path.toAbsolutePath() + "/%s.txt", readFilename)));
             BufferedWriter writer = new BufferedWriter(new FileWriter(getWriteFile(writeFilename)))) {
            while ((line = reader.readLine()) != null && !isDeadlock) {
                isDeadlock = connector(line, writer);
                isRead = true;
            }
            if (!isDeadlock) {
                writer.write("EXECUTION COMPLETED: No deadlock encountered.");
                System.out.println("EXECUTION COMPLETED: No deadlock encountered.\n");
            }
            System.out.printf("The output was successfully written to the file %s.txt.\n", writeFilename);
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
    private static boolean connector(String line, BufferedWriter writer) throws IOException {
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

        return checkCycle(writer);
    }

    /**
     * Checks for the presence of a cycle (deadlock) in the graph. If there is, print a message
     * to the user and halt the JVM. Otherwise, output to the user that there is no deadlock in the graph.
     */
    private static boolean checkCycle(BufferedWriter writer) throws IOException {
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

                return true;
            }
        }
        return false;
    }

    /**
     * Method writes resources to the classpath.
     * @return loaded resources from the classpath.
     */
    private static String getWriteFile(String filename) {
        Path path = Paths.get("");
        try {
            File file = new File(String.format(path.toAbsolutePath() + "/%s.txt", filename));
            if (file.createNewFile())
                System.out.printf("File %s.txt created\n\n", filename);
            else
                System.out.println("File already exists\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return String.format(path.toAbsolutePath() + "/%s.txt", filename);
    }
}

/**
 * RAG class (Resource Allocation Graph) represents the properties and methods of a directed graph for constructing such a graph.
 */
class RAG {
    private final List<Vertex<String>> vertices;
    private final Map<String, List<String>> deadlockPath;
    private final List<String> deadlockProcesses;
    private final List<String> deadlockResources;

    public RAG() {
        this.vertices = new ArrayList<>();
        this.deadlockPath = new HashMap<>();
        this.deadlockProcesses = new ArrayList<>();
        this.deadlockResources = new ArrayList<>();
    }

    /**
     * Get the list of vertices that are in a graph.
     * @return list of vertices.
     */
    public List<Vertex<String>> getVertices() {
        return vertices;
    }

    /**
     * Get the path where the deadlock happened.
     * @return If there was a deadlock hashmap returns 2 key-values pairs, where the first contains
     * processes involved in a deadlock, and the second the resources involved in a deadlock.
     * Otherwise, the hashmap is empty.
     */
    public Map<String, List<String>> getDeadlockPath() {
        return deadlockPath;
    }

    /**
     * Get the list of processes involved in a deadlock if there was one.
     * @return list of processes involved in deadlock.
     */
    public List<String> getDeadlockProcesses() {
        return deadlockProcesses;
    }

    /**
     * Get the list of resources involved in a deadlock if there was one.
     * @return list of resources involved in deadlock
     */
    public List<String> getDeadlockResources() {
        return deadlockResources;
    }

    /**
     * Adds new vertex to list of vertices.
     * @param vertex new vertex that will be added to the graph.
     */
    public void addVertex(Vertex<String> vertex) {
        vertices.add(vertex);
    }

    /**
     * Removes the vertex from the list of vertices.
     * @param vertex vertex that will be deleted from the graph.
     */
    public void removeVertex(Vertex<String> vertex) {
        vertices.remove(vertex);
    }

    /**
     * Checks if such vertex already exists in a list of vertices (i.e., in. a graph).
     * @param vertex vertex to check if it already exists.
     * @return if there is already such a vertex, return the existing one,
     * otherwise, return null meaning that this is a new vertex that will be used.
     */
    public Vertex<String> hasVertex(Vertex<String> vertex) {
        for (Vertex<String> existingVertex : vertices)
            if (existingVertex.getId().equals(vertex.getId()) && existingVertex.getType().equals(vertex.getType()))
                return existingVertex;

        return null;
    }

    /**
     * Decides whether to add a directed edge from process to resource, or from resource
     * to process, depending on whether another process is holding this resource.
     * @param process process that wants to resource to be allocated
     * @param resource resource that can be allocated to the process.
     */
    public void addEdge(Vertex<String> process, Vertex<String> resource) {
        if (!resource.getAdjacencyList().isEmpty()) {
            process.addNeighbor(resource);
            process.setConnected(true);
        }
        else {
            resource.addNeighbor(process);
            resource.setConnected(true);
        }
    }

    /**
     * Decides whether to remove a directed edge that goes from the waiting process
     * to the resource, or from the resource that is allocated to the process.
     * @param process process that releases the resource.
     * @param resource resource that will be release by the process.
     */
    public void removeEdge(Vertex<String> process, Vertex<String> resource) {
        if (!resource.getAdjacencyList().isEmpty()) {
            resource.removeNeighbor(process);
            resource.setConnected(false);
        }
        else {
            process.removeNeighbor(resource);
            process.setConnected(false);
        }
    }

    /**
     * Checks if there is a cycle in the directed graph using To detect a cycle using a variation of DFS traversal.
     * @param sourceVertex Source vertex to start from to check for the cycle.
     * @return true if the cycle exists, false if the cycle does not exist.
     */
    public boolean hasCycle(Vertex<String> sourceVertex) {
        sourceVertex.setBeingVisited(true);

        for (Vertex<String> neighbor : sourceVertex.getAdjacencyList()) {
            if (neighbor.isBeingVisited()) {
                getDeadlockedVertexInfo(sourceVertex);
                return true;
            } else if (hasCycle(neighbor)) {
                getDeadlockedVertexInfo(sourceVertex);
                return true;
            }
        }

        sourceVertex.setBeingVisited(false);
        return false;
    }

    /**
     * Checks what type of vertex it is, and based on that puts data to deadlockPath
     * hashmap that will contain information about who is involved in a deadlock.
     * @param vertex vertex that was involved in a deadlock.
     */
    private void getDeadlockedVertexInfo(Vertex<String> vertex) {
        if (vertex.getType().equals("process")) {
            deadlockProcesses.add(vertex.getId());
            deadlockPath.put("process", deadlockProcesses);
        } else if (vertex.getType().equals("resource")) {
            deadlockResources.add(vertex.getId());
            deadlockPath.put("resource", deadlockResources);
        }
    }
}

/**
 * Vertex class represents the properties and methods that must be used in order to create and manage a vertex.
 * @param <T> This describes my type parameter.
 */
class Vertex<T> {
    private T id;
    private T type;
    private final List<Vertex<T>> adjacencyList;
    private List<Vertex<T>> processesWaitingForResource;

    private boolean isConnected;
    private boolean isWaiting;
    private boolean isBeingVisited;
    private boolean isVisited;

    public Vertex(T id, T type) {
        this.id = id;
        this.type = type;
        this.adjacencyList = new ArrayList<>();
        if (type.equals("resource"))
            this.processesWaitingForResource = new ArrayList<>();
    }

    /**
     * Connects the caller vertex with a coming vertex that will become an adjacent vertex of the caller.
     * @param adjacentVertex vertex that will become adjacent to the vertex caller.
     */
    public void addNeighbor(Vertex<T> adjacentVertex) {
        adjacencyList.add(adjacentVertex);
    }

    /**
     * Disconnects the caller vertex with a coming vertex (i.e. vertex will not be adjacent to the caller vertex).
     * @param adjacentVertex vertex that will be disconnected from the vertex caller (i.ie, no more adjacent to the caller vertex).
     */
    public void removeNeighbor(Vertex<T> adjacentVertex) {
        adjacencyList.remove(adjacentVertex);
    }

    /**
     * Gets vertex id.
     * @return vertex id (i.e., the process or resource number).
     */
    public T getId() {
        return id;
    }

    /**
     * Sets new vertex id.
     * @param id new vertex id (i.e., a new process or resource number).
     */
    public void setId(T id) {
        this.id = id;
    }

    /**
     * Gets vertex type.
     * @return vertex type (i.e., the process or resource).
     */
    public T getType() {
        return type;
    }

    /**
     * Sets new vertex type.
     * @param type new vertex type (i.e., the process or resource).
     */
    public void setType(T type) {
        this.type = type;
    }

    /**
     * Gets a list of adjacent vertices of the caller vertex.
     * @return list of adjacent vertices of the caller vertex.
     */
    public List<Vertex<T>> getAdjacencyList() {
        return adjacencyList;
    }

    /**
     * Gets a list of processes waiting for resource.
     * @return list of processes waiting for resource.
     */
    public List<Vertex<T>> getProcessesWaitingForResource() {
        return processesWaitingForResource;
    }

    /**
     * Adds a process to the list of the waiting processes that wait for the resource.
     * @param vertex vertex that is waiting for resource.
     */
    public void addWaitingProcess(Vertex<T> vertex) {
        processesWaitingForResource.add(vertex);
    }

    /**
     * Removes a process from the list of waiting processes that was waiting for the resource.
     * @param vertex vertex that was waiting for resource.
     */
    public void removeWaitingProcess(Vertex<T> vertex) {
        processesWaitingForResource.remove(vertex);
    }

    /**
     * Checks if the caller vertex is connected to another vertex
     * (i.e, if there exists a directed edge starting from the caller vertex to another vertex).
     * @return true if there is exit directed edge starting from the caller vertex to another vertex, otherwise return false
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Marks that the caller vertex is connected to another vertex
     * (i.e, there will exist a directed edge starting from the caller vertex to another vertex).
     * @param connected true if the caller vertex is connected to another vertex (i.e., an edge from caller
     * to another vertex was created), otherwise if the connection was removed, then set to false.
     */
    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    /**
     * Checks if the process is waiting for the resource.
     * @return true if the process is waiting, otherwise return false.
     */
    public boolean isWaiting() {
        return isWaiting;
    }

    /**
     * Sets the process into the waiting state or brings the vertex out of the waiting state.
     * @param waiting true to bring the process to the waiting state, false to bring the process out of the waiting state.
     */
    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    /**
     * Checks if the vertex is visited or not.
     * @return true if the vertex is already visited (results in a cycle meaning there is a deadlock), otherwise return false.
     */
    public boolean isBeingVisited() {
        return isBeingVisited;
    }

    /**
     * Sets the process as visited or unvisited.
     * @param beingVisited set to true if the vertex is visited, or if not set to false (by default).
     */
    public void setBeingVisited(boolean beingVisited) {
        isBeingVisited = beingVisited;
    }
}
