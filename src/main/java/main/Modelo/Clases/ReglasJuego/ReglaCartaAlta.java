package main.Modelo.Clases.ReglasJuego;

import main.Modelo.Clases.Carta;
import main.Modelo.Clases.ValorMano;
import main.Modelo.Enums.Valor;
import main.Modelo.Interfaces.IReglaMano;

import java.util.List;
import java.util.Optional;

public class ReglaCartaAlta implements IReglaMano {

    private static final int RANKING_CARTA_ALTA = 0;

    @Override
    public Optional<ValorMano> evaluar(List<Carta> mano) {
        List<Integer> kickers = ManoUtils.valoresOrdenadosDesc(mano);
        String descripcion = "Carta alta: " + Valor.desdeNumero(kickers.get(0)).nombreLargo();
        return Optional.of(new ValorMano(RANKING_CARTA_ALTA, kickers, descripcion));
    }
}
