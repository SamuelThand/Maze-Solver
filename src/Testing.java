import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Testing {


    public static Cell[][] generateTestMaze(int rows, int cols) {
        Cell[][] maze = new Cell[rows][cols];
        var random = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = random.nextBoolean() ? Cell.TRAVERSABLE : Cell.WALL;
            }
        }

        return maze;
    }

    public static Queue<MazeTraversalStep> generateTraversalSteps(int rows, int cols) {
        Queue<MazeTraversalStep> steps = new LinkedList<>();
        Random random = new Random();

        // Generate random number of steps
        int numberOfSteps = random.nextInt((rows * cols) / 2) + 1; // At least 1 step, at most half the total number of cells

        for (int i = 0; i < numberOfSteps; i++) {
            // Generate random coordinates
            int row = random.nextInt(rows);
            int col = random.nextInt(cols);

            // Select a random Cell enum as the new state
            Cell[] cellValues = Cell.values();
            Cell newState = cellValues[random.nextInt(cellValues.length)];

            steps.add(new MazeTraversalStep(new Coordinate(row, col), null, 0, 0, newState));
        }

        return steps;
    }

}
