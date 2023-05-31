import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that loads a maze image and converts it into a scaled down 2D array representation of the maze.
 */
public class MazeLoader {

    private static final int WALL_COLOR = -16000000;

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    /**
     * Load the maze image into a 2D array representation of the maze.
     * The image should be jpg and the maze should be black and white, where black is a wall and white is a path.
     * The maze is created in three steps:
     * 1. Remove the white border around the maze.
     * 2. Create a full scale 2D array representation of the maze.
     * 3. Reduce the maze to a compressed version where the path and wall is only one cell wide.
     * Time complexity heavily relies on the size of the image.
     * Time complexity: O(nm)
     * Where n is the height of the maze and m is the width of the maze.
     * @param mazeImage image of the maze
     * @return 2D array of cells
     */
    public Cell[][] loadMaze(File mazeImage) {
        BufferedImage bImage = this.processImage(mazeImage);
        int pathSize = findSmallestContinuousWhite(bImage); // Time complexity: O(n+m)
        int width = bImage.getWidth();
        int height = bImage.getHeight();

        Cell[][] maze = new Cell[height][width];

        // Create an array of cells that represents the full scale maze.
        for (int y = 0; y < height; y++) { // Time complexity: O(nm)
            for (int x = 0; x < width; x++) { // Time complexity: O(n)
                maze[y][x] = isNotWall(bImage.getRGB(x, y)) ? Cell.TRAVERSABLE : Cell.WALL;
            }
        }
        return reduceMaze(maze, pathSize);
    }

    /**
     * Reduces the full scale maze array to a compressed version where the path and wall is only one cell wide.
     * Time complexity: O(nm)
     * Where n is the height of the maze and m is the width of the maze.
     * @param maze full scale representation of the maze
     * @param pathSize size of the path
     * @return compressed version of the maze
     */
    private Cell[][] reduceMaze(Cell[][] maze, int pathSize) {
        int height = maze.length;
        int wallSize = getWallWidth(maze);
        int skipSize = pathSize + wallSize;
        List<Cell[]> reducedMaze = new ArrayList<>();

        // Start at the row where the last top wall cell is, store two rows and increment with skipSize to get to the
        // last pixel of the next wall.
        for (int i = wallSize - 1; i < height; i += skipSize) { // Time complexity: O(nm)
            reducedMaze.add(reduceRowByInterval(maze[i], skipSize, wallSize)); // Time complexity: O(m)
            if (i + 1 < height) {
                reducedMaze.add(reduceRowByInterval(maze[i + 1], skipSize, wallSize)); // Time complexity: O(m)
            }
        }
        return reducedMaze.toArray(new Cell[0][0]); // Time complexity: O(nm)
    }

    /**
     * Reduces the full scale row to a compressed version where the path and wall is only one cell wide.
     * Time complexity: O(m)
     * Where m is the width of the maze.
     * @param row full scale representation of a row in the maze
     * @param skipSize size of the path
     * @return compressed version of the row without null values
     */
    private Cell[] reduceRowByInterval(Cell[] row, int skipSize, int wallSize) {
        int width = row.length;
        List<Cell> reducedMaze = new ArrayList<>();

        // Start at the index where the last left wall cell is, store two cells and increment with skipSize to get to the
        // last pixel of the next wall.
        for (int i = wallSize - 1; i < width; i += skipSize) { // Time complexity: O(m)
            if (i - wallSize < 0) {
                reducedMaze.add(row[i]);
            } else {
                Cell temp = row[i - wallSize] == row[i] ? row[i] : Cell.WALL;
                reducedMaze.add(temp);
            }
            if (i + 1 < width) {
                reducedMaze.add(row[i + 1]);
            }
        }

        return reducedMaze.toArray(new Cell[0]);
    }

    /**
     * Estimates the wall width by iterating from the edge of the maze and inwards.
     * Starts at 1/7 of the height and iterates every 1/7 + 1 of the height.
     * Time complexity: O(m)
     * Where n is the height of the maze and m is the width of the maze.
     * @param maze full scale representation of the maze
     * @return estimated wall width
     */
    private int getWallWidth(Cell[][] maze) {
        int width = maze[0].length;
        int height = maze.length;
        int wallWidth = 0;
        int samples = 7;

        // Iterates samples - 1 times;
        for (int i = height / samples; i < height; i += height / samples + 1) { // Time complexity: O(m)
            int currentWidth = 0;
            for (int j = 0; j < width; j++) { // Time complexity: O(m)
                if (maze[i][j] == Cell.WALL) {
                    currentWidth++;
                } else {
                    wallWidth = Math.max(wallWidth, currentWidth);
                    currentWidth = 0;
                }
            }
        }

        return wallWidth;
    }

