package main.Vista.Grafica.mesa;

import javax.swing.*;
import java.awt.*;
import java.util.function.DoubleConsumer;

public class BetPanel extends JPanel {
    private final JSlider slider;
    private final JLabel lblValue;

    private final JButton btn10 = new JButton("+10");
    private final JButton btn50 = new JButton("+50");
    private final JButton btn100 = new JButton("+100");
    private final JButton btnAllIn = new JButton("ALL IN");
    private final JButton btnConfirm = new JButton("CONFIRMAR");

    public BetPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("APUESTA");
        title.setForeground(Color.WHITE);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblValue = new JLabel("0");
        lblValue.setForeground(new Color(255, 215, 0));
        lblValue.setFont(lblValue.getFont().deriveFont(Font.BOLD, 18f));
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        slider = new JSlider(0, 0, 0);
        slider.setOpaque(false);
        slider.addChangeListener(e -> lblValue.setText(String.valueOf(slider.getValue())));

        JPanel quick = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        quick.setOpaque(false);

        stylizeSmall(btn10);
        stylizeSmall(btn50);
        stylizeSmall(btn100);
        stylizeSmall(btnAllIn);

        quick.add(btn10);
        quick.add(btn50);
        quick.add(btn100);
        quick.add(btnAllIn);

        stylizePrimary(btnConfirm);

        add(title);
        add(Box.createVerticalStrut(6));
        add(lblValue);
        add(Box.createVerticalStrut(8));
        add(slider);
        add(Box.createVerticalStrut(8));
        add(quick);
        add(Box.createVerticalStrut(10));
        add(btnConfirm);

        wireQuickButtons();
    }

    public void setMax(double max) {
        int m = (int) Math.max(0, Math.floor(max));
        slider.setMaximum(m);
        if (slider.getValue() > m) slider.setValue(m);
        lblValue.setText(String.valueOf(slider.getValue()));
    }

    public void setEnabledAll(boolean enabled) {
        slider.setEnabled(enabled);
        btn10.setEnabled(enabled);
        btn50.setEnabled(enabled);
        btn100.setEnabled(enabled);
        btnAllIn.setEnabled(enabled);
        btnConfirm.setEnabled(enabled);
    }

    public void onConfirm(DoubleConsumer handler) {
        btnConfirm.addActionListener(e -> handler.accept(slider.getValue()));
    }

    private void wireQuickButtons() {
        btn10.addActionListener(e -> addToSlider(10));
        btn50.addActionListener(e -> addToSlider(50));
        btn100.addActionListener(e -> addToSlider(100));
        btnAllIn.addActionListener(e -> slider.setValue(slider.getMaximum()));
    }

    private void addToSlider(int delta) {
        int v = slider.getValue() + delta;
        if (v > slider.getMaximum()) v = slider.getMaximum();
        slider.setValue(v);
    }

    private void stylizeSmall(JButton b) {
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 12f));
        b.setPreferredSize(new Dimension(80, 28));
    }

    private void stylizePrimary(JButton b) {
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 12f));
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setPreferredSize(new Dimension(160, 34));
        b.setMaximumSize(new Dimension(220, 34));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 18;
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

        g2.setColor(new Color(255, 215, 0, 120));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, arc, arc);

        g2.dispose();
    }
}

