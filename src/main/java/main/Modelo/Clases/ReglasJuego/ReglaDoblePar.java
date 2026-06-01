package main.Modelo.Clases.ReglasJuego;

import main.Modelo.Clases.Carta;
import main.Modelo.Clases.ValorMano;
import main.Modelo.Enums.Valor;
import main.Modelo.Interfaces.IReglaMano;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReglaDoblePar implements IReglaMano {

    private static final int RANKING_DOBLE_PAR = 2;

    @Override
    public Optional<ValorMano> evaluar(List<Carta> mano) {
        Map<Integer, Long> conteo = ManoUtils.conteoValores(mano);

        if (conteo.size() != 3) return Optional.empty();

        List<Integer> pares = new ArrayList<>();
        Integer kicker = null;

        for (var e : conteo.entrySet()) {
            if (e.getValue() == 2) pares.add(e.getKey());
            else if (e.getValue() == 1) kicker = e.getKey();
        }

        if (pares.size() != 2) return Optional.empty();

        pares.sort(Comparator.reverseOrder());

        List<Integer> kickers = List.of(pares.get(0), pares.get(1), kicker);
        String descripcion = "Doble par de "
                + Valor.desdeNumero(pares.get(0)).nombrePlural()
                + " y "
                + Valor.desdeNumero(pares.get(1)).nombrePlural();
        return Optional.of(new ValorMano(RANKING_DOBLE_PAR, kickers, descripcion));
    }
}
