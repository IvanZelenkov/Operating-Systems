import java.util.*;

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

    public List<Vertex<String>> getVertices() {
        return vertices;
    }

    public Map<String, List<String>> getDeadlockPath() {
        return deadlockPath;
    }

    public List<String> getDeadlockProcesses() {
        return deadlockProcesses;
    }

    public List<String> getDeadlockResources() {
        return deadlockResources;
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
                return getDeadlockInfo(sourceVertex);
            } else if (!neighbor.isVisited() && hasCycle(neighbor)) {
                return getDeadlockInfo(sourceVertex);
            }
        }

        sourceVertex.setBeingVisited(false);
        sourceVertex.setVisited(false);
        return false;
    }

    private boolean getDeadlockInfo(Vertex<String> sourceVertex) {
        if (sourceVertex.getType().equals("process")) {
            deadlockProcesses.add(sourceVertex.getId());
            deadlockPath.put("process", deadlockProcesses);
        } else if (sourceVertex.getType().equals("resource")) {
            deadlockResources.add(sourceVertex.getId());
            deadlockPath.put("resource", deadlockResources);
        }
        return true;
    }
}