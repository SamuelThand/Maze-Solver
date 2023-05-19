import java.awt.*;
import java.io.File;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        var mazeLoader = new MazeLoader(); //TODO Static?
        var mazeSolver = new MazeSolver(); //TODO Static?
        var gui = new Gui(determineFramSize());
        new Controller(gui, mazeLoader, mazeSolver);

        mazeLoader.loadMaze(new File("src/maze-image3.jpg"));

        EventQueue.invokeLater(() -> {
            gui.pack();
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
            gui.setSelectButtonListener((event) -> gui.displayMaze(Testing.generateTestMaze(50))); //TESTING
            gui.setAstarButtonListener((event) -> gui.replaySearchProcedure(Testing.generateTraversalSteps(50))); //TESTING
        });


    }

    private static Dimension determineFramSize() {
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Dimension(screenSize.width / 2, screenSize.height / 2);
    }



}
