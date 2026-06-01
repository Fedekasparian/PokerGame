package main.Modelo.Clases;

import main.Modelo.Clases.entidades.EstadoJuego;
import main.Modelo.Clases.entidades.JugadorHistorial;
import main.Modelo.Interfaces.IRepositorioHistorial;

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RepositorioArchivoHistorial implements IRepositorioHistorial {

    private final File archivo;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    public RepositorioArchivoHistorial(String rutaArchivo) {
        this.archivo = new File(rutaArchivo);
    }

    @Override
    public void registrarManoGanada(String alias) {
        if (alias == null || alias.isBlank()) return;

        lock.writeLock().lock();
        try {
            EstadoJuego estado = cargarEstadoUnsafe();

            Map<String, JugadorHistorial> mapa = new HashMap<>();
            for (JugadorHistorial jh : estado.getRegistroJugadores()) {
                mapa.put(jh.getAlias(), jh);
            }

            JugadorHistorial ganador = mapa.computeIfAbsent(alias, JugadorHistorial::new);
            ganador.sumarManoGanada();

            estado.setRegistroJugadores(new ArrayList<>(mapa.values()));
            guardarEstadoUnsafe(estado);

        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public List<JugadorHistorial> top5PorManosGanadas() {
        lock.readLock().lock();
        try {
            EstadoJuego estado = cargarEstadoUnsafe();

            return estado.getRegistroJugadores().stream()
                    .sorted(Comparator.comparingInt(JugadorHistorial::getManosGanadas).reversed()
                            .thenComparing(JugadorHistorial::getAlias))
                    .limit(5)
                    .toList();

        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<JugadorHistorial> obtenerTodos() {
        lock.readLock().lock();
        try {
            return List.copyOf(cargarEstadoUnsafe().getRegistroJugadores());
        } finally {
            lock.readLock().unlock();
        }
    }

    // -------------------- Internos --------------------

    private EstadoJuego cargarEstadoUnsafe() {
        if (!archivo.exists()) return estadoVacio();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            Object obj = ois.readObject();
            if (obj instanceof EstadoJuego estado) {
                if (estado.getRegistroJugadores() == null) {
                    estado.setRegistroJugadores(new ArrayList<>());
                }
                return estado;
            }
            return estadoVacio();

        } catch (InvalidClassException ice) {
            // Incompatibilidad de serialVersionUID / cambios de clases
            renombrarIncompatible();
            return estadoVacio();

        } catch (Exception e) {
            e.printStackTrace();
            return estadoVacio();
        }
    }

    private void guardarEstadoUnsafe(EstadoJuego estado) {
        // Escritura atómica básica: escribir a temp y renombrar
        File tmp = new File(archivo.getParentFile(), archivo.getName() + ".tmp");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmp))) {
            oos.writeObject(estado);
        } catch (Exception e) {
            e.printStackTrace();
            // si falla, no pisamos el archivo real
            return;
        }

        // Reemplazo
        if (!tmp.renameTo(archivo)) {
            // En Windows a veces renameTo falla si existe; hacemos fallback:
            if (archivo.exists() && !archivo.delete()) {
                System.err.println("No se pudo borrar el archivo antiguo: " + archivo.getAbsolutePath());
                return;
            }
            if (!tmp.renameTo(archivo)) {
                System.err.println("No se pudo renombrar el archivo temporal a definitivo.");
            }
        }
    }

    private void renombrarIncompatible() {
        File backup = new File(archivo.getParentFile(),
                "actual_incompatible_" + System.currentTimeMillis() + ".dat");
        boolean ok = archivo.renameTo(backup);
        if (!ok) {
            System.err.println("No se pudo renombrar archivo incompatible: " + archivo.getAbsolutePath());
        }
    }

    private EstadoJuego estadoVacio() {
        EstadoJuego e = new EstadoJuego();
        e.setRegistroJugadores(new ArrayList<>());
        return e;
    }
}
