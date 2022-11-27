import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RAG class (Resource Allocation Graph) represents the properties and methods of a directed graph for constructing such a graph.
 * @author Ivan Zelenkov
 */
public class RAG {
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