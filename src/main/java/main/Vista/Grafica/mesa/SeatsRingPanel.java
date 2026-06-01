package main.Vista.Grafica.mesa;

import main.Vista.Grafica.util.CardIconCache;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SeatsRingPanel extends JPanel {

    private final List<SeatPanel> others = new ArrayList<>();
    private int rightGutter = 0;

    public SeatsRingPanel(ImageIcon backIcon, CardIconCache iconCache) {
        setOpaque(false);
        setLayout(null);
        for (int i = 0; i < 6; i++) {
            others.add(new SeatPanel(backIcon, iconCache));
        }
        for (SeatPanel s : others) add(s);
    }

    public void setRightGutter(int px) {
        this.rightGutter = Math.max(0, px);
        revalidate();
        repaint();
    }

    @Override
    public void doLayout() {
        int w = getWidth();
        int h = getHeight();

        int seatW = Math.min(260, (int)(w * 0.24));
        int seatH = 115;

        int padX = 25;
        int topY = 10;
        int topDiagY = 55;
        int midY = h/2 - seatH/2 - 40;
        int botY = h - seatH - 260;

        Point[] pos = new Point[] {
                new Point(w/2 - seatW/2, topY),
                new Point(w - seatW - padX, topDiagY),
                new Point(w - seatW - padX, midY),
                new Point(w - seatW - padX, botY),
                new Point(padX, botY),
                new Point(padX, midY)
        };

        for (int i = 0; i < others.size(); i++) {
            SeatPanel s = others.get(i);

            if (!s.isVisible()) continue;

            s.setBounds(pos[i].x, pos[i].y, seatW, seatH);
        }
    }

    public List<SeatPanel> getOtherSeats() { return others; }
}

