package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final long SEC_TILL_OFF = TimeUnit.SECONDS.toMillis(10);
    private final JLabel label = new JLabel();

    /**
     * Builds GUI.
     */
    public AnotherConcurrentGUI() {
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
        new Thread(() -> {
            try {
                Thread.sleep(SEC_TILL_OFF);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            agent.stopCounting();
        }).start();
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
                    SwingUtilities.invokeLater(() -> AnotherConcurrentGUI.this.label.setText(nextText));
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
