package it.unibo.oop.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Second example of reactive GUI.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel label = new JLabel();

    /**
     * Builds GUI.
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenDimension.getWidth() * WIDTH_PERC), (int) (screenDimension.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        final JPanel canvas = new JPanel();
        final JButton stop = new JButton("Stop");
        final JButton up = new JButton("Up");
        final JButton down = new JButton("Down");

        canvas.add(label);
        canvas.add(up);
        canvas.add(down);
        canvas.add(stop);

        this.getContentPane().add(canvas);
        this.setVisible(true);

        final Agent agent = new Agent();

        stop.addActionListener(a -> {
            up.setEnabled(false);
            down.setEnabled(false);
            agent.stopCounting();
        });
        up.addActionListener(a -> agent.countingUp());
        down.addActionListener(a -> agent.countingDown());

        new Thread(agent).start();
    }

    private final class Agent implements Runnable {
        private int counter;
        private volatile boolean stop;
        private volatile boolean up = true;

        @Override
        public void run() {
            try {
                while (!this.stop) {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeLater(() -> ConcurrentGUI.this.label.setText(nextText));
                    if (this.up) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void stopCounting() {
            this.stop = true;
        }

        public void countingUp() {
            this.up = true;
        }

        public void countingDown() {
            this.up = false;
        }
    }
}
