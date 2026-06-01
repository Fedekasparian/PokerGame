package main.Vista.Grafica.util;

import javax.swing.*;
import java.awt.*;

public class CardLabel extends JLabel {
    private boolean selected = false;

    public CardLabel(ImageIcon icon) {
        setIcon(icon);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(2,2,2,2));

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                toggleSelected();
            }
        });
    }

    public boolean isSelected() { return selected; }

    public void setSelected(boolean value) {
        selected = value;
        setBorder(selected
                ? BorderFactory.createLineBorder(new Color(255,165,0), 2, true)
                : BorderFactory.createEmptyBorder(2,2,2,2));
        repaint();
    }

    public void toggleSelected() { setSelected(!selected); }
}