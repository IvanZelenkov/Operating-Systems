import java.util.*;

public class RAG {
    private final List<Vertex<String>> vertices;

    public RAG() {
        this.vertices = new ArrayList<>();
    }

    public List<Vertex<String>> getVertices() {
        return vertices;
    }

    public void addVertex(Vertex<String> vertex) {
        vertices.add(vertex);
    }

    public void removeVertex(Vertex<String> vertex) {
        vertices.remove(vertex);
    }

    public Vertex<String> hasVertex(Vertex<String> newVertex) {
        for (Vertex<String> vertex : vertices) {
            if (vertex.getId().equals(newVertex.getId()) && vertex.getType().equals(newVertex.getType())) {
                return vertex;
            }
        }
        return null;
    }

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

    public boolean hasCycle(Vertex<String> sourceVertex) {
        sourceVertex.setBeingVisited(true);

        for (Vertex<String> neighbor : sourceVertex.getAdjacencyList()) {
            if (neighbor.isBeingVisited()) {
                // backward edge exists
                return true;
            } else if (!neighbor.isVisited() && hasCycle(neighbor)) {
                return true;
            }
        }

        sourceVertex.setBeingVisited(false);
        sourceVertex.setVisited(true);
        return false;
    }
}