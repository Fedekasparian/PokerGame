package main.Vista.Grafica.util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

    public class BackgroundPanel extends JPanel {
        private final Image bg;
        private final float overlayAlpha; // 0..1

        public BackgroundPanel(String resourcePath, float overlayAlpha) {
            URL url = getClass().getResource(resourcePath);
            if (url == null) throw new IllegalArgumentException("No existe recurso: " + resourcePath);
            this.bg = new ImageIcon(url).getImage();
            this.overlayAlpha = overlayAlpha;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int w = getWidth(), h = getHeight();
            g2.drawImage(bg, 0, 0, w, h, this);

            // overlay para que se lea el texto
            int alpha = Math.min(255, Math.max(0, (int) (overlayAlpha * 255)));
            g2.setColor(new Color(0, 0, 0, alpha));
            g2.fillRect(0, 0, w, h);

            g2.dispose();
        }
    }

