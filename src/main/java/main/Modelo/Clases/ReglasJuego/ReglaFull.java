package main.Modelo.Clases.ReglasJuego;

import main.Modelo.Clases.Carta;
import main.Modelo.Clases.ValorMano;
import main.Modelo.Enums.Valor;
import main.Modelo.Interfaces.IReglaMano;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReglaFull implements IReglaMano {

    private static final int RANKING_FULL = 6;

    @Override
    public Optional<ValorMano> evaluar(List<Carta> mano) {
        Map<Integer, Long> conteo = ManoUtils.conteoValores(mano);

        if (conteo.size() != 2) return Optional.empty();

        Integer valorTrio = null;
        Integer valorPar = null;

        for (var e : conteo.entrySet()) {
            if (e.getValue() == 3) valorTrio = e.getKey();
            else if (e.getValue() == 2) valorPar = e.getKey();
        }

        if (valorTrio == null || valorPar == null) return Optional.empty();

        String descripcion = "Full de "
                + Valor.desdeNumero(valorTrio).nombrePlural()
                + " con "
                + Valor.desdeNumero(valorPar).nombrePlural();
        return Optional.of(new ValorMano(RANKING_FULL, List.of(valorTrio, valorPar), descripcion));
    }
}
