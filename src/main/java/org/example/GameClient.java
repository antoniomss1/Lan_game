package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.rmi.Naming;
import java.util.List;
import java.util.UUID;

import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class GameClient extends JPanel implements ActionListener, MouseMotionListener, MouseListener {

    private GameServer server;

    private String playerId = UUID.randomUUID().toString();

    private double playerX = 400, playerY = 300;
    private double mouseX = 400, mouseY = 300;
    private double angle = 0;

    private double speed = 150; // pixels por segundo

    private List<PlayerState> players;
    private List<Bullet> bullets;

    private long lastTime = System.nanoTime();

    Timer timer;

    // 🎨 10 cores fixas
    private Color[] colors = {
            Color.GREEN, Color.RED, Color.BLUE, Color.YELLOW, Color.CYAN,
            Color.MAGENTA, Color.ORANGE, Color.PINK, Color.WHITE, Color.GRAY
    };

    public GameClient() {
        try {
            String ip = discoverServer();

            server = (GameServer) Naming.lookup("rmi://" + ip + "/GameServer");
//            server = (GameServer) Naming.lookup("rmi://localhost/GameServer");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setBackground(Color.BLACK);
        addMouseMotionListener(this);
        addMouseListener(this);

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1e9;
            lastTime = now;

            double dx = mouseX - playerX;
            double dy = mouseY - playerY;
            double distance = Math.hypot(dx, dy);

            if (distance > 5) {
                angle = Math.atan2(dy, dx);

                playerX += Math.cos(angle) * speed * deltaTime;
                playerY += Math.sin(angle) * speed * deltaTime;
            }

            // envia estado
            server.updatePlayer(new PlayerState(playerId, playerX, playerY, angle, 0));

            players = server.getPlayers();
            bullets = server.getBullets();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // jogadores
        if (players != null) {
            for (PlayerState p : players) {

                AffineTransform old = g2d.getTransform();

                g2d.translate(p.x, p.y);
                g2d.rotate(p.angle);

                Polygon triangle = new Polygon();
                triangle.addPoint(15, 0);
                triangle.addPoint(-10, -10);
                triangle.addPoint(-10, 10);

                g2d.setColor(colors[p.colorId % colors.length]);
                g2d.fill(triangle);

                g2d.setTransform(old);
            }
        }

        // tiros
        if (bullets != null) {
            for (Bullet b : bullets) {
                g2d.setColor(colors[b.colorId % colors.length]);
                g2d.fillOval((int) b.x, (int) b.y, 5, 5);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            try {
                // encontra minha cor atual
                int myColor = 0;
                for (PlayerState p : players) {
                    if (p.id.equals(playerId)) {
                        myColor = p.colorId;
                        break;
                    }
                }

                server.shoot(new Bullet(playerX, playerY, angle, myColor));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override public void mouseDragged(MouseEvent e) { mouseMoved(e); }
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cliente");
        frame.add(new GameClient());
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private String discoverServer() {
        try {
            DatagramSocket socket = new DatagramSocket(8888);
            socket.setBroadcast(true);

            byte[] buffer = new byte[256];

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            System.out.println("Procurando servidor...");

            socket.receive(packet);

            String msg = new String(packet.getData(), 0, packet.getLength());

            if (msg.startsWith("GAME_SERVER")) {
                String serverIP = packet.getAddress().getHostAddress();
                System.out.println("Servidor encontrado: " + serverIP);
                return serverIP;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}