import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.function.Function;

public class Gui extends JFrame {

    //TODO refactor cells to have their own coordinate, then optimize

    private final Dimension frameSize;
    private JPanel buttonPanel;
    private JPanel stepsPanel;
    private JPanel mazePanel;
    private JButton selectButton;
    private JButton aStarButton;
    private JButton dijkstraButton;
    private JButton dijkstraButton2;
    private JButton resetMazeButton;
    private JLabel traversalStepsLabel;
    private JLabel traversalStepsCounter;
    private int traversalSteps;
    private Cell[][] unsolvedMaze;
    private JButton[][] graphicalMaze;
    private final Map<JButton, Coordinate> buttonCoordinateMap = new HashMap<>();
    private final List<Coordinate> changedCells;
    private JButton startButton;
    private JButton finishButton;

    private enum State {
        NONE_SELECTED, START_SELECTED, FINISH_SELECTED, BOTH_SELECTED
    }

    private State currentState;

    Gui(final Dimension frameSize) {
        this.frameSize = frameSize;
        this.graphicalMaze = null;
        this.currentState = State.NONE_SELECTED;
        this.traversalSteps = 0;
        this.changedCells = new ArrayList<>();
        this.initFrame();
        this.initPanels();
        this.initComponents();
        this.build();
    }

    private void initFrame() {
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Maze Solver");
    }

