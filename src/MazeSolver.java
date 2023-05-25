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
        //TODO this.maze ....
        System.out.println("start: " + start);
        System.out.println("finish: " + finish);
        Queue<MazeTraversalStep> allSteps = new ArrayDeque<>();
        Queue<MazeTraversalStep> finalPath = new ArrayDeque<>();
        Map<Coordinate, Integer> distance = new HashMap<>();
        Map<Coordinate, Coordinate> previous = new HashMap<>();
        Queue<Coordinate> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(a -> distance.getOrDefault(a, Integer.MAX_VALUE)));
        HashMap<Coordinate, Node> graph = generateGraph(start, finish);

        for (Coordinate coordinate : graph.keySet()) {
            distance.put(coordinate, coordinate.equals(start) ? 0 : Integer.MAX_VALUE);
            priorityQueue.offer(coordinate);
        }

        while(!priorityQueue.isEmpty()) {
            Coordinate current = priorityQueue.poll();
            allSteps.add(new MazeTraversalStep(current, Cell.VISITED));

            if (current.equals(finish)) {
                break;
            }

            for (Map.Entry<Coordinate, Integer> entry : graph.get(current).neighbor().entrySet()) {
                Coordinate neighbor = entry.getKey();
                int newDist = distance.get(current) + entry.getValue();
                if (newDist < distance.get(neighbor)) {
                    distance.put(neighbor, newDist);
                    previous.put(neighbor, current);
                    priorityQueue.remove(neighbor);
                    priorityQueue.offer(neighbor);
                }
            }
            for (Coordinate pos = finish; pos != null; pos = previous.get(pos)) {
                System.out.println(pos);
                finalPath.add(new MazeTraversalStep(pos, Cell.PATH));
            }

        }

        return finalPath;


//        // DEMO of graph
//        for (Coordinate pos : graph.keySet()) {
//            allSteps.add(new MazeTraversalStep(pos, Cell.VISITED));
//            Map<Coordinate, Integer> n = graph.get(pos).neighbor();
//            for (Coordinate c: n.keySet()) {
//                allSteps.add(new MazeTraversalStep(c, Cell.DEAD_END));
//            }
//        }
//        return allSteps;
    }

    public Queue<MazeTraversalStep> dijkstra2(Coordinate start, Coordinate finish) {
        //TODO this.maze ....
        System.out.println("start: " + start);
        System.out.println("finish: " + finish);

        return Testing.generateTraversalSteps(maze.length, maze[0].length);
    }

    /**
     * Iterate over each cell in the maze and insert a node at each spot that is not part of a continuous path.
     * @param start force insertion of a node at start
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
                    if (i - 1 >= 0 && maze[i-1][j] != Cell.WALL) { // Search up
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
                    };
                    if (j - 1 >= 0 && maze[i][j-1] != Cell.WALL) { // Search left
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
                    };
                }
            }
        }
        return graph;
    }

    /**
     * Checks if the given pos is not part of a continuous path.
     * @param pos position to check
     * @param start if pos is start, return true
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

    record Node(Map<Coordinate, Integer> neighbor, Coordinate position) {};
}
