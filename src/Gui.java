import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Queue;

public class Gui extends JFrame {

    private final Dimension frameSize;
    private JPanel buttonPanel;
    private JPanel mazePanel;
    private JButton selectButton;
    private JButton aStarButton;
    private JButton dijkstraButton;
    private JButton dijkstraButton2;
    private Cell[][] mazeModel; //TODO refactor to backend? Get from backend via controller instead
    private JButton[][] interactiveMaze;

    Gui(final Dimension frameSize) {
        this.frameSize = frameSize;
        this.interactiveMaze = null;
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
        this.dijkstraButton = new JButton("Dijkstra");
        this.dijkstraButton2 = new JButton("Dijkstra");
    }

    private void build() {
        this.buttonPanel.add(this.selectButton);
        this.buttonPanel.add(this.aStarButton);
        this.buttonPanel.add(this.dijkstraButton);
        this.buttonPanel.add(this.dijkstraButton2);

        this.add(this.buttonPanel, BorderLayout.NORTH);
        this.add(this.mazePanel, BorderLayout.SOUTH);
    }

    public void setSelectButtonListener(ActionListener listener) {
        this.selectButton.addActionListener(listener);
    }

    public void setAstarButtonListener(ActionListener listener) {
        this.aStarButton.addActionListener(listener);
    }

    public void displayMaze(Cell[][] maze) {

        this.mazePanel.removeAll();
        this.mazePanel.setLayout(new GridLayout(maze.length, maze[0].length));
        this.interactiveMaze = new JButton[maze.length][maze[0].length];

        for (int row = 0; row < maze.length; row++)
            for (int col = 0; col < maze[0].length; col++) {
                var button = new JButton();
                button.setBackground(translateStateToColor(maze[row][col]));
                button.setBorderPainted(false);  // Do not paint the border
                button.setContentAreaFilled(true);  // Fill the content area with the background color

                // Add action listener to the button
                button.addActionListener(e -> {
                    button.setBackground(Color.ORANGE);

                    //TODO Skicka startposition/slutposition till controller
                    //TODO Byt färg för start och stopp-position och gör alla celler icke klickbara om man
                    // inte togglar ur någon av de

                });

                this.interactiveMaze[row][col] = button;
                this.mazePanel.add(button);
            }

        this.mazeModel = maze;
        this.mazePanel.validate();
        this.mazePanel.repaint();
    }

    private void repaintMaze() {
        for (int row = 0; row < this.mazeModel.length; row++)
            for (int col = 0; col < this.mazeModel[0].length; col++)
                this.interactiveMaze[row][col].setBackground(translateStateToColor(this.mazeModel[row][col]));
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

    private void setButtonStates(Boolean value) {
        this.selectButton.setEnabled(value);
        this.aStarButton.setEnabled(value);
        this.dijkstraButton.setEnabled(value);
        this.dijkstraButton2.setEnabled(value);
    }

    private void setInteractiveMazeCells(Boolean value) {
        for (JButton[] buttons : this.interactiveMaze)
            for (JButton button : buttons)
                button.setEnabled(value);
    }

    public void replaySearchProcedure(Queue<MazeTraversalStep> steps) {
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
                    interactiveMaze[step.row()][step.col()].setBackground(translateStateToColor(step.newState()));
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
