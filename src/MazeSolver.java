import java.util.*;

public class MazeSolver {

    private Cell[][] maze;

    public Cell[][] getMaze() {
        return maze;
    }

    public void setMaze(Cell[][] maze) {
        this.maze = maze;
    }

    public Queue<MazeTraversalStep> aStar(Coordinate start, Coordinate finish) { //TODO passa start och slut
        //TODO this.maze ....
        System.out.println("start: " + start);
        System.out.println("finish: " + finish);

        return Testing.generateTraversalSteps(maze.length, maze[0].length);
    }

    public Queue<MazeTraversalStep> dijkstra1(Coordinate start, Coordinate finish) {
        Queue<MazeTraversalStep> allSteps = new ArrayDeque<>();
//        Set<Coordinate> finalPath = new HashSet<>();
        Map<Coordinate, Integer> distance = new HashMap<>();
        Map<Coordinate, Coordinate> previous = new HashMap<>();
        Queue<Coordinate> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(a -> distance.getOrDefault(a, Integer.MAX_VALUE)));
        HashMap<Coordinate, Node> graph = generateGraph(start, finish);

        for (Coordinate coordinate : graph.keySet()) {
            distance.put(coordinate, coordinate.equals(start) ? 0 : Integer.MAX_VALUE);
            priorityQueue.offer(coordinate);
        }

        while (!priorityQueue.isEmpty()) {
            Coordinate current = priorityQueue.poll();
            allSteps.add(new MazeTraversalStep(current, Cell.VISITED));

            if (current.equals(finish)) {
                break;
            }

            for (Map.Entry<Coordinate, Integer> entry : graph.get(current).neighbor().entrySet()) {
                Coordinate neighbor = entry.getKey();
                int currentDistance = distance.get(current);
                int neighborDistance = distance.get(neighbor);
                int newDistance = currentDistance + entry.getValue();

                if (newDistance < neighborDistance) {
                    distance.put(neighbor, newDistance);
                    previous.put(neighbor, current);
                    // Distance is changed, update position in queue
                    priorityQueue.remove(neighbor);
                    priorityQueue.offer(neighbor);
                }
            }
        }

//        // Generate final path by backtracking from finish to start
//        if (previous.containsKey(finish)) {
//            Coordinate pos = finish;
//            while (pos != null) {
//                allSteps.add(new MazeTraversalStep(pos, Cell.PATH));
//                pos = previous.get(pos);
//            }
//        }

        // Generate final path by backtracking from finish to start
        if (previous.containsKey(finish)) {
            connectFinishingPath(finish, allSteps, previous);
        }


