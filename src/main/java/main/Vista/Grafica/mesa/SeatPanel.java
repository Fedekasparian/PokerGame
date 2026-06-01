package main.Vista.Grafica.mesa;

import main.Modelo.Clases.Carta;
import main.Vista.Grafica.util.CardIconCache;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SeatPanel extends JPanel {
    private final JLabel lblAlias = new JLabel("-");
    private final JLabel lblStack = new JLabel("Fichas: 0");
    private final JLabel lblBet = new JLabel("Apuesta: 0");
    private final JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));

    private boolean isTurn = false;
    private boolean isOut = false;
    private boolean showCards = false;

    private final ImageIcon backIcon; // dorso
    private final CardIconCache iconCache;

    public SeatPanel(ImageIcon backIcon, CardIconCache iconCache) {
        this.backIcon = backIcon;
        this.iconCache = iconCache;

        setOpaque(false);
        setLayout(new BorderLayout(0, 4));
        lblAlias.setForeground(Color.WHITE);
        lblAlias.setFont(lblAlias.getFont().deriveFont(Font.BOLD, 12f));
        lblStack.setForeground(new Color(220, 220, 220));
        lblBet.setForeground(new Color(220, 220, 220));

        JPanel top = new JPanel(new GridLayout(3, 1));
        top.setOpaque(false);
        top.add(lblAlias);
        top.add(lblStack);
        top.add(lblBet);

        cardsPanel.setOpaque(false);

        add(top, BorderLayout.NORTH);
        add(cardsPanel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
    }

    public void update(String alias, double saldo, double betFase,
                       boolean turno, boolean retirado, boolean allIn,
                       boolean showCards, List<Carta> mano) {

        this.isTurn = turno;
        this.isOut = retirado;
        this.showCards = showCards;

        String nombre = alias;
        if (allIn) nombre += " (ALL-IN)";
        if(retirado) nombre += " (RETIRADO)";
        lblAlias.setText(nombre);
        lblStack.setText("Fichas: " + fmt(saldo));
        lblBet.setText("Apuesta: " + fmt(betFase));

        // color alias
        lblAlias.setForeground(retirado ? new Color(180, 120, 120) : Color.WHITE);

        // cartas
        cardsPanel.removeAll();
        if (mano == null) mano = java.util.Collections.emptyList();

        for (int i = 0; i < 5; i++) {
            JLabel c = new JLabel();
            if (showCards && mano.size() == 5) {
                c.setIcon(iconCache.getIcon(mano.get(i)));
            } else {
                c.setIcon(backIcon);
            }
            cardsPanel.add(c);
        }

        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // “card glass” simple
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 18;
        g2.setColor(new Color(0, 0, 0, 140));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        // Halo dorado si es turno
        if (isTurn && !isOut) {
            g2.setStroke(new BasicStroke(3f));
            g2.setColor(new Color(255, 215, 0, 220));
            g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, arc, arc);
        } else {
            g2.setColor(new Color(255, 215, 0, 80));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arc, arc);
        }

        g2.dispose();
    }

    private String fmt(double d) {
        if (d == (long) d) return String.valueOf((long) d);
        return String.format(java.util.Locale.US, "%.2f", d);
    }
}

