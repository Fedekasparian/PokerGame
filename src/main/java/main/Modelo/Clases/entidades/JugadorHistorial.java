package main.Modelo.Clases.entidades;

import java.io.Serial;
import java.io.Serializable;

public class JugadorHistorial implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String alias;
    private int manosGanadas;

    public JugadorHistorial(String alias) {
        this.alias = alias;
    }

    public String getAlias() { return alias; }
    public int getManosGanadas() { return manosGanadas; }

    public void sumarManoGanada() { manosGanadas++; }
}