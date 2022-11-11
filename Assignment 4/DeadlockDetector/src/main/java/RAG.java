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

    public void addEdge(Vertex<String> vertex1, Vertex<String> vertex2) {
        vertex1.addNeighbor(vertex2);
    }

    public void removeEdge(Vertex<String> vertex1, Vertex<String> vertex2) {
        vertex1.removeNeighbor(vertex2);
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