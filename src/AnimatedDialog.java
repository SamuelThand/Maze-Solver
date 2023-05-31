import javax.swing.*;

public record AnimatedDialog(JDialog dialog, Timer timer) {

    public JDialog getDialog() {
        return dialog;
    }

    public Timer getTimer() {
        return timer;
    }

}
