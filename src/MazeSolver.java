import java.util.*;

public class MazeSolver {

    private Cell[][] maze;

    public Cell[][] getMaze() {
        return maze;
    }

    public void setMaze(Cell[][] maze) {
        this.maze = maze;
    }

    /**
     * Solve the maze using a greedy/normal version of the A* algorithm.
     *
     * @param start Where to start in the maze
     * @param goal Where the goal is in the maze
     * @param greedy Run the algorithm with the priority queue sorted only based on heuristics
     * @return The results of the search
     */
    public Queue<MazeTraversalStep> aStar(Coordinate start, Coordinate goal, boolean greedy) {

        var procedure = new HashMap<Coordinate, MazeTraversalStep>();
        var cellPriorityQueue = new PriorityQueue<>( //The order to visit the cells
                Comparator.comparingInt(greedy ? MazeTraversalStep::getHeuristicsCost : MazeTraversalStep::totalCost));
        int currentStepNumber = 0;
        var startCell = new MazeTraversalStep(
                currentStepNumber,
                start,
                null,
                0,
                calculateHeuristicsCost(start, goal),
                Cell.START);

        procedure.put(start, startCell);
        cellPriorityQueue.add(startCell);
        while (!cellPriorityQueue.isEmpty()) {
            var currentStep = cellPriorityQueue.poll();
            currentStep.setState(Cell.VISITED);
            currentStepNumber++;

            if (currentStep.getLocation().equals(goal)) // We found the goal coordinate
                return parseResult(procedure);

            for (Coordinate neighbour : getNeighbours(currentStep.getLocation())) { // Parse all neighbours
                var neighbourIsWall = maze[neighbour.row()][neighbour.col()] == Cell.WALL;
                var neighbourIsVisited = maze[neighbour.row()][neighbour.col()] == Cell.VISITED;
                if (neighbourIsWall || neighbourIsVisited)
                    continue;

                int estimatedCostToNeighbour = currentStep.getInitialCost() + 1;
                // Get the neighbour cell step or create a new traversable step if it's not mapped yet
                var neighbourCell = procedure.getOrDefault(neighbour, new MazeTraversalStep(
                                currentStepNumber,
                                neighbour, currentStep.getLocation(),
                                Integer.MAX_VALUE,
                                calculateHeuristicsCost(neighbour, goal),
                                Cell.TRAVERSABLE));

                // A shorter path to the neighbour has been found
                if (estimatedCostToNeighbour < neighbourCell.getInitialCost()) {
                    neighbourCell = new MazeTraversalStep(
                            currentStepNumber,
                            neighbour,
                            currentStep.getLocation(),
                            estimatedCostToNeighbour,
                            neighbourCell.getHeuristicsCost(),
                            Cell.TRAVERSABLE);
                    procedure.put(neighbour, neighbourCell);

                    // The neighbour has not been visited
                    if (!cellPriorityQueue.contains(neighbourCell))
                        cellPriorityQueue.add(neighbourCell);
                }
            }
        }

        return new LinkedList<>(); // No valid path found
    }

    /**
     * Calculate the manhattan distance from the start coordinate to the goal coordinate
     * @param start The starting coordinate
     * @param goal The goal coordinate
     *
     * @return The manhattan distance between two coordinates
     */
    private int calculateHeuristicsCost(Coordinate start, Coordinate goal) {
        return Math.abs(start.row() - goal.row()) + Math.abs(start.col() - goal.col());
    }


    /**
     * Parse the results of the algorithm
     *
     * @param procedure The search procedure
     * @return Parsed steps of the algorithm
     */
    private LinkedList<MazeTraversalStep> parseResult(Map<Coordinate, MazeTraversalStep> procedure) {
        var steps = new LinkedList<>(procedure.values());
        steps.sort(Comparator.comparingInt(MazeTraversalStep::getStepNumber));
        markCells(steps, procedure);

        return steps;
    }

    /**
     * Mark the visited cells contributing to the path as Cell.PATH, and all others as Cell.DEAD_END
     *
     * @param steps The steps of the algorithm
     * @param procedure The search procedure
     */
    private void markCells(LinkedList<MazeTraversalStep> steps, Map<Coordinate, MazeTraversalStep> procedure) {
        var pathStep = steps.getLast();
        while (pathStep != null) {
            pathStep.setState(Cell.PATH);
            pathStep = procedure.get(pathStep.getParentLocation());
        }
        for (MazeTraversalStep step : steps)
            if (step.getState() == Cell.VISITED)
                step.setState(Cell.DEAD_END);
    }

    /**
     * Extract the location of all neighbours
     *
     * @param location Coordinate to find neighbours for
     * @return List of all neighbours
     */
    private List<Coordinate> getNeighbours(Coordinate location) {
        List<Coordinate> neighbours = new ArrayList<>();
        if (location.row() > 0)
            neighbours.add(new Coordinate(location.row() - 1, location.col()));
        if (location.row() < maze.length - 1)
            neighbours.add(new Coordinate(location.row() + 1, location.col()));
        if (location.col() > 0)
            neighbours.add(new Coordinate(location.row(), location.col() - 1));
        if (location.col() < maze[0].length - 1)
            neighbours.add(new Coordinate(location.row(), location.col() + 1));
        return neighbours;
    }

}
