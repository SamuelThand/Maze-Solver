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
        System.out.println("start: " + startCoordinate);
        System.out.println("finish: " + finishCoordinate);

        Map<Coordinate, MazeTraversalStep> mazeTraversalMap = new HashMap<>();
        var openSet = new PriorityQueue<>(Comparator.comparingInt(MazeTraversalStep::totalCost));
        var startCell = new MazeTraversalStep(startCoordinate, null, 0, calculateHeuristicsCost(startCoordinate, finishCoordinate), Cell.START);

        mazeTraversalMap.put(startCoordinate, startCell);
        openSet.add(startCell);
        while (!openSet.isEmpty()) {
            var currentStep = openSet.poll();
            maze[currentStep.coordinate().row()][currentStep.coordinate().col()] = Cell.VISITED;

            if (currentStep.coordinate().equals(finishCoordinate))
                return buildPath(currentStep, mazeTraversalMap);

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
                            Cell.TRAVERSABLE);

                    mazeTraversalMap.put(neighbour, neighbourCell);
                    if (!openSet.contains(neighbourCell))
                        openSet.add(neighbourCell);

                }
            }
        }

        return new LinkedList<>(); // No valid path found
//        return Testing.generateTraversalSteps(maze.length, maze[0].length);
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

//    private Queue<MazeTraversalStep> buildPath(MazeTraversalStep finishNode, Map<Coordinate, MazeTraversalStep> mazeTraversalMap) {
//        Deque<MazeTraversalStep> path = new ArrayDeque<>();
//        MazeTraversalStep current = finishNode;
//        while (current != null) {
//            path.addFirst(current);
//            current = mazeTraversalMap.get(current.parent());
//        }
//        return new LinkedList<>(path);
//    }

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
