import java.util.ArrayList;
import java.util.List;

public class Vertex<T> {
    private T id;
    private T type;
    private final List<Vertex<String>> adjacencyList;
    private final List<Vertex<String>> waitingList;

    private boolean isBeingVisited;
    private boolean isVisited;

    public Vertex(T id, T type) {
        this.id = id;
        this.type = type;
        this.adjacencyList = new ArrayList<>();
        this.waitingList = new ArrayList<>();
    }

    public void addNeighbor(Vertex<String> adjacent) {
        adjacencyList.add(adjacent);
    }

    public void removeNeighbor(Vertex<String> adjacent) {
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

    public List<Vertex<String>> getAdjacencyList() {
        return adjacencyList;
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