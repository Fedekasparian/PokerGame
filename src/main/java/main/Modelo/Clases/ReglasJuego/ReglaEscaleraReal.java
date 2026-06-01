package main.Modelo.Clases.ReglasJuego;

import main.Modelo.Clases.Carta;
import main.Modelo.Clases.ValorMano;
import main.Modelo.Interfaces.IReglaMano;

import java.util.List;
import java.util.Optional;

public class ReglaEscaleraReal implements IReglaMano {

    private static final int RANKING_ESCALERA_REAL = 9;

    @Override
    public Optional<ValorMano> evaluar(List<Carta> mano) {
        if (!ManoUtils.esColor(mano)) return Optional.empty();

        Optional<Integer> alta = ManoUtils.altaEscalera(mano);
        if (alta.isEmpty()) return Optional.empty();

        List<Integer> vals = mano.stream()
                .map(Carta::getValorNumerico)
                .distinct()
                .sorted()
                .toList();

        boolean esRoyal = alta.get() == 14 && vals.get(0) == 10;
        if (!esRoyal) return Optional.empty();

        String descripcion = "Escalera real de " + mano.get(0).getPalo().nombrePlural();
        return Optional.of(new ValorMano(RANKING_ESCALERA_REAL, List.of(14), descripcion));
    }
}
