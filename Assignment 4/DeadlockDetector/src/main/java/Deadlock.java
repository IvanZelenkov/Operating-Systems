import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Deadlock {

    private static final RAG rag = new RAG();

    public static void main(String[] args) {
        String filename = "input1.txt";
        GraphvizBuilder graphvizBuilder = new GraphvizBuilder();
        fileReader(graphvizBuilder, filename);
        graphvizBuilder.toFileUndirected("output.png", "ordering=\"out\"");
    }

    private static void fileReader(GraphvizBuilder graphvizBuilder, String filename) {
        Path path = Paths.get("");
        String line;
        try (BufferedReader reader = new BufferedReader(new FileReader(String.format(path.toAbsolutePath() + "/src/main/resources/" + filename, filename)))) {
            while ((line = reader.readLine()) != null) {
                construct(graphvizBuilder, line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: file not found.");
        } catch (IOException e) {
            System.out.println("Error: operating system can't complete the appropriate Input/Output.");
        }
    }

    public static void construct(GraphvizBuilder graphvizBuilder, String line) {
        String[] tokens = line.trim().split("\\s+");

        Vertex<String> newVertex1 = new Vertex<>(tokens[0], "process");
        Vertex<String> newVertex2 = new Vertex<>(tokens[2], "resource");

        // if there is 
        Vertex<String> check = rag.hasVertex(newVertex1);
        if (check == null) {
            rag.addVertex(newVertex1);
            graphvizBuilder.addLabeledNode(newVertex1, "ID: " + newVertex1.getId() + "\nType: " + newVertex1.getType());
        } else {
            newVertex1 = check;
        }

        check = rag.hasVertex(newVertex2);
        if (check == null) {
            rag.addVertex(newVertex2);
            graphvizBuilder.addLabeledNode(newVertex2, "ID: " + newVertex2.getId() + "\nType: " + newVertex2.getType());
        } else {
            newVertex2 = check;
        }

        if (tokens[1].equalsIgnoreCase("w")) {
            System.out.println("Process " + newVertex1.getId() + " wants resource " + newVertex2.getId() + " - Resource " + newVertex2.getId() + " is allocated to process " + newVertex1.getId());
            rag.addEdge(newVertex1, newVertex2);
            graphvizBuilder.addEdge(newVertex1, newVertex2);
        }
        else if (tokens[1].equalsIgnoreCase("r")) {
            System.out.println("Process " + newVertex1.getId() + " releases resource " + newVertex2.getId() + " - Resource " + newVertex2.getId() + " is allocated to process " + newVertex1.getId());
            rag.removeEdge(newVertex1, newVertex2);
        }
    }
}
