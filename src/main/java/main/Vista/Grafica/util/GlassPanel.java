package main.Vista.Grafica.util;

import javax.swing.*;
import java.awt.*;

    public class GlassPanel extends JPanel {
        public GlassPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 18;

            // fondo glass
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            // borde suave
            g2.setColor(new Color(255, 215, 0, 120)); // dorado suave
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }

