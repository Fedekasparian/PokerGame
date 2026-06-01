package main.Modelo.DTOs;

import main.Modelo.Clases.Carta;

import java.io.Serializable;
import java.util.List;

public class InfoJugadorDTO implements Serializable {

    private final String alias;
    private final double saldo;
    private final double montoApostado;
    private final double montoApostadoFase;
    private final boolean allIn;
    private final boolean retirado;
    private final boolean haRecargado;
    private final List<Carta> mano;

    public InfoJugadorDTO(String alias, double saldo, double montoApostado,
                          double montoApostadoFase, boolean allIn,
                          boolean retirado, boolean haRecargado, List<Carta> mano) {
        this.alias = alias;
        this.saldo = saldo;
        this.montoApostado = montoApostado;
        this.montoApostadoFase = montoApostadoFase;
        this.allIn = allIn;
        this.retirado = retirado;
        this.haRecargado = haRecargado;
        this.mano = mano;
    }

    public String getAlias() {
        return alias;
    }

    public double getSaldo() {
        return saldo;
    }

    public double getMontoApostado() {
        return montoApostado;
    }

    public double getMontoApostadoFase() {
        return montoApostadoFase;
    }

    public boolean isAllIn() {
        return allIn;
    }

    public boolean isRetirado() {
        return retirado;
    }

    public boolean isHaRecargado() {
        return haRecargado;
    }

    public List<Carta> getMano() {
        return mano;
    }
}
