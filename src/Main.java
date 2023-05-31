import java.awt.*;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            var mazeLoader = new MazeLoader();
            var mazeSolver = new MazeSolver();
            var gui = new Gui(determineFramSize());
            new Controller(gui, mazeLoader, mazeSolver);
            gui.pack();
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);

        });
    }

    private static Dimension determineFramSize() {
        var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Dimension(screenSize.width / 2, screenSize.height / 2);
    }
}
