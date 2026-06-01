package main.Vista.Grafica.util;

import main.Modelo.Clases.Carta;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CardIconCache {

    private final Map<String, ImageIcon> cache = new HashMap<>();
    private final int w, h;

    public CardIconCache(int w, int h) {
        this.w = w;
        this.h = h;
    }

    public ImageIcon getIcon(Carta c) {
        String folder = c.getPalo().carpeta();
        String codigo = c.getValor().siglaArchivo() + c.getPalo().letra();
        String key = folder + "/" + codigo;
        return cache.computeIfAbsent(key, k -> cargarNombreArchivo(folder, codigo));
    }
    private ImageIcon cargarNombreArchivo(String folder, String codigo) {
        String path = "/resources/img/" + folder + "/" + codigo + ".jpg"; // <-- TU estructura real
        URL url = getClass().getResource(path);

        if (url == null) {
            System.out.println("No encuentro imagen: " + path);
            return new ImageIcon();
        }

        try {
            BufferedImage src = ImageIO.read(url);
            if (src == null) {
                System.out.println("ImageIO no pudo leer: " + path);
                return new ImageIcon();
            }
            // Convertir a RGB (evita CMYK/YCCK)
            BufferedImage rgb = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = rgb.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(src, 0, 0, null);
            g.dispose();
            Image scaled = rgb.getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);

        } catch (Exception e) {
            System.out.println("Error leyendo imagen: " + path);
            e.printStackTrace();
            return new ImageIcon();
        }
    }

}

