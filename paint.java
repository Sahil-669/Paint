import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.*;
import java.util.Stack;

class Paint {

    static Color[] palette = { Color.RED, Color.GREEN, Color.BLUE, Color.WHITE, Color.BLACK, Color.PINK};
    static int boxSize = 20;
    static int brushSize = 30;
    static Stack<BufferedImage> history = new Stack<>();

    public static void main(String[] args) {

        BufferedImage image = new BufferedImage(900, 600,BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();

        g2.setColor(Color.WHITE);
        g2.fillRect(0,0,900,600);

        drawPallete(g2);
        g2.setColor(Color.BLACK);

        JFrame frame = new JFrame("Better Paint");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0,0,null);
            }
        };
            panel.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (e.getY() > boxSize) {
                    g2.fillOval(e.getX()-(brushSize/2), e.getY()-(brushSize/2), brushSize, brushSize);
                    panel.repaint();
                }
                }
            });

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {

                    history.push(copyImage(image));
                    int x = e.getX();
                    int y = e.getY();

                    if (y < boxSize) {
                        int index = x / boxSize;

                        if (index >= 0 && index < palette.length) {
                            Color picked = palette[index];
                            g2.setColor(picked);
                            System.out.println("Color picked: "+ picked);
                        }
                    }
                }
            });

            panel.addMouseWheelListener(new MouseWheelListener() {

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {

                    int notches = e.getWheelRotation();

                    if (notches < 0) {
                        brushSize += 5;
                    } else {
                        brushSize -= 5;
                    }
                    if (brushSize < 5) brushSize = 5;
                    if (brushSize > 100) brushSize = 100;
                    System.out.println("Brush Size: "+ brushSize);
                }
            });
            frame.addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
                        if (!history.isEmpty()) {
                            System.out.println("Undo");

                            BufferedImage lastState = history.pop();
                            g2.drawImage(lastState, 0, 0, null);
                            drawPallete(g2);
                            panel.repaint();
                        } else {
                            System.out.println("Nothing to Undo.");
                        }
                    }
                }

            });
        panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0,0, null);
        g.dispose();
        return b;
    }
    public static void drawPallete(Graphics2D g2) {
        Color oldColor = g2.getColor();
        for (int i = 0; i < palette.length; i++) {
            g2.setColor(palette[i]);
            g2.fillRect(i * boxSize, 0, boxSize, boxSize);
        }
        g2.setColor(oldColor);
    }
}
