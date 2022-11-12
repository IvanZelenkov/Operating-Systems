import java.util.ArrayList;
import java.util.List;

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

    public void addNeighbor(Vertex<T> adjacent) {
        adjacencyList.add(adjacent);
    }

    public void removeNeighbor(Vertex<T> adjacent) {
        adjacencyList.remove(adjacent);
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    public List<Vertex<T>> getAdjacencyList() {
        return adjacencyList;
    }

    public List<Vertex<T>> getProcessesWaitingForResource() {
        return processesWaitingForResource;
    }


    public void addWaitingProcess(Vertex<T> vertex) {
        processesWaitingForResource.add(vertex);
    }

    public void removeWaitingProcess(Vertex<T> vertex) {
        processesWaitingForResource.remove(vertex);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    public boolean isBeingVisited() {
        return isBeingVisited;
    }

    public void setBeingVisited(boolean beingVisited) {
        isBeingVisited = beingVisited;
    }

    public boolean isVisited() {
        return isVisited;
    }

    public void setVisited(boolean visited) {
        isVisited = visited;
    }
}