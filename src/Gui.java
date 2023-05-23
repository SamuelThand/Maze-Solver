import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class Gui extends JFrame {

    private final Dimension frameSize;
    private JPanel buttonPanel;
    private JPanel mazePanel;
    private JButton selectButton;
    private JButton aStarButton;
    private JButton djikstraButton;
    private JButton djikstraButton2;
    private Cell[][] originalMaze; //TODO refactor to backend? Get from backend via controller instead
    private JButton[][] graphicalMaze;
    private ArrayList<JButton> interactiveMazeCells;
    private Map<JButton, Coordinate> buttonCoordinateMap = new HashMap<>();
    private JButton startButton;
    private JButton finishButton;
    private Coordinate startCoordinate;
    private Coordinate finishCoordinate;

    private enum State {
        NONE_SELECTED, START_SELECTED, FINISH_SELECTED, BOTH_SELECTED
    }
    private State currentState;

    Gui(final Dimension frameSize) {
        this.frameSize = frameSize;
        this.graphicalMaze = null;
        this.startButton = null;
        this.finishButton = null;
        this.currentState = State.NONE_SELECTED;
        this.interactiveMazeCells = new ArrayList<>();
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
    }

    private void initComponents() {
        this.selectButton = new JButton("Select Maze");
        this.aStarButton = new JButton("A*");
        this.djikstraButton = new JButton("Dijkstra");
        this.djikstraButton2 = new JButton("Dijkstra");

        this.setButtonStates(false);
        this.selectButton.setEnabled(true);
    }

    private void build() {
        this.buttonPanel.add(this.selectButton);
        this.buttonPanel.add(this.aStarButton);
        this.buttonPanel.add(this.djikstraButton);
        this.buttonPanel.add(this.djikstraButton2);

        this.add(this.buttonPanel, BorderLayout.NORTH);
        this.add(this.mazePanel, BorderLayout.SOUTH);
    }

    public void setSelectButtonListener(ActionListener listener) {
        this.selectButton.addActionListener(listener);
    }

    public void setAstarButtonListener(ActionListener listener) {
        this.aStarButton.addActionListener(listener);
    }

    public void setDjikstraButtonListener(ActionListener listener) {
        this.djikstraButton.addActionListener(listener);
    }

    public void setDjikstraButton2Listener(ActionListener listener) {
        this.djikstraButton2.addActionListener(listener);
    }

    public Coordinate getStartCoordinate() {
        return buttonCoordinateMap.get(this.startButton);
    }

    public Coordinate getFinishCoordinate() {
        return buttonCoordinateMap.get(this.finishButton);
    }

    public void displayMaze(Cell[][] maze) {
        this.currentState = State.NONE_SELECTED;
        this.startButton = null;
        this.finishButton = null;

        this.mazePanel.removeAll();
        this.mazePanel.setLayout(new GridLayout(maze.length, maze[0].length));
        this.graphicalMaze = new JButton[maze.length][maze[0].length];

        for (int row = 0; row < maze.length; row++)
            for (int col = 0; col < maze[0].length; col++) {
                var button = new JButton();
                button.setBackground(translateStateToColor(maze[row][col]));
                button.setBorderPainted(false);  // Do not paint the border
                button.setContentAreaFilled(true);  // Fill the content area with the background color

                if (maze[row][col] == Cell.WALL)
                    button.setEnabled(false);
                else
                    this.interactiveMazeCells.add(button);
                    button.addActionListener(e -> {
                        switch (this.currentState) {
                            case NONE_SELECTED:
                                button.setBackground(translateStateToColor(Cell.START));
                                this.startButton = button;
                                this.currentState = State.START_SELECTED;
                                break;
                            case START_SELECTED:
                                if (button == this.startButton) {
                                    button.setBackground(translateStateToColor(Cell.TRAVERSABLE));
                                    this.startButton = null;
                                    this.currentState = State.NONE_SELECTED;
                                } else {
                                    button.setBackground(translateStateToColor(Cell.FINISH));
                                    this.finishButton = button;
                                    this.currentState = State.BOTH_SELECTED;
                                    this.setInteractiveMazeCells(false, this.startButton, this.finishButton);
                                    this.setButtonStates(true, this.selectButton);
                                }
                                break;
                            case FINISH_SELECTED:
                                if (button == this.finishButton) {
                                    button.setBackground(translateStateToColor(Cell.TRAVERSABLE));
                                    this.finishButton = null;
                                    this.currentState = State.NONE_SELECTED;
                                } else {
                                    button.setBackground(translateStateToColor(Cell.START));
                                    this.startButton = button;
                                    this.currentState = State.BOTH_SELECTED;
                                    this.setInteractiveMazeCells(false, this.startButton, this.finishButton);
                                    this.setButtonStates(true, this.selectButton);
                                }
                                break;
                            case BOTH_SELECTED:
                                if (button == this.startButton) {
                                    button.setBackground(translateStateToColor(Cell.TRAVERSABLE));
                                    this.startButton = null;
                                    this.currentState = State.FINISH_SELECTED;
                                    this.setInteractiveMazeCells(true, this.finishButton);
                                    this.setButtonStates(false, this.selectButton);
                                } else if (button == this.finishButton) {
                                    button.setBackground(translateStateToColor(Cell.TRAVERSABLE));
                                    this.finishButton = null;
                                    this.currentState = State.START_SELECTED;
                                    this.setInteractiveMazeCells(true, this.startButton);
                                    this.setButtonStates(false, this.selectButton);
                                }
                        }
                });

                this.graphicalMaze[row][col] = button;
                buttonCoordinateMap.put(button, new Coordinate(row, col));
                this.mazePanel.add(button);
            }

        this.originalMaze = maze;
        this.mazePanel.validate();
        this.mazePanel.repaint();
    }

    private void repaintMaze() {
        for (int row = 0; row < this.originalMaze.length; row++)
            for (int col = 0; col < this.originalMaze[0].length; col++)
                this.graphicalMaze[row][col].setBackground(translateStateToColor(this.originalMaze[row][col]));
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

    private void setButtonStates(Boolean value, JButton... exceptions) {
        if (!containsButton(this.selectButton, exceptions))
            this.selectButton.setEnabled(value);
        if (!containsButton(this.aStarButton, exceptions))
            this.aStarButton.setEnabled(value);
        if (!containsButton(this.djikstraButton, exceptions))
            this.djikstraButton.setEnabled(value);
        if (!containsButton(this.djikstraButton2, exceptions))
            this.djikstraButton2.setEnabled(value);
    }

    private void setInteractiveMazeCells(Boolean value, JButton... exceptions) {
        for (JButton button : this.interactiveMazeCells)
            if (!containsButton(button, exceptions))
                button.setEnabled(value);
    }

    private static boolean containsButton(JButton button, JButton... exceptions) {
        for (JButton exception : exceptions) {
            if (button == exception) {
                return true;
            }
        }
        return false;
    }

    public void filePicker(Function<File, Void> callback) {
//        var path = "src/med7.jpg"; //TODO extract path from picker function
        var picker = new JFileChooser();
        picker.setCurrentDirectory(new File("resources/mazes"));
        picker.setFileFilter(new FileNameExtensionFilter("JPG files", "jpg"));

        if (picker.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            callback.apply(new File(picker.getSelectedFile().getAbsolutePath()));
    }

    public void replaySearchProcedure(Queue<MazeTraversalStep> steps) {

        //TODO increment a counter in the gui for each executed step

        this.repaintMaze();
        this.setButtonStates(false);
        this.setInteractiveMazeCells(false);
        var worker = new SwingWorker<Void, MazeTraversalStep>() {
            @Override
            protected Void doInBackground() {
                for (MazeTraversalStep step : steps) {
                    try {
                        Thread.sleep(5); // Sleep for 100 milliseconds
                        publish(step);     // Send the step to the process method
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
                }
            }

            @Override
            protected void done() {
                setButtonStates(true);
            }
        };

        worker.execute();
    }
}
