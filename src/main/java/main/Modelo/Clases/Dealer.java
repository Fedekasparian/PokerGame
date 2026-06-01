package main.Modelo.Clases;

import main.Modelo.Interfaces.IJugador;

import java.io.Serializable;
import java.util.Queue;

public class Dealer implements Serializable {

    private static final int CARTAS_POR_JUGADOR = 5;

    private Mazo mazo;

    public Dealer() {
        this.mazo = new Mazo();
        this.mazo.mezclar();
    }

    public void resetearJuego() {
        this.mazo = new Mazo();
        this.mazo.mezclar();
    }

    public void repartirCartasAJugadores(Queue<IJugador> jugadores) {
        for (IJugador jugador : jugadores) {
            for (int i = 0; i < CARTAS_POR_JUGADOR; i++) {
                jugador.recibirCarta(mazo.sacarCarta());
            }
        }
    }

    public void repartirCartasFaltantes(Queue<IJugador> jugadores) {
        for (IJugador jugador : jugadores) {
            for (int i = jugador.getMano().size(); i < CARTAS_POR_JUGADOR; i++) {
                jugador.recibirCarta(mazo.sacarCarta());
            }
        }
    }
}
