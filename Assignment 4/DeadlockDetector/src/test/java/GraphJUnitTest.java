//import org.jgrapht.alg.cycle.CycleDetector;
//import org.jgrapht.graph.DefaultEdge;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//public class GraphJUnitTest {
//
//    @DisplayName("Test if the graph is cyclic")
//    @Test
//    public void whenCheckCycles_thenDetectCycles() {
//        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<>(DirectedGraph.directedGraph);
//
//        assertFalse(cycleDetector.detectCycles());
//        Set<String> cycleVertices = cycleDetector.findCycles();
//
//        assertFalse(cycleVertices.size() > 0);
//    }
//}
