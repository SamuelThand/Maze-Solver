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
     * @param startCoordinate Where to start in the maze
     * @param finishCoordinate Where the goal is in the maze
     * @param greedy Run the algorithm with the priority queue sorted only based on heuristics
     * @return
     */
    public Queue<MazeTraversalStep> aStar(Coordinate startCoordinate, Coordinate finishCoordinate, boolean greedy) {

        //Tracks the search procedure, a cell location and the step of that cell location
        var mazeTraversalMap = new HashMap<Coordinate, MazeTraversalStep>();
        //The order to visit the cells

        var cellPriorityQueue = new PriorityQueue<>(
                Comparator.comparingInt(
                        greedy ? MazeTraversalStep::getHeuristicsCost : MazeTraversalStep::totalCost));

        int currentStepNumber = 0;
        var startCell = new MazeTraversalStep(currentStepNumber,
                startCoordinate,
                null,
                0,
                calculateHeuristicsCost(startCoordinate, finishCoordinate),
                Cell.START);

        mazeTraversalMap.put(startCoordinate, startCell);
        cellPriorityQueue.add(startCell);
        while (!cellPriorityQueue.isEmpty()) {
            var currentStep = cellPriorityQueue.poll();
            currentStep.setState(Cell.VISITED);
            currentStepNumber++;

            // We found the finish coordinate, build the final queue of steps, including DEAD_END steps.
            if (currentStep.getLocation().equals(finishCoordinate)) {
                var steps = new LinkedList<>(mazeTraversalMap.values());
                steps.sort(Comparator.comparingInt(MazeTraversalStep::getStepNumber));

                //Mark the steps contributing to the path with the correct state
                markPath(steps, mazeTraversalMap);

                // Traverse the visited steps in order and mark dead ends
                for (MazeTraversalStep step : steps)
                    if (step.getState() == Cell.VISITED)
                        step.setState(Cell.DEAD_END);

                return steps;
            }

            for (Coordinate neighbour : getNeighbours(currentStep.getLocation())) {
                var neighbourIsWall = maze[neighbour.row()][neighbour.col()] == Cell.WALL;
                var neighbourIsVisited = maze[neighbour.row()][neighbour.col()] == Cell.VISITED;

                if (neighbourIsWall || neighbourIsVisited)
                    continue;

                int estimatedCostToNeighbour = currentStep.getInitialCost() + 1;

                // Get the neighbour cell step or create a new traversable step if it's not mapped yet
                var neighbourCell = mazeTraversalMap.getOrDefault(neighbour, new MazeTraversalStep(
                                currentStepNumber,
                                neighbour, currentStep.getLocation(),
                                Integer.MAX_VALUE,
                                calculateHeuristicsCost(neighbour, finishCoordinate),
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
                    mazeTraversalMap.put(neighbour, neighbourCell);

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
     * Mark the cells contributing to the path as Cell.PATH
     *
     * @param steps
     * @param mazeTraversalMap
     */
    private void markPath(LinkedList<MazeTraversalStep> steps, Map<Coordinate, MazeTraversalStep> mazeTraversalMap) {
        var step = steps.getLast();
        while (step != null) {
            step.setState(Cell.PATH);
            step = mazeTraversalMap.get(step.getParentLocation());
        }
    }

    private List<Coordinate> getNeighbours(Coordinate coordinate) {
        List<Coordinate> neighbours = new ArrayList<>();
        if (coordinate.row() > 0)
            neighbours.add(new Coordinate(coordinate.row() - 1, coordinate.col()));
        if (coordinate.row() < maze.length - 1)
            neighbours.add(new Coordinate(coordinate.row() + 1, coordinate.col()));
        if (coordinate.col() > 0)
            neighbours.add(new Coordinate(coordinate.row(), coordinate.col() - 1));
        if (coordinate.col() < maze[0].length - 1)
            neighbours.add(new Coordinate(coordinate.row(), coordinate.col() + 1));
        return neighbours;
    }

    public Queue<MazeTraversalStep> dijkstra1(Coordinate start, Coordinate finish) {
        //TODO this.maze ....
        System.out.println("start: " + start);
        System.out.println("finish: " + finish);

        return Testing.generateTraversalSteps(maze.length, maze[0].length);
    }

    public Queue<MazeTraversalStep> dijkstra2(Coordinate start, Coordinate finish) {
        //TODO this.maze ....
        System.out.println("start: " + start);
        System.out.println("finish: " + finish);

        return Testing.generateTraversalSteps(maze.length, maze[0].length);
    }

}
