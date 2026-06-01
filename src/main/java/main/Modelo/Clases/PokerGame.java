package main.Modelo.Clases;

import main.Excepciones.ReglaJuegoException;
import main.Modelo.Clases.entidades.JugadorHistorial;
import main.Modelo.DTOs.EstadoMesaDTO;
import main.Modelo.DTOs.InfoJugadorDTO;
import main.Modelo.Enums.Eventos;
import main.Modelo.Enums.Fase;
import main.Modelo.Interfaces.IJugador;
import main.Modelo.Interfaces.IPokerGame;
import ar.edu.unlu.rmimvc.observer.ObservableRemoto;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PokerGame extends ObservableRemoto implements IPokerGame {

    private static final String ARCHIVO_HISTORIAL = "actual.dat";
    private static PokerGame instancia;

    private final Pozo pozo;
    private final AdminJugadores administrador;
    private final Dealer dealer;
    private final EvaluadorMano evaluadorMano;
    private final RepositorioArchivoHistorial historialRepositorio;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean reinicioProgramado;

    private Fase fase;
    private int contadorFase;
    private String descripcionJuegoGanador;

    public PokerGame() throws RemoteException {
        this.dealer = new Dealer();
        this.pozo = new Pozo();
        this.administrador = new AdminJugadores();
        this.evaluadorMano = new EvaluadorMano();
        this.historialRepositorio = new RepositorioArchivoHistorial(ARCHIVO_HISTORIAL);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.reinicioProgramado = new AtomicBoolean(false);
        iniciarJuego();
    }

    public static PokerGame getInstanciaJuego() throws RemoteException {
        if (instancia == null) instancia = new PokerGame();
        return instancia;
    }

    // ------------------ Estado de la partida ------------------

    @Override
    public EstadoMesaDTO getEstadoParaJugador(String alias) throws RemoteException {
        boolean enShowdown = (this.fase == Fase.FINALIZADO);

        InfoJugadorDTO miInfo = null;
        List<InfoJugadorDTO> enMesa = new ArrayList<>();

        for (IJugador jug : administrador.getJugadoresMesa()) {
            boolean retirado = administrador.estaRetirado(jug.getAlias());
            boolean esMio = Objects.equals(jug.getAlias(), alias);
            List<Carta> manoVisible = null;
            if (esMio || (enShowdown && !retirado)) {
                manoVisible = jug.getMano();
            }

            InfoJugadorDTO info = new InfoJugadorDTO(
                    jug.getAlias(),
                    jug.getSaldo(),
                    jug.getMontoApostado(),
                    jug.getMontoApostadoFase(),
                    jug.esAllIn(),
                    retirado,
                    jug.haRecargado(),
                    manoVisible
            );

            enMesa.add(info);
            if (esMio) miInfo = info;
        }

        if (miInfo == null) {
            IJugador miJug = administrador.getJugador(alias);
            if (miJug != null) {
                miInfo = new InfoJugadorDTO(
                        miJug.getAlias(),
                        miJug.getSaldo(),
                        miJug.getMontoApostado(),
                        miJug.getMontoApostadoFase(),
                        miJug.esAllIn(),
                        false,
                        miJug.haRecargado(),
                        miJug.getMano()
                );
            } else {
                miInfo = new InfoJugadorDTO(alias, 0, 0, 0, false, false, false, Collections.emptyList());
            }
        }

        return new EstadoMesaDTO(
                this.fase,
                this.pozo.getCantidad(),
                this.pozo.getMontoAIgualar(),
                administrador.quienTieneTurno(),
                administrador.getUltimoGanador(),
                miInfo,
                enMesa,
                enShowdown
        );
    }

    @Override
    public Fase getFasePartida() {
        return this.fase;
    }

    @Override
    public String getTurno() throws RemoteException {
        return administrador.quienTieneTurno();
    }

    @Override
    public String getUltimoJugador() {
        return administrador.getUltimoJugador();
    }

    @Override
    public String getUltimoGanador() {
        return administrador.getUltimoGanador();
    }

    @Override
    public String getDescripcionJuegoGanador() {
        return descripcionJuegoGanador;
    }

    @Override
    public double getSaldo(String alias) {
        return administrador.getSaldo(alias);
    }

    @Override
    public double getMontoApostado(String alias) {
        return administrador.getMontoApostado(alias);
    }

    @Override
    public List<JugadorHistorial> top5PorManosGanadas() {
        return historialRepositorio.top5PorManosGanadas();
    }

    // ------------------ Login ------------------

    @Override
    public boolean registrarNuevoJugador(String alias, String contrasena, double saldo) {
        return administrador.registrarNuevoJugador(alias, contrasena, saldo);
    }

    @Override
    public boolean ingresarJugador(String alias, String contrasena, double saldo) throws RemoteException {
        try {
            if (administrador.getJugadoresMesa().size() < 7) {
                administrador.iniciarSesion(alias, contrasena);
                administrador.agregarJugador(alias, this.fase);
                notificarObservadores(Eventos.NUEVO_JUGADOR);
                if (administrador.getJugadoresMesa().size() >= 2 && this.fase == Fase.SIN_COMENZAR) {
                    iniciarJuego();
                }
            } else {
                notificarObservadores(Eventos.NO_SE_PUDO_INGRESAR_JUGADOR_NUEVO);
            }
            return true;
        } catch (RemoteException | ReglaJuegoException e) {
            throw new RuntimeException(e);
        }
    }

    // ------------------ Acciones del jugador ------------------

    @Override
    public void apostar(String alias, Double monto) throws RemoteException {
        boolean allIn = administrador.esAllIn(alias);
        double saldo = administrador.getSaldo(alias);

        if (monto <= 0) {
            throw new ReglaJuegoException("El monto apostado debe ser mayor a 0");
        }
        if (monto > saldo) {
            throw new ReglaJuegoException("Saldo insuficiente");
        }
        double faltaIgualar = pozo.getMontoAIgualar() - administrador.getMontoApostadoFase(alias);
        boolean apuestaTodoElSaldo = monto == saldo;
        if (monto < faltaIgualar && !allIn && !apuestaTodoElSaldo) {
            throw new ReglaJuegoException("El monto debe ser igual o mayor a: " + faltaIgualar);
        }
        if (!puedeApostar()) {
            throw new ReglaJuegoException("Espere a la ronda de apuestas para apostar");
        }

        try {
            if (administrador.jugadorApuesta(alias, monto)) {
                if (administrador.getMontoApostadoFase(alias) > pozo.getMontoAIgualar()) {
                    contadorFase = 1;
                } else {
                    contadorFase++;
                }
                agregarMontoAlPozo(monto);
                pozo.setMontoAIgualar(monto);
                notificarObservadores(Eventos.APUESTA);
                evaluarFinFase();
            } else {
                notificarObservadores(Eventos.NO_PUDO_APOSTAR);
            }
        } catch (RuntimeException | RemoteException e) {
            notificarObservadores(Eventos.NO_PUDO_APOSTAR);
        }
    }

    @Override
    public void igualar(String alias) throws RemoteException {
        double faltaIgualar = pozo.getMontoAIgualar() - administrador.getMontoApostadoFase(alias);
        double saldo = administrador.getSaldo(alias);
        double aApostar = Math.min(faltaIgualar, saldo);
        apostar(alias, aApostar);
    }

    @Override
    public void pasar(String alias) throws RemoteException {
        double faltaIgualar = pozo.getMontoAIgualar() - administrador.getMontoApostadoFase(alias);
        boolean allIn = administrador.esAllIn(alias);
        if (faltaIgualar > 0 && !allIn) {
            throw new ReglaJuegoException("No podes pasar, minimo tenes que apostar: " + faltaIgualar);
        }
        administrador.jugadorPasa();
        contadorFase++;
        evaluarFinFase();
        notificarObservadores(Eventos.PASAR);
    }

    @Override
    public void descartar(String alias, int ubicacion) throws RemoteException {
        administrador.jugadorDescarta(alias, ubicacion);
        notificarObservadores(Eventos.DESCARTA);
    }

    @Override
    public void retirarJugadorDeMano(String alias) throws RemoteException {
        administrador.retirarJugadorMano(alias);
        if (administrador.getJugadoresJugando().size() == 1) {
            terminarJuego();
            notificarObservadores(Eventos.JUEGO_TERMINADO);
            programarSiguientePartida(15);
        } else {
            notificarObservadores(Eventos.CAMBIO_TURNO);
        }
        notificarObservadores(Eventos.JUGADOR_SE_RETIRO);
    }

    @Override
    public void retirarJugadorDeMesa(String alias) throws RemoteException {
        administrador.retirarJugadorMesa(alias);
        if (administrador.getJugadoresJugando().size() == 1) {
            terminarJuego();
            notificarObservadores(Eventos.JUEGO_TERMINADO);
        } else {
            notificarObservadores(Eventos.CAMBIO_TURNO);
        }
    }

    @Override
    public void recargarFichas(String alias) throws RemoteException {
        if (this.fase != Fase.APUESTA_INICIAL) {
            throw new ReglaJuegoException("Solo podes recargar fichas durante la ronda de apuesta inicial");
        }
        administrador.recargarFichas(alias);
        notificarObservadores(Eventos.RECARGA_FICHAS);
    }

    // ------------------ Ciclo de partida ------------------

    @Override
    public void iniciarNuevaPartida() throws RemoteException {
        int enMesa = administrador.getJugadoresMesa().size();
        if (enMesa >= 2 && enMesa <= 7) {
            resetNuevaPartida();
            arrancarPartida();
        } else {
            this.fase = Fase.SIN_COMENZAR;
            notificarObservadores(Eventos.ESPERANDO_JUGADORES);
        }
    }

    @Override
    public void siguienteFase() throws RemoteException {
        switch (this.fase) {
            case SIN_COMENZAR -> {
                this.fase = Fase.INICIADO;
                notificarObservadores(Eventos.INICIADO);
            }
            case INICIADO -> {
                this.fase = Fase.APUESTA_INICIAL;
                contadorFase = 0;
                notificarObservadores(Eventos.APUESTA_INICIAL);
                siguienteRondaEn(7);
            }
            case APUESTA_INICIAL -> {
                this.fase = Fase.DESCARTE;
                resetearMontosApuestaFase(administrador.getJugadoresJugando());
                contadorFase = 0;
                notificarObservadores(Eventos.DESCARTE);
            }
            case DESCARTE -> {
                dealer.repartirCartasFaltantes(administrador.getJugadoresJugando());
                resetearMontosApuestaFase(administrador.getJugadoresJugando());
                contadorFase = 0;
                this.fase = Fase.APUESTA_FINAL;
                notificarObservadores(Eventos.APUESTA_FINAL);
            }
            case APUESTA_FINAL -> {
                this.fase = Fase.FINALIZADO;
                terminarJuego();
                notificarObservadores(Eventos.JUEGO_TERMINADO);
                programarSiguientePartida(15);
            }
            case FINALIZADO -> {
                this.fase = Fase.SIN_COMENZAR;
                iniciarNuevaPartida();
            }
        }
    }

    @Override
    public void siguienteTurno() throws RemoteException {
        administrador.siguienteTurno();
        notificarObservadores(Eventos.CAMBIO_TURNO);
        pasarAutomaticoSiAllIn();
    }

    private void pasarAutomaticoSiAllIn() {
        String turno = administrador.quienTieneTurno();
        if (turno.isEmpty()) return;
        if (!administrador.esAllIn(turno)) return;
        if (this.fase != Fase.APUESTA_INICIAL && this.fase != Fase.APUESTA_FINAL) return;

        scheduler.schedule(() -> {
            try {
                if (this.fase == Fase.APUESTA_INICIAL || this.fase == Fase.APUESTA_FINAL) {
                    pasar(turno);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }, 1, TimeUnit.SECONDS);
    }

    @Override
    public void terminarJuego() throws RemoteException {
        resetearMontosApuestaFase(administrador.getJugadoresJugando());
        evaluarGanador();
        entregarPozoAlGanador();
        historialRepositorio.registrarManoGanada(administrador.getUltimoGanador());
        contadorFase = 0;
    }

    @Override
    public void evaluarGanador() {
        Queue<IJugador> jugadores = administrador.getJugadoresJugando();
        if (jugadores.isEmpty()) return;

        IJugador ganador = null;
        ValorMano mejor = null;

        for (IJugador jug : jugadores) {
            ValorMano hv = evaluadorMano.evaluar(jug.getMano());
            if (mejor == null || hv.compareTo(mejor) > 0) {
                mejor = hv;
                ganador = jug;
            }
        }
        administrador.setUltimoGanador(ganador.getAlias());
        this.descripcionJuegoGanador = mejor.getDescripcion();
    }

    @Override
    public void repartirCarta() {
        dealer.repartirCartasAJugadores(administrador.getJugadoresJugando());
    }

    // ------------------ Helpers privados ------------------

    private void iniciarJuego() throws RemoteException {
        iniciarNuevaPartida();
    }

    private void resetNuevaPartida() {
        this.fase = Fase.SIN_COMENZAR;
        administrador.partidaNueva();
        dealer.resetearJuego();
        pozo.resetearJuego();
    }

    private void arrancarPartida() throws RemoteException {
        this.fase = Fase.INICIADO;
        notificarObservadores(Eventos.INICIADO);
        repartirCarta();

        scheduler.schedule(() -> {
            try {
                if (this.fase == Fase.INICIADO) {
                    siguienteFase();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }, 5, TimeUnit.SECONDS);
    }

    private void siguienteRondaEn(int segundos) {
        scheduler.schedule(() -> {
            try {
                if (this.fase == Fase.INICIADO || this.fase == Fase.FINALIZADO) {
                    siguienteFase();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }, segundos, TimeUnit.SECONDS);
    }

    private void programarSiguientePartida(int segundos) {
        if (!reinicioProgramado.compareAndSet(false, true)) {
            return;
        }
        scheduler.schedule(() -> {
            try {
                iniciarNuevaPartida();
            } catch (RemoteException e) {
                e.printStackTrace();
            } finally {
                reinicioProgramado.set(false);
            }
        }, segundos, TimeUnit.SECONDS);
    }

    private void evaluarFinFase() throws RemoteException {
        if (fase == Fase.APUESTA_INICIAL || fase == Fase.APUESTA_FINAL || fase == Fase.DESCARTE) {
            if (finFase()) {
                siguienteFase();
            }
            siguienteTurno();
        }
    }

    private boolean finFase() {
        return contadorFase == administrador.getJugadoresJugando().size();
    }

    private boolean puedeApostar() {
        return fase == Fase.APUESTA_INICIAL || fase == Fase.APUESTA_FINAL;
    }

    private void resetearMontosApuestaFase(Queue<IJugador> jugadores) {
        for (IJugador jug : jugadores) {
            jug.setMontoApostadoFase(0);
        }
        pozo.setMontoAIgualar(0);
    }

    private void agregarMontoAlPozo(Double monto) {
        pozo.setMontoAIgualar(monto);
        pozo.agregarAlPozo(monto);
    }

    private void entregarPozoAlGanador() {
        double cantidad = pozo.getCantidad();
        administrador.entregarPozo(cantidad);
        pozo.resetearJuego();
    }
}
