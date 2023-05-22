import java.io.File;

public class Controller {

    private final Gui gui;
    private final MazeLoader loader;
    private final MazeSolver solver;

    public Controller(Gui gui, MazeLoader loader, MazeSolver solver) {
        this.gui = gui;
        this.loader = loader;
        this.solver = solver;
        this.setButtonListeners();
    }

    private void setButtonListeners() {
//        this.gui.setSelectButtonListener((event) -> this.gui.displayMaze(Testing.generateTestMaze(50))); //TESTING
//        this.gui.setAstarButtonListener((event) -> this.gui.replaySearchProcedure(Testing.generateTraversalSteps(50))); //TESTING

        this.gui.setSelectButtonListener(
                (event) -> this.gui.filePicker(
                    (file) -> {
                        this.gui.displayMaze(this.loader.loadMaze(file));
                        return null;
                    }
        ));

//        this.gui.setAstarButtonListener((event) -> {
//            this.
//            TODO utför algoritmen på den aktuella mazen,
//             returnera en sekvens med steg till gui.replay(), samt
//             statistik om hur många steg som togs etc
//
//        });
    }

}
