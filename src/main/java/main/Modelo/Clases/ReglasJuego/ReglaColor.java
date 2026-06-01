package main.Modelo.Clases.ReglasJuego;

import main.Modelo.Clases.Carta;
import main.Modelo.Clases.ValorMano;
import main.Modelo.Interfaces.IReglaMano;

import java.util.List;
import java.util.Optional;

public class ReglaColor implements IReglaMano {

    private static final int RANKING_COLOR = 5;

    @Override
    public Optional<ValorMano> evaluar(List<Carta> mano) {
        if (!ManoUtils.esColor(mano)) return Optional.empty();

        List<Integer> kickers = ManoUtils.valoresOrdenadosDesc(mano);
        String descripcion = "Color de " + mano.get(0).getPalo().nombrePlural();
        return Optional.of(new ValorMano(RANKING_COLOR, kickers, descripcion));
    }
}