    /**
     * Iterates over the edges and finds the smallest continuous white area.
     * This is used to determine the size of the path.
     * Time complexity: O(n+m)
     * Where n is the height of the maze and m is the width of the maze.
     * @param image image of the maze
     * @return size of the path
     */
    private static int findSmallestContinuousWhite(BufferedImage image) {
        int smallestWhite = Integer.MAX_VALUE;
        int currentWhite = 0;
        int badPixelThreshold = 2;
        int width = image.getWidth();
        int height = image.getHeight();
        boolean foundWhite = false;

        // Iterate over the top edge (left to right)
        for (int i = 0; i < width; i++) { // Time complexity: O(m)
            if (isNotWall(image.getRGB(i, 0)) && i != width - 1) {
                currentWhite++;
                foundWhite = true;
            } else {
                if (foundWhite && currentWhite > badPixelThreshold) {
                    smallestWhite = Math.min(smallestWhite, currentWhite);
                    foundWhite = false;
                }
                currentWhite = 0;
            }
        }

        // Iterate over the right edge (top to bottom)
        for (int i = 0; i < height; i++) { // Time complexity: O(n)
            if (isNotWall(image.getRGB(width - 1, i)) && i != height - 1) {
                currentWhite++;
                foundWhite = true;
            } else {
                if (foundWhite && currentWhite > badPixelThreshold) {
                    smallestWhite = Math.min(smallestWhite, currentWhite);
                    foundWhite = false;
                }
                currentWhite = 0;
            }
        }

        // Iterate over the bottom edge (right to left)
        for (int i = width - 1; i >= 0; i--) { // Time complexity: O(m)
            if (isNotWall(image.getRGB(i, height - 1)) && i != 0) {
                currentWhite++;
                foundWhite = true;
            } else {
                if (foundWhite && currentWhite > badPixelThreshold) {
                    smallestWhite = Math.min(smallestWhite, currentWhite);
                    foundWhite = false;
                }
                currentWhite = 0;
            }
        }

        // Iterate over the left edge (bottom to top)
        for (int i = height - 1; i >= 0; i--) { // Time complexity: O(n)
            if (isNotWall(image.getRGB(0, i)) && i != 0) {
                currentWhite++;
                foundWhite = true;
            } else {
                if (foundWhite && currentWhite > badPixelThreshold) {
                    smallestWhite = Math.min(smallestWhite, currentWhite);
                    foundWhite = false;
                }
                currentWhite = 0;
            }
        }

        return smallestWhite;
    }

    /**
     * Checks if the given color is not a wall (black).
     * Time complexity: O(1)
     * @param color color to check
     * @return true if the color is not a wall
     */
    private static boolean isNotWall(int color) {
        return color > WALL_COLOR;
    }


    /**
     * Processes the given image to remove the white borders.
     * Time complexity: O(n+m)
     * Where n is the height of the maze and m is the width of the maze.
     * @param mazeImage image of the maze
     * @return the given image with white borders removed.
     */
    private BufferedImage processImage(File mazeImage) {
        BufferedImage image = null;
        try {
            // Load the image
            image = ImageIO.read(mazeImage);

            // Process the image
            image = removeBorders(image); // Time complexity: O(n+m)
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Removes white borders form a black and white maze image.
     * Time complexity: O(n+m)
     * Where n is the height of the maze and m is the width of the maze.
     * @param image image containing black and white maze
     * @return the given image with white borders removed.
     */
    private BufferedImage removeBorders(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Define variables to store measurement results
        int[][] measurements = new int[4][2];

        // Define start coordinates for measurements on each side
        int[][] startCoords = {
                {0, height / 3},  // x-coordinate of left border, direction RIGHT
                {width / 3, 0},  // y-coordinate of top border, direction DOWN
                {width - 1, height / 3},  // x-coordinate of right border, direction LEFT
                {width / 3, height - 1}  // y-coordinate of bottom border, direction UP
        };
        Direction[] directions = {Direction.RIGHT, Direction.DOWN, Direction.LEFT, Direction.UP};

        // Perform measurements on each side
        for (int i = 0; i < startCoords.length; i++) { // Time complexity: O(n+m)
            int[] firstMeasurement = findFirstWall(image, directions[i], startCoords[i]); // Time complexity: O(n) or O(m)
            int[] secondMeasurement;
            do { // Time complexity: O(n) or O(m)
                // Update start coordinates for the second measurement
                // If direction against the wall is horizontal, move start coordinates along the vertical axis
                startCoords[i][0] += (directions[i] == Direction.UP || directions[i] == Direction.DOWN) ? height / 6 : 0;
                // If direction against the wall is vertical, move start coordinates along the horizontal axis
                startCoords[i][1] += (directions[i] == Direction.LEFT || directions[i] == Direction.RIGHT) ? width / 6 : 0;

                secondMeasurement = findFirstWall(image, directions[i], startCoords[i]); // Time complexity: O(n) or O(m)

                // Continue until the first and second measurement are the same
                // i 0 and 2 are x-coordinate measures, 1 and 3 are y-coordinate measures
            } while (firstMeasurement[i % 2] != secondMeasurement[i % 2]);

            measurements[i] = firstMeasurement;
        }

        // Determine the start coordinates to crop the image
        int x = measurements[0][0];  // x-coordinate of left border, direction RIGHT
        int y = measurements[1][1];  // y-coordinate of top border, direction DOWN
        // Determine the width and height of the cropped image
        int w = measurements[2][0] - x;  // offset from left border to right border
        int h = measurements[3][1] - y;  // offset from top border to bottom border

        // Crop the image and return it
        return image.getSubimage(x, y, w + 1, h + 1);
    }


    /**
     * Iterate through the image from the given start coordinates towards the given direction.
     * Time complexity: O(n) or O(m)
     * Where n is the height of the maze and m is the width of the maze.
     * @param image       to search through
     * @param direction   towards the middle of the image
     * @param startCoords to begin at
     * @return the coordinates of the first wall (not white pixel) found
     */
    private int[] findFirstWall(BufferedImage image, Direction direction, int[] startCoords) {
        while (startCoords[0] >= 0 && startCoords[0] < image.getWidth() &&
                startCoords[1] >= 0 && startCoords[1] < image.getHeight() &&
                image.getRGB(startCoords[0], startCoords[1]) > WALL_COLOR) { // Time complexity: O(n) or O(m)
            switch (direction) {
                case UP -> startCoords[1] -= 1;
                case DOWN -> startCoords[1] += 1;
                case LEFT -> startCoords[0] -= 1;
                case RIGHT -> startCoords[0] += 1;
            }
        }
        return startCoords;
    }
}
