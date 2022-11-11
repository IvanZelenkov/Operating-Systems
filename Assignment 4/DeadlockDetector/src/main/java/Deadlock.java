import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Deadlock {

    private static final RAG rag = new RAG();

    public static void main(String[] args) {
        String filename = "input3.txt";
        fileReader(filename);

        List<Vertex<String>> vertices = rag.getVertices();
        for (Vertex<String> vertex : vertices) {
            System.out.println(vertex.getId() + " : " + vertex.getType());
            List<Vertex<String>> adjacencyList = vertex.getAdjacencyList();
            for (Vertex<String> v : adjacencyList) {
                System.out.println("    |");
                System.out.println("     -- " + v.getId() + " : " + v.getType());
            }
            System.out.println();
        }
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
            if (process.isWaiting()) {
                System.out.println(process.getType() + " " + process.getId() + " is already waiting. Do nothing.");
            }
            else if (resource.isConnected()) {
                resource.addWaitingProcess(process);
                process.setWaiting(true);
                rag.addEdge(process, resource);
                System.out.println(
                        process.getType() + " " + process.getId() + " wants " +
                        resource.getType() + " " + resource.getId() + " - " +
                        process.getType() + " " + process.getId() + " must wait."
                );
            }
            else {
                System.out.println(
                        process.getType() + " " + process.getId() + " wants " +
                        resource.getType() + " " + resource.getId() + " - " +
                        resource.getType() + " " + resource.getId() + " is allocated to " +
                        process.getType() + " " + process.getId()
                );
                rag.addEdge(process, resource);
            }
        }
        else if (tokens[1].equalsIgnoreCase("r")) {
            if (!resource.getProcessesWaitingForResource().isEmpty()) {
                rag.removeEdge(process, resource);
                for (Vertex<String> vertex : resource.getProcessesWaitingForResource()) {
                    if (process.getId().equals(vertex.getId()) && process.getType().equals(vertex.getType())) {
                        if (!vertex.isConnected() && vertex.getResourcesWaitingForProcess().isEmpty()) {
                            // revert an edge
                            rag.removeEdge(vertex, resource);
                            rag.addEdge(resource, vertex);
                        }
                    }
                    System.out.println(
                            process.getType() + " " + process.getId() + " releases " +
                            resource.getType() + " " + resource.getId() + " - " +
                            resource.getType() + " " + resource.getId() + " is allocated to " +
                            vertex.getType() + " " + vertex.getId()
                    );
                    break;
                }
            }
            else {
                rag.removeEdge(process, resource);
                process.setConnected(false);
                resource.setConnected(false);
                System.out.println(
                        process.getType() + " 1111" + process.getId() + " releases " +
                                resource.getType() + " " + resource.getId() + " - " +
                                resource.getType() + " " + resource.getId() + " is allocated to " +
                                process.getType() + " " + process.getId()
                );
            }
        }
    }
}
