package main.Vista.Grafica.mesa;

import javax.swing.*;
import java.awt.*;

public class TablePanel extends JPanel {
    private final SeatsRingPanel seats;
    private final CenterPotPanel center;

    public TablePanel(SeatsRingPanel seats, CenterPotPanel center) {
        this.seats = seats;
        this.center = center;

        setOpaque(false);
        setLayout(new GridBagLayout());

        // capa base: un panel null para ubicar centro sobre asientos
        JPanel layer = new JPanel(null);
        layer.setOpaque(false);

        layer.add(seats);
        layer.add(center);

        add(layer, new GridBagConstraints());
    }

    @Override
    public void doLayout() {
        super.doLayout();
        Component layer = getComponent(0);
        layer.setBounds(0, 0, getWidth(), getHeight());

        seats.setBounds(0, 0, layer.getWidth(), layer.getHeight());

        // Centro: más chico y más abajo para que no choque con asientos superiores
        int cw = Math.min(380, (int)(layer.getWidth() * 0.40));
        int ch = 140;

        int cx = layer.getWidth()/2 - cw/2;
        int cy = layer.getHeight()/2 - ch/2 + 10; // +10 lo baja un poco

        center.setBounds(cx, cy, cw, ch);
    }
}

