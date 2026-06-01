package main.Modelo.Interfaces;

import main.Modelo.Clases.Carta;

import java.util.List;

public interface IJugador {

    // ------------------ Acciones ------------------

    void apostar(double monto);

    void recibirCarta(Carta carta);

    void descartar(int ubicacion);

    void sumarPozo(double monto);

    void recargarFichas();

    void limpiarMano();

    boolean iniciarSesion(String alias, String contrasena);

    // ------------------ Consulta ------------------

    String getAlias();

    double getSaldo();

    List<Carta> getMano();

    double getMontoApostado();

    double getMontoApostadoFase();

    boolean esAllIn();

    boolean haRecargado();

    void setMontoApostado(Double monto);

    void setMontoApostadoFase(double monto);

    void setAllIn(boolean allIn);
}
