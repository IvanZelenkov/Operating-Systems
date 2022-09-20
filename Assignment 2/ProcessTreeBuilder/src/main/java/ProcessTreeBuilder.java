import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Ivan Zelenkov
 */
public class ProcessTreeBuilder {
    private static NaryTree<Long> naryTree;
    private static Node<Long> root;
    private static JSONArray processes;
    private static double startTime;

    /**
     * The method reads data from a file, checks if each value is an integer or a string, and writes the specified output to the same file in the same format
     */
    private static boolean readFile(String filename) throws IOException {
        boolean isSuccess = false;
        Path path = Paths.get("");
        JSONParser parser = new JSONParser();
        try {
            processes = (JSONArray) parser.parse(new FileReader(String.format(path.toAbsolutePath() + "/src/main/java/%s.json", filename)));
            startTime = System.currentTimeMillis();
            isSuccess = true;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.print("Invalid file name, please try again.\n>>> ");
        }
        return isSuccess;
    }

    private static void buildTree(GraphvizBuilder graphvizBuilder, JSONArray processes) {
        for (int i = 0; i < processes.size(); i++) {
            JSONObject jsonObject = (JSONObject) processes.get(i);
            Node<Long> parent = null;
            Node<Long> currentNode;
            if (i == 0) {
                root = new Node<>((Long) jsonObject.get("level"), (Long) jsonObject.get("pid"), (Long) jsonObject.get("ppid"));
                naryTree = new NaryTree<>(root);
                currentNode = root;
            } else {
                if (naryTree.exists((Long) jsonObject.get("ppid"))) {
                    parent = naryTree.findNode(root, (Long) jsonObject.get("ppid"));
                    currentNode = new Node<>((Long) jsonObject.get("level"), (Long) jsonObject.get("pid"), (Long) jsonObject.get("ppid"));
                    parent.addChild(currentNode);
                } else {
                    currentNode = new Node<>((Long) jsonObject.get("level"), (Long) jsonObject.get("pid"), (Long) jsonObject.get("ppid"));
                    root.addChild(currentNode);
                }
            }
            graphvizBuilder.addLabeledNode(currentNode, "Level: " + currentNode.getLevel() + "\nPID: " + currentNode.getPid() + "\nPPID: " + currentNode.getPpid());
            if (parent != null)
                graphvizBuilder.addEdge(parent, currentNode);
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter a filename to read.\n>>> ");
        String filename = reader.readLine();
        System.out.println();

        while (!readFile(filename)) {
            filename = reader.readLine();
            System.out.println();
        }

        GraphvizBuilder graphvizBuilder = new GraphvizBuilder();
        buildTree(graphvizBuilder, processes);
        double endTime = System.currentTimeMillis();
        double executionTotalTime = (endTime - startTime) / 1000;
        System.out.println("Execution time: " + executionTotalTime + " seconds");
        graphvizBuilder.toFileUndirected(filename, "ordering=\"out\"");
    }
}
