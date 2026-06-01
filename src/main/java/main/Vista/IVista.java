package main.Vista;

import main.Controlador.Controlador;

import java.rmi.RemoteException;

public interface IVista {

    void iniciar() throws RemoteException;

    void setControlador(Controlador controlador);

    void cargarDatosPantalla() throws RemoteException;

    void mostrarMensaje(String mensaje);

    void mostrarPanelJuego() throws RemoteException;

    void mostrarFinalPartida(String ganador, String descripcionJuego, int segundos) throws RemoteException;
}
