package main.Modelo.Clases.ReglasJuego;

import main.Modelo.Clases.Carta;
import main.Modelo.Clases.ValorMano;
import main.Modelo.Enums.Valor;
import main.Modelo.Interfaces.IReglaMano;

import java.util.List;
import java.util.Optional;

public class ReglaEscaleraColor implements IReglaMano {

    private static final int RANKING_ESCALERA_COLOR = 8;

    @Override
    public Optional<ValorMano> evaluar(List<Carta> mano) {
        if (!ManoUtils.esColor(mano)) return Optional.empty();

        Optional<Integer> alta = ManoUtils.altaEscalera(mano);
        if (alta.isEmpty()) return Optional.empty();

        String descripcion = "Escalera color al "
                + Valor.desdeNumero(alta.get()).nombreLargo()
                + " de "
                + mano.get(0).getPalo().nombrePlural();
        return Optional.of(new ValorMano(RANKING_ESCALERA_COLOR, List.of(alta.get()), descripcion));
    }
}
