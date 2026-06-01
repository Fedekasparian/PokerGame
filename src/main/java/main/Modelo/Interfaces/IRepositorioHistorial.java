package main.Modelo.Interfaces;

import main.Modelo.Clases.entidades.JugadorHistorial;

public interface IRepositorioHistorial {
    void registrarManoGanada(String alias);
    java.util.List<JugadorHistorial> top5PorManosGanadas();
    java.util.List<JugadorHistorial> obtenerTodos();
}
