import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MazeLoader {

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    public Cell[][] loadMaze(File mazeImage) {
        BufferedImage bImage = this.processImage(mazeImage);
        return null;
    }

    private BufferedImage processImage(File mazeImage) {
        // TODO ide:
        // Ta bort vita mellanrummet utanför labyrinten genom att räkna antal rader och kolumner tills den slår i svart
        // Ignorera rött och grönt
        // Ta resultatlabyrinten med svart border och downscalea så att pathsen bara blir 1 pixel och väggar 1 pixel
        // Generera maze som matrix utifrån deta
        // Gör funktionalitet för att markera start och finish cell och låt användaren sätta ut start och finish i GUI
        BufferedImage image = null;
        try {
            // Load the image
            image = ImageIO.read(mazeImage);

            // Process the image
            image = removeBorders(image);

            // Save the image
            ImageIO.write(image, "jpg", new File("src/new-maze.jpg"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    /**
     * Removes white borders form a black and white maze image.
     *
     * @param image image containing black and white maze
     * @return the given image with white borders removed.
     */
    private BufferedImage removeBorders(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        System.out.println("Height: " + height + " Width: " + width);

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
        for (int i = 0; i < 4; i++) {
            int[] firstMeasurement = findFirstWall(image, directions[i], startCoords[i]);
            int[] secondMeasurement;
            do {
                // Update start coordinates for the second measurement
                // If direction against the wall is horizontal, move start coordinates along the vertical axis
                startCoords[i][0] += (directions[i] == Direction.UP || directions[i] == Direction.DOWN) ? height / 6 : 0;
                // If direction against the wall is vertical, move start coordinates along the horizontal axis
                startCoords[i][1] += (directions[i] == Direction.LEFT || directions[i] == Direction.RIGHT) ? width / 6 : 0;

                secondMeasurement = findFirstWall(image, directions[i], startCoords[i]);

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
        return image.getSubimage(x, y, w+1, h+1);
    }


    /**
     * Iterate through the image from the given start coordinates towards the given direction.
     *
     * @param image to search through
     * @param direction towards the middle of the image
     * @param startCoords to begin at
     * @return the coordinates of the first wall (not white pixel) found
     */
    private int[] findFirstWall(BufferedImage image, Direction direction, int[] startCoords) {
        while (startCoords[0] >= 0 && startCoords[0] < image.getWidth() &&
                startCoords[1] >= 0 && startCoords[1] < image.getHeight() &&
                image.getRGB(startCoords[0], startCoords[1]) > -1000000) {
            System.out.println(image.getRGB(startCoords[0], startCoords[1]));
            switch (direction) {
                case UP -> startCoords[1] -= 1;
                case DOWN -> startCoords[1] += 1;
                case LEFT -> startCoords[0] -= 1;
                case RIGHT -> startCoords[0] += 1;
            }
        }
        System.out.println(image.getRGB(startCoords[0], startCoords[1]));
        System.out.println("Found wall at: " + Arrays.toString(startCoords));
        return startCoords;
    }


}
