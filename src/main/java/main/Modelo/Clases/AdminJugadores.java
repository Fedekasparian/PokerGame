package main.Modelo.Clases;

import main.Excepciones.ReglaJuegoException;
import main.Modelo.Enums.Fase;
import main.Modelo.Interfaces.IJugador;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

public class AdminJugadores implements Serializable {

    private final Queue<IJugador> jugJugando;
    private final Queue<IJugador> jugEnMeza;
    private final Queue<IJugador> jugNuevos;
    private final List<IJugador> registroJugadores;
    private String ultimoJugador;
    private String ultimoGanador;

    public AdminJugadores() {
        this.jugJugando = new LinkedList<>();
        this.jugEnMeza = new LinkedList<>();
        this.jugNuevos = new LinkedList<>();
        this.registroJugadores = new ArrayList<>();
    }

    // ------------------ Login y registro ------------------

    public boolean registrarNuevoJugador(String alias, String contrasena, double saldo) {
        for (IJugador jugador : registroJugadores) {
            if (jugador.getAlias().equalsIgnoreCase(alias)) {
                throw new ReglaJuegoException("El alias ingresado ya existe");
            }
        }
        registroJugadores.add(new Jugador(alias, contrasena, saldo));
        return true;
    }

    public boolean iniciarSesion(String alias, String contrasena) {
        for (IJugador jugador : registroJugadores) {
            if (jugador.iniciarSesion(alias, contrasena)) {
                return true;
            }
        }
        throw new ReglaJuegoException("Debe registrarse primero para iniciar sesión");
    }

    public void agregarJugador(String alias, Fase fase) {
        IJugador jugador = getJugador(alias);
        if (fase == Fase.SIN_COMENZAR) {
            jugEnMeza.add(jugador);
        } else {
            jugNuevos.add(jugador);
        }
    }

    // ------------------ Turnos ------------------

    public void siguienteTurno() {
        IJugador jug = jugJugando.poll();
        if (jug != null) {
            jugJugando.offer(jug);
        }
    }

    public String quienTieneTurno() {
        if (jugJugando.isEmpty()) {
            return "";
        }
        return jugJugando.peek().getAlias();
    }

    // ------------------ Acciones del jugador ------------------

    public boolean jugadorApuesta(String alias, double monto) {
        for (IJugador jugador : jugJugando) {
            if (Objects.equals(jugador.getAlias(), alias)) {
                if (jugador.getSaldo() >= monto) {
                    jugador.apostar(monto);
                    setUltimoJugador(jugador.getAlias());
                    return true;
                }
            }
        }
        return false;
    }

    public void jugadorPasa() {
        setUltimoJugador(quienTieneTurno());
    }

    public void jugadorDescarta(String alias, int ubicacion) {
        for (IJugador jugador : jugJugando) {
            if (Objects.equals(jugador.getAlias(), alias)) {
                jugador.descartar(ubicacion - 1);
            }
        }
        setUltimoJugador(alias);
    }

    public void retirarJugadorMano(String alias) {
        jugJugando.removeIf(j -> Objects.equals(j.getAlias(), alias));
        setUltimoJugador(alias);
    }

    public void retirarJugadorMesa(String alias) {
        jugJugando.removeIf(j -> Objects.equals(j.getAlias(), alias));
        jugEnMeza.removeIf(j -> Objects.equals(j.getAlias(), alias));
        setUltimoJugador(alias);
    }

    public void recargarFichas(String alias) {
        IJugador jugador = getJugador(alias);
        if (jugador == null) {
            throw new ReglaJuegoException("Jugador no encontrado");
        }
        jugador.recargarFichas();
        setUltimoJugador(alias);
    }

    // ------------------ Ciclo de partida ------------------

    public void partidaNueva() {
        jugJugando.clear();
        while (!jugNuevos.isEmpty() && jugEnMeza.size() < 7) {
            jugEnMeza.add(jugNuevos.poll());
        }
        limpiarJugadores();
        jugJugando.addAll(jugEnMeza);
    }

    public void entregarPozo(double pozo) {
        IJugador ganador = getJugador(getUltimoGanador());
        ganador.sumarPozo(pozo);
    }

    private void limpiarJugadores() {
        for (IJugador jug : jugEnMeza) {
            jug.limpiarMano();
            jug.setMontoApostado(0d);
            jug.setAllIn(false);
        }
    }

    // ------------------ Consulta de estado ------------------

    public Queue<IJugador> getJugadoresJugando() {
        return new LinkedList<>(jugJugando);
    }

    public Queue<IJugador> getJugadoresMesa() {
        return new LinkedList<>(jugEnMeza);
    }

    public IJugador getJugador(String alias) {
        for (IJugador jug : registroJugadores) {
            if (Objects.equals(jug.getAlias(), alias)) {
                return jug;
            }
        }
        return null;
    }

    public boolean esAllIn(String alias) {
        for (IJugador jug : jugJugando) {
            if (Objects.equals(jug.getAlias(), alias)) {
                return jug.esAllIn();
            }
        }
        return false;
    }

    public boolean estaRetirado(String alias) {
        if (jugJugando.isEmpty()) return false;
        for (IJugador jug : jugEnMeza) {
            if (Objects.equals(jug.getAlias(), alias)) {
                for (IJugador j : jugJugando) {
                    if (Objects.equals(j.getAlias(), alias)) return false;
                }
                return true;
            }
        }
        return false;
    }

    public double getSaldo(String alias) {
        for (IJugador jug : jugJugando) {
            if (Objects.equals(jug.getAlias(), alias)) {
                return jug.getSaldo();
            }
        }
        return 0;
    }

    public double getMontoApostado(String alias) {
        for (IJugador jug : jugJugando) {
            if (Objects.equals(jug.getAlias(), alias)) {
                return jug.getMontoApostado();
            }
        }
        return -1;
    }

    public double getMontoApostadoFase(String alias) {
        for (IJugador jug : jugJugando) {
            if (Objects.equals(jug.getAlias(), alias)) {
                return jug.getMontoApostadoFase();
            }
        }
        return -1;
    }

    public String getUltimoJugador() {
        return ultimoJugador;
    }

    public String getUltimoGanador() {
        return ultimoGanador;
    }

    public void setUltimoJugador(String alias) {
        this.ultimoJugador = alias;
    }

    public void setUltimoGanador(String alias) {
        this.ultimoGanador = alias;
    }
}
