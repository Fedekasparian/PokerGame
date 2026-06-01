package main.Vista.Grafica.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameButton extends JButton {
    private boolean hover = false;

    public GameButton(String text) {
        super(text);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(getFont().deriveFont(Font.BOLD, 14f));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 16;

        // sombra
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRoundRect(4, 5, getWidth() - 8, getHeight() - 8, arc, arc);

        // cuerpo
        g2.setColor(hover ? new Color(70, 70, 70, 240) : new Color(30, 30, 30, 220));
        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 6, arc, arc);

        // borde dorado
        g2.setColor(new Color(255, 215, 0, hover ? 200 : 140));
        g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 6, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}

