package main.Modelo.Clases;

import main.Excepciones.ReglaJuegoException;
import main.Modelo.Interfaces.IJugador;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Jugador implements IJugador, Serializable {

    private static final double MONTO_RECARGA = 3000;

    private final String alias;
    private final String contrasena;
    private double saldo;
    private final Mano mano;
    private double montoApostado;
    private double montoApostadoFase;
    private boolean allIn;
    private boolean recargado;

    public Jugador(String alias, String contrasena, double saldo) {
        this.alias = alias;
        this.contrasena = contrasena;
        this.saldo = saldo;
        this.mano = new Mano();
        this.montoApostado = 0;
        this.montoApostadoFase = 0;
        this.allIn = false;
        this.recargado = false;
    }

    // ------------------ Acciones ------------------

    @Override
    public void apostar(double monto) {
        if (monto > this.saldo) {
            throw new ReglaJuegoException("No tenes dinero suficiente para apostar");
        }
        this.saldo -= monto;
        this.montoApostado += monto;
        this.montoApostadoFase += monto;
        if (this.saldo == 0) {
            this.allIn = true;
        }
    }

    @Override
    public void recibirCarta(Carta carta) {
        this.mano.agregar(carta);
    }

    @Override
    public void descartar(int ubicacion) {
        this.mano.descartar(ubicacion);
    }

    @Override
    public void sumarPozo(double monto) {
        this.saldo += monto;
    }

    @Override
    public void recargarFichas() {
        if (this.saldo != 0) {
            throw new ReglaJuegoException("Solo podes recargar fichas cuando tu saldo sea 0");
        }
        if (this.recargado) {
            throw new ReglaJuegoException("Ya recargaste fichas en esta partida");
        }
        this.saldo += MONTO_RECARGA;
        this.recargado = true;
        this.allIn = false;
    }

    @Override
    public void limpiarMano() {
        this.mano.limpiar();
        this.montoApostado = 0;
    }

    @Override
    public boolean iniciarSesion(String alias, String contrasena) {
        return Objects.equals(this.alias, alias) && Objects.equals(this.contrasena, contrasena);
    }

    // ------------------ Consulta ------------------

    @Override
    public List<Carta> getMano() {
        return mano.cartas();
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public double getSaldo() {
        return saldo;
    }

    @Override
    public double getMontoApostado() {
        return montoApostado;
    }

    @Override
    public double getMontoApostadoFase() {
        return montoApostadoFase;
    }

    @Override
    public boolean esAllIn() {
        return allIn;
    }

    @Override
    public boolean haRecargado() {
        return recargado;
    }

    @Override
    public void setMontoApostadoFase(double monto) {
        this.montoApostadoFase = monto;
    }

    @Override
    public void setMontoApostado(Double monto) {
        this.montoApostado = monto;
    }

    @Override
    public void setAllIn(boolean allIn) {
        this.allIn = allIn;
    }
}
