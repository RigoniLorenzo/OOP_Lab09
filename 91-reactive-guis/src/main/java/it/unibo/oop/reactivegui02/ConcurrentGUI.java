package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton btnStop = new JButton("stop");
    private final JButton btnUp = new JButton("up");
    private final JButton btnDown = new JButton("down");

    /**
     * Builds a new CGUI.
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(btnStop);
        panel.add(btnUp);
        panel.add(btnDown);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();
        /*
         * Register the listeners to stop, increment and decrement
         */
        btnStop.addActionListener((e) -> agent.stopCounting());
        btnUp.addActionListener((e) -> agent.setIncrement());
        btnDown.addActionListener((e) -> agent.setDecrement());
    }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private int counter;
        private boolean upOrDown = true;

        /*
         * boolean upOrDown:
         * -true if increment
         * -false if decrement
         */

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    // The EDT doesn't access `counter` anymore, it doesn't need to be volatile 
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(nextText));
                    if (this.upOrDown) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    /*
                     * This is just a stack trace print, in a real program there
                     * should be some logging and decent error reporting
                     */
                    ex.printStackTrace(); // NOPMD: allowed as this is just an exercise
                }
            }
        }
        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
            btnStop.setEnabled(false);
            btnUp.setEnabled(false);
            btnDown.setEnabled(false);
        }
        /**
         * External command to set the increment.
         */
        public void setIncrement() {
            this.upOrDown = true;
        }
        /**
         * External command to set the decrement.
         */
        public void setDecrement() {
            this.upOrDown = false;
        }
    }
}
