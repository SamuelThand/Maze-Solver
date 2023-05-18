public class Controller {

    private final Gui gui;
    private final MazeLoader loader;
    private final MazeSolver solver;

    public Controller(Gui gui, MazeLoader loader, MazeSolver solver) {
        this.gui = gui;
        this.loader = loader;
        this.solver = solver;
    }
}
