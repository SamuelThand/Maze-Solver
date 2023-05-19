import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Testing {


    public static Cell[][] generateTestMaze(int size) {
        Cell[][] maze = new Cell[size][size];
        var random = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                maze[i][j] = random.nextBoolean() ? Cell.TRAVERSABLE : Cell.WALL;
            }
        }

        return maze;
    }

    public static Queue<MazeTraversalStep> generateTraversalSteps(int size) {
        Queue<MazeTraversalStep> steps = new LinkedList<>();
        Random random = new Random();

        // Generate random number of steps
        int numberOfSteps = random.nextInt((size * size) / 2) + 1; // At least 1 step, at most half the total number of cells

        for (int i = 0; i < numberOfSteps; i++) {
            // Generate random coordinates
            int row = random.nextInt(size);
            int col = random.nextInt(size);

            // Select a random Cell enum as the new state
            Cell[] cellValues = Cell.values();
            Cell newState = cellValues[random.nextInt(cellValues.length)];

            steps.add(new MazeTraversalStep(row, col, newState));
        }

        return steps;
    }

}
