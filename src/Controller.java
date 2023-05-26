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
        this.gui.setSelectButtonListener(
                (event) -> this.gui.filePicker(
                    (file) -> {
                        var maze = this.loader.loadMaze(file);
                        System.out.println(maze.length);
                        System.out.println(maze[0].length);
                        this.solver.setMaze(maze);
                        this.gui.displayMaze(this.solver.getMaze());
                        return null;
                    }
        ));

        this.gui.setAstarButtonListener((event) -> {
            this.gui.replaySearchProcedure(this.solver.aStar(this.gui.getStartCoordinate(), this.gui.getFinishCoordinate(), true));
        });

        //TODO Astar knapp 2
//        this.gui.setAstarButtonListener((event) -> {
//            this.gui.replaySearchProcedure(this.solver.aStar(this.gui.getStartCoordinate(), this.gui.getFinishCoordinate(), false));
//        });

        this.gui.setDijkstraButtonListener((event) -> {
            this.gui.replaySearchProcedure(this.solver.dijkstra1(this.gui.getStartCoordinate(), this.gui.getFinishCoordinate()));
        });
        this.gui.setDijkstraButton2Listener((event) -> {
            this.gui.replaySearchProcedure(this.solver.dijkstra2(this.gui.getStartCoordinate(), this.gui.getFinishCoordinate()));
        });
        this.gui.setResetMazeButtonListener((event) -> {
            this.gui.resetMaze();
        });
    }
}
