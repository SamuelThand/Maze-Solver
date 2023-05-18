import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Gui extends JFrame {

    private final Dimension frameSize;
    private JPanel buttonPanel;
    private JPanel mazePanel;
    private JButton selectButton;
    private JButton aStarButton;
    private JButton dijkstraButton;
    private JButton dijkstraButton2;

    Gui(final Dimension frameSize) {
        this.frameSize = frameSize;
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
        this.buttonPanel = new JPanel();
//        this.buttonPanel = new JPanel(this) {
//            private Gui gui;
//
//            JPanel(Gui gui) {
//                this.gui = gui;
//            }
//
//            @Override
//            public Dimension getPreferredSize() {
//                Dimension frameSize = gameFrame.getFrameSize();
//
//                return new Dimension(frameSize.width, frameSize.height / this.frameHeightFraction);
//            }
//        };
        this.buttonPanel.setLayout(new FlowLayout());
        this.mazePanel = new JPanel();
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

    public void displayMaze() {
        this.mazePanel.setLayout(new GridLayout(10, 10));
    }

}
