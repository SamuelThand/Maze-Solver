import javax.swing.*;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

public record Controller(Gui gui, MazeLoader loader, MazeSolver solver) {

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
            var dialog = gui.createDialog("Solving");
            SwingWorker<Queue<MazeTraversalStep>, Void> worker = new SwingWorker<>() {
                @Override
                protected Queue<MazeTraversalStep> doInBackground() {
                    dialog.timer().start();
                    return solver.aStar(gui.getStartCoordinate(), gui.getFinishCoordinate(), false);
                }

                @Override
                protected void done() {
                    processDoneTask(dialog, this);
                }
            };

            worker.execute();
            dialog.dialog().setVisible(true);
        });


        this.gui.setGreedyAstarButtonListener((event) -> {
            var dialog = gui.createDialog("Solving");
            SwingWorker<Queue<MazeTraversalStep>, Void> worker = new SwingWorker<>() {
                @Override
                protected Queue<MazeTraversalStep> doInBackground() {
                    dialog.timer().start();
                    return solver.aStar(gui.getStartCoordinate(), gui.getFinishCoordinate(), true);
                }

                @Override
                protected void done() {
                    processDoneTask(dialog, this);
                }
            };

            worker.execute();
            dialog.dialog().setVisible(true);
        });

        this.gui.setDijkstraButtonListener((event) -> {
            var dialog = gui.createDialog("Solving");
            SwingWorker<Queue<MazeTraversalStep>, Void> worker = new SwingWorker<>() {
                @Override
                protected Queue<MazeTraversalStep> doInBackground() {
                    dialog.timer().start();
                    return solver.dijkstra1(gui.getStartCoordinate(), gui.getFinishCoordinate());
                }

                @Override
                protected void done() {
                    processDoneTask(dialog, this);
                }
            };

            worker.execute();
            dialog.dialog().setVisible(true);
        });

        this.gui.setDijkstraButton2Listener((event) -> {
            var dialog = gui.createDialog("Solving");
            SwingWorker<Queue<MazeTraversalStep>, Void> worker = new SwingWorker<>() {
                @Override
                protected Queue<MazeTraversalStep> doInBackground() {
                    dialog.timer().start();
                    return solver.dijkstra2(gui.getStartCoordinate(), gui.getFinishCoordinate());
                }

                @Override
                protected void done() {
                    processDoneTask(dialog, this);
                }
            };

            worker.execute();
            dialog.dialog().setVisible(true);
        });

        this.gui.setResetMazeButtonListener((event) -> this.gui.resetMaze());
    }

    private void processDoneTask(AnimatedDialog dialog, SwingWorker<Queue<MazeTraversalStep>, Void> worker) {
        dialog.dialog().dispose();
        dialog.timer().stop();
        try {
            Queue<MazeTraversalStep> path = worker.get();
            gui.replaySearchProcedure(path);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
