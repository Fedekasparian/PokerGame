package main.Vista.Grafica.mesa;

import javax.swing.*;
import java.awt.*;

public class CenterPotPanel extends JPanel {
    private final JLabel lblPozo = new JLabel("POZO: 0");
    private final JLabel lblFase = new JLabel("FASE: -");
    private final JLabel lblIgualar = new JLabel("A IGUALAR: 0");
    private final JLabel lblUltima = new JLabel(" ");

    public CenterPotPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        lblPozo.setFont(lblPozo.getFont().deriveFont(Font.BOLD, 20f));
        lblPozo.setForeground(Color.WHITE);

        for (JLabel l : new JLabel[]{lblFase, lblIgualar, lblUltima}) {
            l.setForeground(new Color(230, 230, 230));
            l.setFont(l.getFont().deriveFont(Font.PLAIN, 14f));
        }

        lblPozo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblFase.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblIgualar.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblUltima.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(lblPozo);
        add(Box.createVerticalStrut(6));
        add(lblFase);
        add(Box.createVerticalStrut(4));
        add(lblIgualar);
        add(Box.createVerticalStrut(8));
        add(lblUltima);
    }

    public void update(double pozo, String fase, double aIgualar, String ultimaAccion) {
        lblPozo.setText("Pozo: " + fmt(pozo));
        lblFase.setText("Ronda: " + fase);
        lblIgualar.setText("A igualar: " + fmt(aIgualar));
        lblUltima.setText(ultimaAccion == null ? " " : "Turno de: " + ultimaAccion);


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 22;
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        g2.setColor(new Color(255, 215, 0, 140));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, arc, arc);

        g2.dispose();
    }

    private String fmt(double d) {
        if (d == (long) d) return String.valueOf((long) d);
        return String.format(java.util.Locale.US, "%.2f", d);
    }
}

