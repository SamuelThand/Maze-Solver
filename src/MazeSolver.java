import org.w3c.dom.Node;

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
     * Time complexity: Worst case = O(N log N), Best case = O(1)
     *
     * @param start  Where to start in the maze
     * @param goal   Where the goal is in the maze
     * @param greedy Run the algorithm with the priority queue sorted only based on heuristics
     * @return The results of the search
     */
    public Queue<MazeTraversalStep> aStar(Coordinate start, Coordinate goal, boolean greedy) {

        var procedure = new HashMap<Coordinate, MazeTraversalStep>();
        var cellPriorityQueue = new PriorityQueue<>( //The order to process the cells
                Comparator.comparingInt(greedy ? MazeTraversalStep::getHeuristicsCost : MazeTraversalStep::totalCost));
        int currentStepNumber = 0;
        var startCell = new MazeTraversalStep(
                currentStepNumber,
                start,
                null,
                0,
                calculateHeuristicsCost(start, goal),
                Cell.START);

        procedure.put(start, startCell); // O(1)
        cellPriorityQueue.add(startCell); // O(1)
        while (!cellPriorityQueue.isEmpty()) { // O(N)

            currentStepNumber++;
            var currentStep = cellPriorityQueue.poll(); // O(log N)
            currentStep.setState(Cell.VISITED);

            // We found the goal coordinate
            if (currentStep.getLocation().equals(goal)) {
                currentStep.setStepNumber(currentStepNumber);
                procedure.put(currentStep.getLocation(), currentStep);
                return parseResult(procedure);
            }

            // Process all neighbours
            for (Coordinate neighbour : getNeighbours(currentStep.getLocation())) { // O(4)

                if (maze[neighbour.row()][neighbour.col()] == Cell.WALL)
                    continue;

                int estimatedCostToNeighbour = currentStep.getInitialCost() + 1;
                // Get the neighbour cell step or create a new traversable step if it's not mapped yet
                var neighbourCell = procedure.getOrDefault(neighbour, new MazeTraversalStep(
                        currentStepNumber,
                        neighbour,
                        currentStep.getLocation(),
                        Integer.MAX_VALUE,
                        calculateHeuristicsCost(neighbour, goal),
                        Cell.TRAVERSABLE));

                // A shorter path to the neighbour has been found
                if (estimatedCostToNeighbour < neighbourCell.getInitialCost()) {
                    neighbourCell.setInitialCost(estimatedCostToNeighbour);
                    procedure.put(neighbour, neighbourCell);

                    // The neighbour has not been visited, queue it for processing
                    if (!cellPriorityQueue.contains(neighbourCell))
                        cellPriorityQueue.add(neighbourCell); // O(log N)
                }
            }
        }

        return new LinkedList<>(); // No valid path found
    }

    /**
     * Calculate the manhattan distance from the start coordinate to the goal coordinate
     *
     * @param start The starting coordinate
     * @param goal  The goal coordinate
     * @return The manhattan distance between two coordinates
     */
    private int calculateHeuristicsCost(Coordinate start, Coordinate goal) {
        return Math.abs(start.row() - goal.row()) + Math.abs(start.col() - goal.col());
    }


    /**
     * Parse the results of the algorithm
     *
     * Time complexity: O(N log N)
     *
     * @param procedure The search procedure
     * @return Parsed steps of the algorithm
     */
    private LinkedList<MazeTraversalStep> parseResult(Map<Coordinate, MazeTraversalStep> procedure) {
        var steps = new LinkedList<>(procedure.values()); // O(N)
        steps.sort(Comparator.comparingInt(MazeTraversalStep::getStepNumber)); // O(N log N)
        markCells(steps, procedure);

        return steps;
    }

    /**
     * Mark the visited cells contributing to the path as Cell.PATH, and all others as Cell.DEAD_END
     *
     * @param steps     The steps of the algorithm
     * @param procedure The search procedure
     */
    private void markCells(LinkedList<MazeTraversalStep> steps, Map<Coordinate, MazeTraversalStep> procedure) {
        var pathStep = steps.getLast();
        while (pathStep != null) { // O(N)
            pathStep.setState(Cell.PATH);
            pathStep = procedure.get(pathStep.getParentLocation());
        }
        for (MazeTraversalStep step : steps) // O(N)
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

    /**
     * Dijkstra's algorithm using a priority queue and graph.
     * Time complexity: O((v+e) log v)
     * Where v is vertices, e is edges.
     * @param start  Coordinate to start at
     * @param finish Coordinate to finish at
     * @return A queue containing all steps taken to find the final path and each cell traversed in the final path.
     */
    public Queue<MazeTraversalStep> dijkstra1(Coordinate start, Coordinate finish) {
        Queue<MazeTraversalStep> allSteps = new ArrayDeque<>();
        // Map to store total cost/weight/distance of all searched nodes.
        // The Coordinate is one of the nodes and the integer is the distance traveled from start to that node in a
        // straight path.
        Map<Coordinate, Integer> distance = new HashMap<>();
        Map<Coordinate, Coordinate> previous = new HashMap<>(); // Map containing the path taken between nodes
        // PriorityQueue used to keep track of next least expensive path to take.
        Queue<Coordinate> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(a -> distance.getOrDefault(a, Integer.MAX_VALUE)));
        HashMap<Coordinate, Node> graph = generateGraph(start, finish); // Weighed graph of the maze. Time complexity: O(v)

        // Initialize distance to all nodes to infinity, except for start node which is 0
        for (Coordinate coordinate : graph.keySet()) { // Time complexity: O(v)
            distance.put(coordinate, coordinate.equals(start) ? 0 : Integer.MAX_VALUE);
            if (coordinate.equals(start)) {
                priorityQueue.offer(coordinate);
            }
        }

        while (!priorityQueue.isEmpty()) { // O((e+v) log v)
            Coordinate current = priorityQueue.poll(); // O(log v)
            if (markAndStoreStep(start, finish, allSteps, current)) {
                break;
            }

            // Loop through all neighbors of current node
            for (Map.Entry<Coordinate, Integer> entry : graph.get(current).neighbor().entrySet()) { // O(e)
                Coordinate neighbor = entry.getKey();
                int currentDistance = distance.get(current);
                int neighborDistance = distance.get(neighbor);
                int newDistance = currentDistance + entry.getValue();
                // If new distance is less than the current distance, update the distance and previous node
                if (newDistance < neighborDistance) {
                    distance.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    priorityQueue.offer(neighbor); // O(log v)
                }
            }
        }

        // Generate final path by backtracking from finish to start
        if (previous.containsKey(finish)) {
            connectFinishingPath(finish, allSteps, previous); // Time complexity: O(v)
        }
        return allSteps;
    }

    /**
     * Dijkstra's algorithm using an arrayList.
     * Time complexity: O(v^2+e)
     * Where v is vertices and e is edges.
     * @param start  Coordinate to start at
     * @param finish Coordinate to finish at
     * @return A queue containing all steps taken to find the final path and each cell traversed in the final path.
     */
    public Queue<MazeTraversalStep> dijkstra2(Coordinate start, Coordinate finish) {
        Queue<MazeTraversalStep> allSteps = new ArrayDeque<>();
        // Map to store total cost/weight/distance of all searched nodes.
        // The Coordinate is one of the nodes and the integer is the distance traveled from start to that node in a
        // straight path.
        Map<Coordinate, Integer> distance = new HashMap<>();
        Map<Coordinate, Coordinate> previous = new HashMap<>(); // Map containing the path taken between nodes
        List<Coordinate> nodeList = new ArrayList<>(); // List containing nodes
        HashMap<Coordinate, Node> graph = generateGraph(start, finish); // O(v)

        // Initialize distance to all nodes to infinity, except for start node which is 0
        // Also add all nodes to list
        for (Coordinate coordinate : graph.keySet()) { // O(v)
            distance.put(coordinate, coordinate.equals(start) ? 0 : Integer.MAX_VALUE);
            nodeList.add(coordinate);
        }

        while (!nodeList.isEmpty()) { // O(v^2+e)
            Coordinate current = null;
            int smallestDistance = Integer.MAX_VALUE;

            // Find node with the smallest distance
            for (Coordinate node : nodeList) { // O(v)
                int nodeDistance = distance.get(node);
                if (nodeDistance < smallestDistance) {
                    smallestDistance = nodeDistance;
                    current = node;
                }
            }
            nodeList.remove(current); // O(v)

            if (markAndStoreStep(start, finish, allSteps, current)) {
                break;
            }

            // Loop through all neighbors of current node
            for (Map.Entry<Coordinate, Integer> entry : graph.get(current).neighbor().entrySet()) { // O(e)
                Coordinate neighbor = entry.getKey();
                int currentDistance = distance.get(current);
                int neighborDistance = distance.get(neighbor);
                int newDistance = currentDistance + entry.getValue();

                if (newDistance < neighborDistance) {
                    distance.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                }
            }
        }

        // Generate final path by backtracking from finish to start
        if (previous.containsKey(finish)) {
            connectFinishingPath(finish, allSteps, previous); // O(v)
        }
        return allSteps;
    }

    /**
     * Stores the current step with state based on location. Start and finish is will still be marked START/FINISH.
     * Time complexity: O(1)
     * @param start start coordinate
     * @param finish finish coordinate
     * @param allSteps queue to add the step
     * @param current current coordinate to include in step
     * @return true if current is equal to finish, else false.
     */
    private boolean markAndStoreStep(Coordinate start, Coordinate finish, Queue<MazeTraversalStep> allSteps, Coordinate current) {
        MazeTraversalStep step;
        if (current.equals(finish)) {
            step = new MazeTraversalStep(current, Cell.FINISH);
            allSteps.add(step);
            return true;
        } else if (current.equals(start)) {
            step = new MazeTraversalStep(current, Cell.START);
        } else {
            step = new MazeTraversalStep(current, Cell.VISITED);
        }
        allSteps.add(step);
        return false;
    }

    /**
     * Add all steps traveled from finish to start to the allSteps queue.
     * Time complexity: O(nm)
     * Where n is the height of the maze and m is the width of the maze.
     * @param finish   finish coordinate
     * @param allSteps queue to add the steps to
     * @param previous map containing the path taken between nodes (in order to backtrack from finish to start)
     */
    private static void connectFinishingPath(Coordinate finish, Queue<MazeTraversalStep> allSteps, Map<Coordinate, Coordinate> previous) {
        Coordinate pos = finish;
        while (pos != null) { // Time complexity: O(nm)
            // Generate MazeTraversalStep for each coordinate in the final path
            Coordinate newPos = previous.get(pos);
            if (newPos != null) {
                // Determine the direction of movement
                if (newPos.row() == pos.row()) { // Moving horizontally
                    int prevCol = pos.col();
                    int newCol = newPos.col();
                    if (prevCol < newCol) {
                        // Moving right
                        for (int col = prevCol; col <= newCol; col++) { // Time complexity: O(m)
                            allSteps.add(new MazeTraversalStep(new Coordinate(pos.row(), col), Cell.PATH));
                        }
                    } else {
                        // Moving left
                        for (int col = prevCol; col >= newCol; col--) { // Time complexity: O(m)
                            allSteps.add(new MazeTraversalStep(new Coordinate(pos.row(), col), Cell.PATH));
                        }
                    }
                } else { // Moving vertically
                    int prevRow = pos.row();
                    int newRow = newPos.row();
                    if (prevRow < newRow) {
                        // Moving down
                        for (int row = prevRow; row <= newRow; row++) { // Time complexity: O(n)
                            allSteps.add(new MazeTraversalStep(new Coordinate(row, pos.col()), Cell.PATH));
                        }
                    } else {
                        // Moving up
                        for (int row = prevRow; row >= newRow; row--) { // Time complexity: O(n)
                            allSteps.add(new MazeTraversalStep(new Coordinate(row, pos.col()), Cell.PATH));
                        }
                    }
                }
            }
            pos = newPos;
        }
    }


    /**
     * Iterate over each cell in the maze and insert a node at each spot that is not part of a continuous path.
     * Time complexity: O(n(n+m))
     * Where n is the height of the maze and m is the width of the maze.
     * @param start  force insertion of a node at start
     * @param finish force insertion of a node at finish
     * @return graph representation of the maze
     */
    private HashMap<Coordinate, Node> generateGraph(Coordinate start, Coordinate finish) {
        Coordinate current;
        HashMap<Coordinate, Node> graph = new HashMap<>();
        int rows = this.maze.length;
        int cols = this.maze[0].length;
        for (int i = 0; i < rows; i++) { // Time complexity: O(n(n+m))
            for (int j = 0; j < cols; j++) { // Time complexity: O(n+m)
                if (maze[i][j] == Cell.WALL) {
                    continue;
                }
                current = new Coordinate(i, j);
                if (shouldStoreNode(current, start, finish)) {
                    Map<Coordinate, Integer> neighbors = new HashMap<>();
                    Node node = new Node(neighbors, current);
                    graph.put(current, node);
                    if (i - 1 >= 0 && maze[i - 1][j] != Cell.WALL) { // Search up
                        int offset = 1;
                        while (i - offset >= 0 && maze[i - offset][j] != Cell.WALL) { // Time complexity: O(n)
                            Coordinate searchPos = new Coordinate(i - offset, j);
                            if (graph.containsKey(searchPos)) {
                                neighbors.put(searchPos, offset);
                                graph.get(searchPos).neighbor.put(current, offset);
                                break;
                            }
                            offset++;
                        }
                    }
                    if (j - 1 >= 0 && maze[i][j - 1] != Cell.WALL) { // Search left
                        int offset = 1;
                        while (j - offset >= 0 && maze[i][j - offset] != Cell.WALL) { // Time complexity: O(m)
                            Coordinate searchPos = new Coordinate(i, j - offset);
                            if (graph.containsKey(searchPos)) {
                                neighbors.put(searchPos, offset);
                                graph.get(searchPos).neighbor.put(current, offset);
                                break;
                            }
                            offset++;
                        }
                    }
                }
            }
        }
        return graph;
    }

    /**
     * Checks if the given pos is not part of a continuous path.
     * Time complexity: O(1)
     * @param pos    position to check
     * @param start  if pos is start, return true
     * @param finish if pos is finish, return true
     * @return true if a node should be inserted for this position, otherwise false
     */
    private boolean shouldStoreNode(Coordinate pos, Coordinate start, Coordinate finish) {
        if (pos.equals(start) || pos.equals(finish)) { // pos is at start or finish
            return true;
        } else if (pos.row() == 0 || pos.row() == maze.length - 1) { // pos is at the top or bottom edge
            return true;
        } else if (pos.col() == 0 || pos.col() == maze[0].length - 1) { // pos is at the left or right edge
            return true;
        }
        Cell up = maze[Math.max(0, pos.row() - 1)][pos.col()];
        Cell down = maze[Math.min(maze.length - 1, pos.row() + 1)][pos.col()];
        Cell left = maze[pos.row()][Math.max(0, pos.col() - 1)];
        Cell right = maze[pos.row()][Math.min(maze[0].length - 1, pos.col() + 1)];

        // pos is part of horizontal continuous path
        boolean horizontalPath = (left == Cell.TRAVERSABLE && right == Cell.TRAVERSABLE) && (up == Cell.WALL && down == Cell.WALL);
        // pos is part of vertical continuous path
        boolean verticalPath = (up == Cell.TRAVERSABLE && down == Cell.TRAVERSABLE) && (left == Cell.WALL && right == Cell.WALL);

        return !horizontalPath && !verticalPath;
    }

    record Node(Map<Coordinate, Integer> neighbor, Coordinate position) {}

}
