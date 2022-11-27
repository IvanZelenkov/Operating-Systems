import java.util.ArrayList;
import java.util.List;

/**
 * Vertex class represents the properties and methods that must be used in order to create and manage a vertex.
 * @author Ivan Zelenkov
 * @param <T> This describes my type parameter.
 */
public class Vertex<T> {
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