package main.Modelo.Interfaces;

import main.Modelo.Clases.entidades.JugadorHistorial;
import main.Modelo.DTOs.EstadoMesaDTO;
import main.Modelo.Enums.Fase;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

import java.rmi.RemoteException;
import java.util.List;

public interface IPokerGame extends IObservableRemoto {

    // ------------------ Estado de la partida ------------------

    EstadoMesaDTO getEstadoParaJugador(String alias) throws RemoteException;

    Fase getFasePartida() throws RemoteException;

    String getTurno() throws RemoteException;

    String getUltimoJugador() throws RemoteException;

    String getUltimoGanador() throws RemoteException;

    String getDescripcionJuegoGanador() throws RemoteException;

    double getSaldo(String alias) throws RemoteException;

    double getMontoApostado(String alias) throws RemoteException;

    List<JugadorHistorial> top5PorManosGanadas() throws RemoteException;

    // ------------------ Login ------------------

    boolean registrarNuevoJugador(String alias, String contrasena, double saldo) throws RemoteException;

    boolean ingresarJugador(String alias, String contrasena, double saldo) throws RemoteException;

    // ------------------ Acciones ------------------

    void apostar(String alias, Double monto) throws RemoteException;

    void igualar(String alias) throws RemoteException;

    void pasar(String alias) throws RemoteException;

    void descartar(String alias, int ubicacion) throws RemoteException;

    void retirarJugadorDeMano(String alias) throws RemoteException;

    void retirarJugadorDeMesa(String alias) throws RemoteException;

    void recargarFichas(String alias) throws RemoteException;

    // ------------------ Ciclo de partida ------------------

    void iniciarNuevaPartida() throws RemoteException;

    void siguienteFase() throws RemoteException;

    void siguienteTurno() throws RemoteException;

    void terminarJuego() throws RemoteException;

    void evaluarGanador() throws RemoteException;

    void repartirCarta() throws RemoteException;
}
