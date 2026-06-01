package main.Modelo.Clases.entidades;

import main.Modelo.Enums.Fase;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class EstadoJuego implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<JugadorHistorial> registroJugadores = new ArrayList<>();

    public List<JugadorHistorial> getRegistroJugadores() { return registroJugadores; }
    public void setRegistroJugadores(List<JugadorHistorial> registroJugadores) {
        this.registroJugadores = registroJugadores;
    }
}
