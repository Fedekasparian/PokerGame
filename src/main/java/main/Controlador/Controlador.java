package main.Controlador;

import main.Modelo.Clases.entidades.JugadorHistorial;
import main.Modelo.DTOs.EstadoMesaDTO;
import main.Modelo.Enums.Eventos;
import main.Modelo.Enums.Fase;
import main.Modelo.Interfaces.IPokerGame;
import main.Vista.IVista;
import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;

import javax.swing.SwingUtilities;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Objects;

public class Controlador implements IControladorRemoto {

    private IVista vista;
    private IPokerGame modelo;
    private String miAlias;

    public Controlador(IVista vista) {
        this.vista = vista;
    }

    // ------------------ Consulta de estado ------------------

    public String getMiAlias() {
        return this.miAlias;
    }

    public EstadoMesaDTO getEstadoMesa() throws RemoteException {
        return modelo.getEstadoParaJugador(this.miAlias);
    }

    public boolean esMiTurno() throws RemoteException {
        return Objects.equals(modelo.getTurno(), this.miAlias);
    }

    public boolean puedeDescartar() throws RemoteException {
        return modelo.getFasePartida() == Fase.DESCARTE;
    }

    public String getDescripcionJuegoGanador() throws RemoteException {
        return modelo.getDescripcionJuegoGanador();
    }

    public List<JugadorHistorial> getTop5() throws RemoteException {
        return modelo.top5PorManosGanadas();
    }

    // ------------------ Login ------------------

    public void registrarse(String alias, String contrasena) throws RemoteException {
        try {
            if (modelo.registrarNuevoJugador(alias, contrasena, 3000)) {
                this.miAlias = alias;
                vista.mostrarMensaje("se ha registrado correctamente el usuario " + alias);
            } else {
                vista.mostrarMensaje("no se pudo registrar correctamente, pruebe cambiando miAlias");
            }
        } catch (RuntimeException | RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public void iniciarSesion(String alias, String contrasena) throws RemoteException {
        try {
            if (modelo.ingresarJugador(alias, contrasena, 3000)) {
                this.miAlias = alias;
                vista.mostrarMensaje("se ha iniciado sesion correctamente, usuario " + alias);
            } else {
                vista.mostrarMensaje("no se pudo iniciar sesion correctamente, pruebe cambiando miAlias o contrasena");
            }
        } catch (RuntimeException | RemoteException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // ------------------ Acciones del jugador ------------------

    public void apostar(Double monto) throws RemoteException {
        modelo.apostar(this.miAlias, monto);
    }

    public void igualar() throws RemoteException {
        modelo.igualar(this.miAlias);
    }

    public void irAllIn() throws RemoteException {
        double saldo = modelo.getSaldo(this.miAlias);
        modelo.apostar(this.miAlias, saldo);
    }

    public void pasar() throws RemoteException {
        modelo.pasar(this.miAlias);
    }

    public void descartarCarta(int ubicacion) throws RemoteException {
        modelo.descartar(this.miAlias, ubicacion);
    }

    public void retirarseDeMano() throws RemoteException {
        modelo.retirarJugadorDeMano(this.miAlias);
        vista.mostrarMensaje(this.miAlias + " se ha retirado de la mano");
    }

    public void retirarseMesa() throws RemoteException {
        modelo.retirarJugadorDeMesa(this.miAlias);
    }

    public void recargarFichas() throws RemoteException {
        modelo.recargarFichas(this.miAlias);
    }

    // ------------------ Observer ------------------

    @Override
    public void actualizar(IObservableRemoto modelo, Object cambio) throws RemoteException {
        Eventos evento = (Eventos) cambio;

        SwingUtilities.invokeLater(() -> {
            try {
                switch (evento) {
                    case ESPERANDO_JUGADORES -> {
                        vista.mostrarPanelJuego();
                        vista.mostrarMensaje("Esperando jugadores para jugar!");
                        vista.cargarDatosPantalla();
                    }
                    case INICIADO -> {
                        vista.mostrarPanelJuego();
                        vista.cargarDatosPantalla();
                    }
                    case APUESTA_INICIAL -> {
                        vista.mostrarMensaje("RONDA DE APUESTA INICIAL");
                        vista.cargarDatosPantalla();
                    }
                    case DESCARTE -> {
                        vista.mostrarMensaje("RONDA DE DESCARTE");
                        vista.cargarDatosPantalla();
                    }
                    case APUESTA_FINAL -> {
                        vista.mostrarMensaje("RONDA DE APUESTA FINAL");
                        vista.cargarDatosPantalla();
                    }
                    case JUEGO_TERMINADO -> {
                        vista.cargarDatosPantalla();
                        vista.mostrarFinalPartida(this.modelo.getUltimoGanador(), getDescripcionJuegoGanador(), 15);
                    }
                    case CAMBIO_TURNO -> {
                        vista.mostrarMensaje("Turno de: " + this.modelo.getTurno());
                        vista.cargarDatosPantalla();
                    }
                    case APUESTA -> {
                        String ultimo = this.modelo.getUltimoJugador();
                        vista.mostrarMensaje(ultimo + " apostó: " + this.modelo.getMontoApostado(ultimo));
                        vista.cargarDatosPantalla();
                    }
                    case NO_PUDO_APOSTAR -> {
                        if (esMiTurno()) {
                            vista.mostrarMensaje("No se pudo apostar. Plata disponible: " + this.modelo.getSaldo(this.miAlias));
                        }
                        vista.cargarDatosPantalla();
                    }
                    case PASAR -> vista.cargarDatosPantalla();
                    case DESCARTA -> {
                        if (esMiTurno()) {
                            vista.mostrarMensaje("Descartaste una carta");
                        }
                        vista.cargarDatosPantalla();
                    }
                    case JUGADOR_SE_RETIRO -> {
                        if (!esMiTurno()) {
                            vista.mostrarMensaje(this.modelo.getUltimoJugador() + " se retiró");
                        }
                        vista.cargarDatosPantalla();
                    }
                    case RECARGA_FICHAS -> {
                        vista.mostrarMensaje(this.modelo.getUltimoJugador() + " recargó 3000 fichas");
                        vista.cargarDatosPantalla();
                    }
                    case NUEVO_JUGADOR -> {
                        vista.mostrarPanelJuego();
                        vista.cargarDatosPantalla();
                    }
                    case NO_SE_PUDO_INGRESAR_JUGADOR_NUEVO -> {
                        vista.mostrarMensaje("No se pudo ingresar jugador nuevo a la mesa");
                        vista.cargarDatosPantalla();
                    }
                    default -> {
                        vista.mostrarMensaje("Evento no identificado");
                        vista.cargarDatosPantalla();
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) throws RemoteException {
        this.modelo = (IPokerGame) modeloRemoto;
    }
}
