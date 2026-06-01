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

public class ReglaPar implements IReglaMano {

    private static final int RANKING_PAR = 1;

    @Override
    public Optional<ValorMano> evaluar(List<Carta> mano) {
        Map<Integer, Long> conteo = ManoUtils.conteoValores(mano);

        if (conteo.size() != 4) return Optional.empty();

        Integer valorPar = null;
        List<Integer> restos = new ArrayList<>();

        for (var e : conteo.entrySet()) {
            if (e.getValue() == 2) valorPar = e.getKey();
            else if (e.getValue() == 1) restos.add(e.getKey());
        }

        if (valorPar == null) return Optional.empty();

        restos.sort(Comparator.reverseOrder());

        List<Integer> kickers = new ArrayList<>();
        kickers.add(valorPar);
        kickers.addAll(restos);

        String descripcion = "Par de " + Valor.desdeNumero(valorPar).nombrePlural();
        return Optional.of(new ValorMano(RANKING_PAR, kickers, descripcion));
    }
}
