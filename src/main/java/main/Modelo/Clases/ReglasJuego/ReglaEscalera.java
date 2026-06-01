package main.Modelo.Clases.ReglasJuego;

import main.Modelo.Clases.Carta;
import main.Modelo.Clases.ValorMano;
import main.Modelo.Enums.Valor;
import main.Modelo.Interfaces.IReglaMano;

import java.util.List;
import java.util.Optional;

public class ReglaEscalera implements IReglaMano {

    private static final int RANKING_ESCALERA = 4;

    @Override
    public Optional<ValorMano> evaluar(List<Carta> mano) {
        Optional<Integer> alta = ManoUtils.altaEscalera(mano);
        if (alta.isEmpty()) return Optional.empty();

        String descripcion = "Escalera al " + Valor.desdeNumero(alta.get()).nombreLargo();
        return Optional.of(new ValorMano(RANKING_ESCALERA, List.of(alta.get()), descripcion));
    }
}
