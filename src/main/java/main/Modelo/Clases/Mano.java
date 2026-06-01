package main.Modelo.Clases;

import main.Excepciones.ReglaJuegoException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mano implements Serializable {

    private final List<Carta> cartas;

    public Mano() {
        this.cartas = new ArrayList<>();
    }

    public void agregar(Carta carta) {
        cartas.add(carta);
    }

    public void descartar(int ubicacion) {
        if (ubicacion < 0 || ubicacion >= cartas.size()) {
            throw new ReglaJuegoException("Debe elegir una ubicacion valida para descartar");
        }
        cartas.remove(ubicacion);
    }

    public void limpiar() {
        cartas.clear();
    }

    public List<Carta> cartas() {
        return List.copyOf(cartas);
    }
}
