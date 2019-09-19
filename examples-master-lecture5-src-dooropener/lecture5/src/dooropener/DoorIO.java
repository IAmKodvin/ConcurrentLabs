package dooropener;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

public class DoorIO {

    private enum Direction { IDLE, CLOSING, OPENING }

    public void start() {
        new Thread(this::runMotor).start();
    }
    
    public void startOpening() {
        state.setDirection(Direction.OPENING);
    }

    public void startClosing() {
        state.setDirection(Direction.CLOSING);
    }

    public void stop() {
        state.setDirection(Direction.IDLE);
    }

    public void awaitButton() throws InterruptedException {
        state.awaitButton();
    }

    public void awaitOpen() throws InterruptedException {
        state.awaitOpenness(100);
    }

    public void awaitClosed() throws InterruptedException {
        state.awaitOpenness(0);
    }

    // =======================================================================

    private final JLabel stateIndicator = new JLabel("---", SwingConstants.CENTER); 
    
    private static final int DOOR_WIDTH    = 120;
    private static final int DOOR_HEIGHT   = 250;
    private static final int DOOR_MARGIN   = 20;
    private static final int DOOR_X        = DOOR_WIDTH;
    private static final int DOOR_Y        = DOOR_HEIGHT / 3;
    private static final int BUTTON_WIDTH  = 80;
    private static final int BUTTON_HEIGHT = 30;

    private static final Dimension VIEW_SIZE = new Dimension(DOOR_WIDTH * 3, DOOR_HEIGHT * 5 / 3);

    private static final Stroke WIDE_STROKE  = new BasicStroke(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
    private static final Font INDICATOR_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);

    @SuppressWarnings("serial")
    private class DoorView extends JPanel {
        private DoorView() {
            super(null);
            setPreferredSize(VIEW_SIZE);

            JFrame frame = new JFrame("Door Opener");
            frame.add(this, BorderLayout.CENTER);

            JButton b = new JButton("OPEN");
            b.addActionListener(e -> state.buttonPressed());
            b.setBounds(DOOR_WIDTH * 2 + (DOOR_WIDTH - BUTTON_WIDTH) / 2, DOOR_Y + (DOOR_HEIGHT - BUTTON_HEIGHT) / 2, BUTTON_WIDTH, BUTTON_HEIGHT);
            b.setBorder(new LineBorder(Color.GRAY, 2));
            b.setFocusable(false);
            add(b);
            frame.getRootPane().setDefaultButton(b);

            stateIndicator.setFont(INDICATOR_FONT);
            stateIndicator.setForeground(Color.DARK_GRAY);
            stateIndicator.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            frame.add(stateIndicator, BorderLayout.SOUTH);

            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, DOOR_Y - DOOR_MARGIN, VIEW_SIZE.width, DOOR_HEIGHT + 2 * DOOR_MARGIN);
            
            double alpha = 3 * state.getOpenness() * Math.PI / 400;
            int dx = (int) (DOOR_WIDTH * Math.cos(alpha));
            int dy = (int) (DOOR_HEIGHT * Math.sin(alpha) / 4);  // for perspective

            int openingX = Math.max(0, dx);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(DOOR_X + openingX, DOOR_Y, DOOR_WIDTH - openingX, DOOR_HEIGHT);
 
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(WIDE_STROKE);
            g2d.drawRect(DOOR_X, DOOR_Y, DOOR_WIDTH - 1, DOOR_HEIGHT - 1);
            
            GeneralPath door = new GeneralPath();
            door.moveTo(DOOR_X, DOOR_Y);
            door.lineTo(DOOR_X + dx, DOOR_Y - dy);
            door.lineTo(DOOR_X + dx, DOOR_Y + DOOR_HEIGHT + dy);
            door.lineTo(DOOR_X, DOOR_Y + DOOR_HEIGHT);
            door.closePath();
            g2d.setColor(Color.RED.darker());
            g2d.fill(door);
        }
    }

    // -----------------------------------------------------------------------

    private JPanel createView() {
        // perform initialization in the Swing UI thread
        final CompletableFuture<JPanel> viewStore = new CompletableFuture<>();
        try {
            SwingUtilities.invokeLater(() -> viewStore.complete(new DoorView()));
            return viewStore.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new Error(e);
        }
    }

    private final JPanel view = createView();

    // =======================================================================
    
    private class State {

        private Direction direction = Direction.IDLE;
        private int openness = 0; // door state in percent (0 = closed, 100 = open)
        private boolean buttonPressed = false;

        public synchronized void setDirection(Direction dir) {
            direction = dir;
            notifyAll();
            SwingUtilities.invokeLater(() -> stateIndicator.setText(dir.toString()));
        }

        public synchronized void awaitOpenness(int openness) throws InterruptedException {
            while (this.openness != openness) {
                wait();
            }
        }

        private synchronized void runMotorIncrement() throws InterruptedException {
            while (direction == Direction.IDLE) {
                wait();
            }
            switch (direction) {
            case CLOSING:
                openness--;
                if (openness < 0) {
                    openness = 0;
                }
                notifyAll();
                break;
            case OPENING:
                openness++;
                if (openness > 100) {
                    openness = 100;
                }
                notifyAll();
                break;
            default:
                throw new Error("shouldn't happen");
            }
        }
        
        public synchronized double getOpenness() {
            return openness;
        }

        public synchronized void buttonPressed() {
            buttonPressed = true;
            notifyAll();
        }

        public synchronized void awaitButton() throws InterruptedException {
            buttonPressed = false;

            // this loop is necessary to avoid spurious wakeups
            while (!buttonPressed) {
                wait();
            }
            buttonPressed = false; // acknowledge
        }
    }
    
    private final State state = new State();

    // =======================================================================
    
    private static final int DOOR_UPDATE_PERIOD = 20;

    public void runMotor() {
        try {
            state.setDirection(Direction.IDLE); // ensure UI is updated
            while (true) {
                state.runMotorIncrement();
                view.repaint();
                Thread.sleep(DOOR_UPDATE_PERIOD);
            }
        } catch (InterruptedException unexpected) {
            throw new Error(unexpected);
        }
    }

    // =======================================================================

    static {
        // Makes for better performance on our lab machines.
        // (Must be done before Swing initialization.)
        System.setProperty("sun.java2d.opengl", "True");
        
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // never mind
        }
    }
}