        return allSteps;
    }

    private static void connectFinishingPath(Coordinate finish, Queue<MazeTraversalStep> allSteps, Map<Coordinate, Coordinate> previous) {
        Coordinate pos = finish;
        while (pos != null) {
            // Generate MazeTraversalStep for each coordinate in the final path
            Coordinate newPos = previous.get(pos);
            if (newPos != null) {
                // Determine the direction of movement
                if (newPos.row() == pos.row()) { // Moving horizontally
                    int prevCol = pos.col();
                    int newCol = newPos.col();
                    if (prevCol < newCol) {
                        // Moving right
                        for (int col = prevCol; col <= newCol; col++) {
                            allSteps.add(new MazeTraversalStep(new Coordinate(pos.row(), col), Cell.PATH));
                        }
                    } else {
                        // Moving left
                        for (int col = prevCol; col >= newCol; col--) {
                            allSteps.add(new MazeTraversalStep(new Coordinate(pos.row(), col), Cell.PATH));
                        }
                    }
                } else { // Moving vertically
                    int prevRow = pos.row();
                    int newRow = newPos.row();
                    if (prevRow < newRow) {
                        // Moving down
                        for (int row = prevRow; row <= newRow; row++) {
                            allSteps.add(new MazeTraversalStep(new Coordinate(row, pos.col()), Cell.PATH));
                        }
                    } else {
                        // Moving up
                        for (int row = prevRow; row >= newRow; row--) {
                            allSteps.add(new MazeTraversalStep(new Coordinate(row, pos.col()), Cell.PATH));
                        }
                    }
                }
            }
            pos = newPos;
        }
    }

    private Queue<MazeTraversalStep> genereateAllCellSteps(Queue<MazeTraversalStep> allSteps, Set<Coordinate> finalPath) {
        // TODO: The steps are not in order, they are added in the order they are generated
        Queue<MazeTraversalStep> mergedSteps = new ArrayDeque<>();
        Coordinate prev = allSteps.poll().coordinate();
        MazeTraversalStep step = allSteps.poll();
        while (!allSteps.isEmpty()) {
            Cell type = finalPath.contains(step.coordinate()) ? Cell.PATH : Cell.VISITED;
            // Generate MazeTraversalStep for each cell between prev and step.coordinate()
            if (step.coordinate().row() == prev.row()) {
                if (step.coordinate().col() < prev.col()) {
                    for (int i = step.coordinate().col(); i < prev.col(); i++) {
                        mergedSteps.add(new MazeTraversalStep(new Coordinate(step.coordinate().row(), i), type));
                    }
                } else {
                    for (int i = prev.col(); i < step.coordinate().col(); i++) {
                        mergedSteps.add(new MazeTraversalStep(new Coordinate(step.coordinate().row(), i), type));
                    }
                }
            } else {
                if (step.coordinate().row() < prev.row()) {
                    for (int i = step.coordinate().row(); i < prev.row(); i++) {
                        mergedSteps.add(new MazeTraversalStep(new Coordinate(i, step.coordinate().col()), type));
                    }
                } else {
                    for (int i = prev.row(); i < step.coordinate().row(); i++) {
                        mergedSteps.add(new MazeTraversalStep(new Coordinate(i, step.coordinate().col()), type));
                    }
                }
            }
            mergedSteps.add(step);
            prev = step.coordinate();
            step = allSteps.poll();
        }
        return mergedSteps;
    }

    public Queue<MazeTraversalStep> dijkstra2(Coordinate start, Coordinate finish) {
        //TODO this.maze ....
        System.out.println("start: " + start);
        System.out.println("finish: " + finish);

        return Testing.generateTraversalSteps(maze.length, maze[0].length);
    }

    /**
     * Iterate over each cell in the maze and insert a node at each spot that is not part of a continuous path.
     *
     * @param start  force insertion of a node at start
     * @param finish force insertion of a node at finish
     * @return graph representation of the maze
     */
    private HashMap<Coordinate, Node> generateGraph(Coordinate start, Coordinate finish) {
        HashMap<Coordinate, Node> graph = new HashMap<>();
        int rows = this.maze.length;
        int cols = this.maze[0].length;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Coordinate current = new Coordinate(i, j);
                if (maze[i][j] == Cell.WALL) {
                    continue;
                }
                if (shouldStoreNode(current, start, finish)) {
                    Map<Coordinate, Integer> neighbors = new HashMap<>();
                    Node node = new Node(neighbors, current);
                    graph.put(current, node);
                    if (i - 1 >= 0 && maze[i - 1][j] != Cell.WALL) { // Search up
                        int offset = 1;
                        while (i - offset >= 0 && maze[i - offset][j] != Cell.WALL) {
                            Coordinate searchPos = new Coordinate(i - offset, j);
                            if (graph.containsKey(searchPos)) {
                                neighbors.put(searchPos, offset);
                                graph.get(searchPos).neighbor.put(current, offset);
                                break;
                            }
                            offset++;
                        }
                    }
                    ;
                    if (j - 1 >= 0 && maze[i][j - 1] != Cell.WALL) { // Search left
                        int offset = 1;
                        while (j - offset >= 0 && maze[i][j - offset] != Cell.WALL) {
                            Coordinate searchPos = new Coordinate(i, j - offset);
                            if (graph.containsKey(searchPos)) {
                                neighbors.put(searchPos, offset);
                                graph.get(searchPos).neighbor.put(current, offset);
                                break;
                            }
                            offset++;
                        }
                    }
                    ;
                }
            }
        }
        return graph;
    }

    /**
     * Checks if the given pos is not part of a continuous path.
     *
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

    record Node(Map<Coordinate, Integer> neighbor, Coordinate position) {
    }

    ;
}
