import java.util.*;

public class MazeSolver {

    private Cell[][] maze;

    public Cell[][] getMaze() {
        return maze;
    }

    public void setMaze(Cell[][] maze) {
        this.maze = maze;
    }

    public Queue<MazeTraversalStep> aStar(Coordinate startCoordinate, Coordinate finishCoordinate) {

        var maze = this.copyMaze();
        var mazeTraversalMap = new HashMap<Coordinate, MazeTraversalStep>();
        var visitedStepsOrder = new LinkedList<MazeTraversalStep>();
        var pathSet = new HashSet<Coordinate>();
        var cellPriorityQueue = new PriorityQueue<>(Comparator.comparingInt(MazeTraversalStep::totalCost));
        var startCell = new MazeTraversalStep(startCoordinate, null, 0, calculateHeuristicsCost(startCoordinate, finishCoordinate), Cell.START);

        mazeTraversalMap.put(startCoordinate, startCell);
        cellPriorityQueue.add(startCell);
        maze[startCoordinate.row()][startCoordinate.col()] = Cell.START;
        pathSet.add(startCoordinate);
        visitedStepsOrder.add(startCell);

        while (!cellPriorityQueue.isEmpty()) {
            var currentStep = cellPriorityQueue.poll();

            maze[currentStep.coordinate().row()][currentStep.coordinate().col()] = Cell.VISITED;

            if (currentStep.coordinate().equals(finishCoordinate)) {
                for (MazeTraversalStep step : buildPath(currentStep, mazeTraversalMap))
                    pathSet.add(step.coordinate()); //Fill the path set with the coordinates contributing to the final path
                for (MazeTraversalStep step : visitedStepsOrder) { // Traverse the visited steps in order and mark dead ends
                    Coordinate coord = step.coordinate();
                    // Mark the cell of the step as dead end if it has been visited and is not contributing to the final path
                    if (maze[coord.row()][coord.col()] == Cell.VISITED && !pathSet.contains(coord)) {
                        maze[coord.row()][coord.col()] = Cell.DEAD_END;
                        MazeTraversalStep newStep = new MazeTraversalStep(coord, step.parent(), step.initialCost(), step.heuristicsCost(), Cell.DEAD_END);
                        mazeTraversalMap.put(coord, newStep);
                    }
                }

                // Build the final queue of steps, including DEAD_END steps.
                Queue<MazeTraversalStep> finalSteps = new LinkedList<>();
                for (MazeTraversalStep step : visitedStepsOrder)
                    finalSteps.add(mazeTraversalMap.get(step.coordinate())); // Get the latest version of the step from the map.

                return finalSteps;
            }

            for (Coordinate neighbour : getNeighbours(currentStep.coordinate())) {
                var neighbourIsWall = maze[neighbour.row()][neighbour.col()] == Cell.WALL;
                var neighbourIsVisited =  maze[neighbour.row()][neighbour.col()] == Cell.VISITED;

                if (neighbourIsWall || neighbourIsVisited)
                    continue;

                int preliminaryInitialCostToNeighbourCell = currentStep.initialCost() + 1;
                var neighbourCell = mazeTraversalMap.getOrDefault(neighbour, new MazeTraversalStep(
                                neighbour, currentStep.coordinate(),
                                Integer.MAX_VALUE,
                                calculateHeuristicsCost(neighbour, finishCoordinate),
                                Cell.TRAVERSABLE));

                // A shorter path to the neighbour has been found
                if (preliminaryInitialCostToNeighbourCell < neighbourCell.initialCost()) {
                    neighbourCell = new MazeTraversalStep(
                            neighbour,
                            currentStep.coordinate(),
                            preliminaryInitialCostToNeighbourCell,
                            neighbourCell.heuristicsCost(),
                            Cell.VISITED);
                    mazeTraversalMap.put(neighbour, neighbourCell);

                    if (!cellPriorityQueue.contains(neighbourCell))
                        cellPriorityQueue.add(neighbourCell);
                        mazeTraversalMap.put(neighbour, neighbourCell);
                        visitedStepsOrder.add(neighbourCell);
                }
            }
        }

        return new LinkedList<>(); // No valid path found
    }

    public Cell[][] copyMaze() {
        Cell[][] copiedMaze = new Cell[this.maze.length][this.maze[0].length];
        for (int i = 0; i < this.maze.length; i++)
            System.arraycopy(this.maze[i], 0, copiedMaze[i], 0, this.maze[0].length);

        return copiedMaze;
    }


    /**
     * Calculate the blablabla using manhattan distance
     * @param a
     * @param b
     * @return
     */
    private int calculateHeuristicsCost(Coordinate a, Coordinate b) {
        return Math.abs(a.row() - b.row()) + Math.abs(a.col() - b.col());
    }

    private Queue<MazeTraversalStep> buildPath(MazeTraversalStep lastStep, Map<Coordinate, MazeTraversalStep> mazeTraversalMap) {
        var path = new LinkedList<MazeTraversalStep>();
        var currentStep = lastStep;
        while (currentStep != null) {
            path.addFirst(currentStep);
            currentStep = mazeTraversalMap.get(currentStep.parent());
        }
        return path;
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
