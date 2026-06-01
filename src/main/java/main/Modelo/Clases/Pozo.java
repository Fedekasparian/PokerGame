package main.Modelo.Clases;

import java.io.Serializable;

public class Pozo implements Serializable {

    private double cantidad;
    private double montoAIgualar;

    public Pozo() {
        this.cantidad = 0;
        this.montoAIgualar = 0;
    }

    public void agregarAlPozo(double monto) {
        this.cantidad += monto;
    }

    public void resetearJuego() {
        this.cantidad = 0;
        this.montoAIgualar = 0;
    }

    public double getCantidad() {
        return cantidad;
    }

    public double getMontoAIgualar() {
        return montoAIgualar;
    }

    public void setMontoAIgualar(double monto) {
        this.montoAIgualar = monto;
    }
}
