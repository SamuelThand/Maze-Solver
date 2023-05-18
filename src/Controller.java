public class Controller {

    private Gui gui;
    private MazeLoader loader;
    private MazeSolver solver;

    public Controller(Gui gui, MazeLoader loader, MazeSolver solver) {
        this.gui = gui;
        this.loader = loader;
        this.solver = solver;
    }
}
