
//
import java.time.Duration;
import java.time.Instant;
public class Main {
    public static void main(String[] args) {
        // Пример графа в виде матрицы смежности
        int[][] adjacencyMatrix = {
                {0, 1, 1, 0},  // Вершина 0 соединена с 1 и 2
                {1, 0, 1, 1},  // Вершина 1 соединена с 0, 2 и 3
                {1, 1, 0, 0},  // Вершина 2 соединена с 0 и 1
                {0, 1, 0, 0}   // Вершина 3 соединена с 1
        };

        Instant startTime = Instant.now();
        int edgeCount = countEdges(adjacencyMatrix);
        Instant endTime = Instant.now();
        System.out.println("Total edge count in graph: " + edgeCount);
        System.out.println("Time = " + Duration.between(startTime, endTime).toMillis());
    }
    public static int countEdges(int[][] adjacencyMatrix) {
        int count = 0;
        int n = adjacencyMatrix.length;

        // Проходим по верхней треугольной части матрицы
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (adjacencyMatrix[i][j] == 1) {
                    count++;
                }
            }
        }
        return count;
    }

}
