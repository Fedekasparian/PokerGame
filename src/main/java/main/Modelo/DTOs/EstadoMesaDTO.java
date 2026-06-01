package main.Modelo.DTOs;

import main.Modelo.Enums.Fase;

import java.io.Serializable;
import java.util.List;

public class EstadoMesaDTO implements Serializable {

    private final Fase fase;
    private final double pozo;
    private final double montoAIgualar;
    private final String turnoActual;
    private final String ultimoGanador;
    private final InfoJugadorDTO miInfo;
    private final List<InfoJugadorDTO> jugadoresEnMesa;
    private final boolean enShowdown;

    public EstadoMesaDTO(Fase fase, double pozo, double montoAIgualar,
                         String turnoActual, String ultimoGanador,
                         InfoJugadorDTO miInfo, List<InfoJugadorDTO> jugadoresEnMesa,
                         boolean enShowdown) {
        this.fase = fase;
        this.pozo = pozo;
        this.montoAIgualar = montoAIgualar;
        this.turnoActual = turnoActual;
        this.ultimoGanador = ultimoGanador;
        this.miInfo = miInfo;
        this.jugadoresEnMesa = jugadoresEnMesa;
        this.enShowdown = enShowdown;
    }

    public Fase getFase() {
        return fase;
    }

    public double getPozo() {
        return pozo;
    }

    public double getMontoAIgualar() {
        return montoAIgualar;
    }

    public String getTurnoActual() {
        return turnoActual;
    }

    public String getUltimoGanador() {
        return ultimoGanador;
    }

    public InfoJugadorDTO getMiInfo() {
        return miInfo;
    }

    public List<InfoJugadorDTO> getJugadoresEnMesa() {
        return jugadoresEnMesa;
    }

    public boolean isEnShowdown() {
        return enShowdown;
    }
}
