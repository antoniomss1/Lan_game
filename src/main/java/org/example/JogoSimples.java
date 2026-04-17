package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class JogoSimples extends JPanel implements ActionListener, MouseMotionListener {

    private double playerX = 400;
    private double playerY = 300;

    private double mouseX = 400;
    private double mouseY = 300;

    private double angle = 0;
    private double speed = 2.5;

    Timer timer;

    public JogoSimples() {
        setBackground(Color.BLACK);
        addMouseMotionListener(this);

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Calcula direção
        double dx = mouseX - playerX;
        double dy = mouseY - playerY;

        angle = Math.atan2(dy, dx);

        // Move o player
        playerX += Math.cos(angle) * speed;
        playerY += Math.sin(angle) * speed;

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // Suavização
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Salva transformação original
        AffineTransform old = g2d.getTransform();

        // Move para posição do player
        g2d.translate(playerX, playerY);

        // Rotaciona para o mouse
        g2d.rotate(angle);

        // Desenha triângulo (apontando para direita inicialmente)
        Polygon triangle = new Polygon();
        triangle.addPoint(15, 0);
        triangle.addPoint(-10, -10);
        triangle.addPoint(-10, 10);

        g2d.setColor(Color.GREEN);
        g2d.fill(triangle);

        // Restaura transformação
        g2d.setTransform(old);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Jogo Simples");
        JogoSimples jogo = new JogoSimples();

        frame.add(jogo);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}