    private void initPanels() {
        final Dimension thisFrameSize = this.frameSize;
        this.mazePanel = new JPanel() {
            private final Dimension frameSize = thisFrameSize;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(frameSize.width, frameSize.height);
            }
        };
        this.buttonPanel = new JPanel();
        this.buttonPanel.setLayout(new FlowLayout());
        this.stepsPanel = new JPanel();
    }

    private void initComponents() {
        this.selectButton = new JButton("Select Maze");
        this.aStarButton = new JButton("A*");
        this.dijkstraButton = new JButton("Dijkstra");
        this.dijkstraButton2 = new JButton("Dijkstra2");
        this.resetMazeButton = new JButton("Reset Maze");

        this.traversalStepsLabel = new JLabel("Steps: ");
        this.traversalStepsCounter = new JLabel(String.valueOf(this.traversalSteps));

        this.setButtonStates(false, null);
        this.selectButton.setEnabled(true);
    }

    private void build() {
        this.buttonPanel.add(this.selectButton);
        this.buttonPanel.add(this.resetMazeButton);
        this.buttonPanel.add(this.aStarButton);
        this.buttonPanel.add(this.dijkstraButton);
        this.buttonPanel.add(this.dijkstraButton2);

        this.stepsPanel.add(this.traversalStepsLabel);
        this.stepsPanel.add(this.traversalStepsCounter);

        this.add(this.buttonPanel, BorderLayout.NORTH);
        this.add(this.stepsPanel, BorderLayout.CENTER);
        this.add(this.mazePanel, BorderLayout.SOUTH);
    }

    public void setSelectButtonListener(ActionListener listener) {
        this.selectButton.addActionListener(listener);
    }

    public void setAstarButtonListener(ActionListener listener) {
        this.aStarButton.addActionListener(listener);
    }

    public void setDijkstraButtonListener(ActionListener listener) {
        this.dijkstraButton.addActionListener(listener);
    }

    public void setDijkstraButton2Listener(ActionListener listener) {
        this.dijkstraButton2.addActionListener(listener);
    }

    public void setResetMazeButtonListener(ActionListener listener) {
        this.resetMazeButton.addActionListener(listener);
    }

    public Coordinate getStartCoordinate() {
        return buttonCoordinateMap.get(this.startButton);
    }

    public Coordinate getFinishCoordinate() {
        return buttonCoordinateMap.get(this.finishButton);
    }

    private void restoreState() {
        this.currentState = State.NONE_SELECTED;
        this.startButton = null;
        this.finishButton = null;
        this.resetStepsCounter();
        this.setButtonStates(false, this.selectButton);
        this.changedCells.clear();
    }

    public void resetMaze() {
        this.repaintMaze();
        this.restoreState();
        this.resetStepsCounter();
    }

    public void displayMaze(Cell[][] maze) {
        this.restoreState();
        this.mazePanel.removeAll();
        this.mazePanel.setLayout(new GridLayout(maze.length, maze[0].length));
        this.graphicalMaze = new JButton[maze.length][maze[0].length];

        for (int row = 0; row < maze.length; row++)
            for (int col = 0; col < maze[0].length; col++) {
                var button = new JButton();
                button.setBackground(translateStateToColor(maze[row][col]));
                button.setBorderPainted(false);
                button.setContentAreaFilled(true);

                if (maze[row][col] == Cell.WALL)
                    button.setEnabled(false);
                else {
                    int finalRow = row;
                    int finalCol = col;
                    button.addActionListener(e -> {
                        switch (this.currentState) {
                            case NONE_SELECTED -> {
                                button.setBackground(translateStateToColor(Cell.START));
                                this.changedCells.add(new Coordinate(finalRow, finalCol));
                                this.startButton = button;
                                this.currentState = State.START_SELECTED;
                            }
                            case START_SELECTED -> {
                                if (button == this.startButton) {
                                    button.setBackground(translateStateToColor(Cell.TRAVERSABLE));
                                    this.startButton = null;
                                    this.currentState = State.NONE_SELECTED;
                                } else {
                                    this.changedCells.add(new Coordinate(finalRow, finalCol));
                                    button.setBackground(translateStateToColor(Cell.FINISH));
                                    this.finishButton = button;
                                    this.currentState = State.BOTH_SELECTED;
                                    this.setButtonStates(true, this.selectButton);
                                }
                            }
                            case FINISH_SELECTED -> {
                                if (button == this.finishButton) {
                                    button.setBackground(translateStateToColor(Cell.TRAVERSABLE));
                                    this.finishButton = null;
                                    this.currentState = State.NONE_SELECTED;
                                } else {
                                    this.changedCells.add(new Coordinate(finalRow, finalCol));
                                    button.setBackground(translateStateToColor(Cell.START));
                                    this.startButton = button;
                                    this.currentState = State.BOTH_SELECTED;
                                    this.setButtonStates(true, this.selectButton);
                                }
                            }
                            case BOTH_SELECTED -> {
                                if (button == this.startButton) {
                                    button.setBackground(translateStateToColor(Cell.TRAVERSABLE));
                                    this.startButton = null;
                                    this.currentState = State.FINISH_SELECTED;
                                    this.setButtonStates(false, this.selectButton);
                                } else if (button == this.finishButton) {
                                    button.setBackground(translateStateToColor(Cell.TRAVERSABLE));
                                    this.finishButton = null;
                                    this.currentState = State.START_SELECTED;
                                    this.setButtonStates(false, this.selectButton);
                                }
                            }
                        }
                });
                }

                this.graphicalMaze[row][col] = button;
                buttonCoordinateMap.put(button, new Coordinate(row, col));
                this.mazePanel.add(button);
            }

        this.unsolvedMaze = maze;
        this.mazePanel.validate();
        this.mazePanel.repaint();
    }

    private void repaintMaze() {
        for (Coordinate changedCell : this.changedCells)
                this.graphicalMaze[changedCell.row()][changedCell.col()].setBackground(
                        translateStateToColor(this.unsolvedMaze[changedCell.row()][changedCell.col()]));
    }

    private static Color translateStateToColor(Cell cell) {
        return switch (cell) {
            case TRAVERSABLE -> Color.WHITE;
            case WALL -> Color.BLACK;
            case DEAD_END -> Color.RED;
            case VISITED -> Color.BLUE;
            case PATH -> Color.GREEN;
            case START -> Color.ORANGE;
            case FINISH -> Color.CYAN;
        };
    }

    private void setButtonStates(Boolean value, JButton exception) {
        if (!(this.selectButton == exception))
            this.selectButton.setEnabled(value);
        if (!(this.aStarButton == exception))
            this.aStarButton.setEnabled(value);
        if (!(this.dijkstraButton == exception))
            this.dijkstraButton.setEnabled(value);
        if (!(this.dijkstraButton2 == exception))
            this.dijkstraButton2.setEnabled(value);
        if (!(this.resetMazeButton == exception))
            this.resetMazeButton.setEnabled(value);
    }

    public void filePicker(Function<File, Void> callback) {
        var picker = new JFileChooser();
        picker.setCurrentDirectory(new File("resources/mazes"));
        picker.setFileFilter(new FileNameExtensionFilter("JPG files", "jpg"));

        if (picker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            callback.apply(new File(picker.getSelectedFile().getAbsolutePath()));
    }

    public void replaySearchProcedure(Queue<MazeTraversalStep> steps) {

        this.repaintMaze();
        this.resetStepsCounter();
        this.setButtonStates(false, null);

        var worker = new SwingWorker<Void, MazeTraversalStep>() {
            @Override
            protected Void doInBackground() {
                for (MazeTraversalStep step : steps) {
                        publish(step);     // Send the step to the process method
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Restore the interrupted status
                    }
                }
                return null;
            }

            @Override
            protected void process(List<MazeTraversalStep> chunks) {
                for (MazeTraversalStep step : chunks) {
                    graphicalMaze[step.coordinate().row()][step.coordinate().col()].setBackground(translateStateToColor(step.newState()));
                    changedCells.add(step.coordinate());
                    incrementStepsCounter();
                }
            }

            @Override
            protected void done() {
                setButtonStates(true, null);
            }
        };

        worker.execute();
    }

    private void resetStepsCounter() {
        this.traversalSteps = 0;
        this.traversalStepsCounter.setText(String.valueOf(this.traversalSteps));
    }

    private void incrementStepsCounter() {
        this.traversalSteps++;
        this.traversalStepsCounter.setText(String.valueOf(this.traversalSteps));
    }
}
