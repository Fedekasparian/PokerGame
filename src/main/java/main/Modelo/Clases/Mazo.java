package main.Modelo.Clases;

import main.Modelo.Enums.Palo;
import main.Modelo.Enums.Valor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mazo implements Serializable {

    private final List<Carta> cartas;

    public Mazo() {
        this.cartas = new ArrayList<>();
        for (Palo p : Palo.values()) {
            for (Valor v : Valor.values()) {
                cartas.add(new Carta(p, v));
            }
        }
    }

    public void mezclar() {
        Collections.shuffle(cartas);
    }

    public Carta sacarCarta() {
        return cartas.removeFirst();
    }
}
