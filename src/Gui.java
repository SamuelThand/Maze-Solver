import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Queue;

public class Gui extends JFrame {

    private final Dimension frameSize;
    private JPanel buttonPanel;
    private JPanel mazePanel;
    private JButton selectButton;
    private JButton aStarButton;
    private JButton dijkstraButton;
    private JButton dijkstraButton2;
    private JButton[][] graphicalMaze;

    Gui(final Dimension frameSize) {
        this.frameSize = frameSize;
        this.graphicalMaze = null;
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

    public void displayMaze(Cell[][] maze) {

        this.mazePanel.removeAll();
        this.mazePanel.setLayout(new GridLayout(maze.length, maze[0].length));
        this.graphicalMaze = new JButton[maze.length][maze[0].length];

        for (int row = 0; row < maze.length; row++)
            for (int col = 0; col < maze[0].length; col++) {
                var button = new JButton();

                button.setBackground(switch (maze[row][col]) {
                    case TRAVERSABLE -> Color.WHITE;
                    case WALL -> Color.BLACK;
                    case DEAD_END -> Color.RED;
                    case VISITED -> Color.BLUE;
                    case PATH -> Color.GREEN;
                    default -> throw new IllegalStateException("Unexpected value: " + maze[row][col]);
                });
                button.setBorderPainted(false);  // Do not paint the border
                button.setContentAreaFilled(true);  // Fill the content area with the background color

                // Add action listener to the button
                button.addActionListener(e -> {
                    button.setBackground(Color.ORANGE);

                    //TODO Skicka startposition/slutposition till controller
                    //TODO Byt färg för start och stopp-position och gör alla celler icke klickbara om man
                    // inte togglar ur någon av de

                });

                this.graphicalMaze[row][col] = button;
                this.mazePanel.add(button);
            }

        this.mazePanel.validate();
        this.mazePanel.repaint();
    }

    public void replaySearchProcedure(Queue<MazeTraversalStep> steps) {
        //TODO take steps and replay them by changing the colors in the graphical maze based on the new state.
    }

}
