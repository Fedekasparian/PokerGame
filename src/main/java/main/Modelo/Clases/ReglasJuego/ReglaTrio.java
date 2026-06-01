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

public class ReglaTrio implements IReglaMano {

    private static final int RANKING_TRIO = 3;

    @Override
    public Optional<ValorMano> evaluar(List<Carta> mano) {
        Map<Integer, Long> conteo = ManoUtils.conteoValores(mano);

        if (conteo.size() != 3) return Optional.empty();

        Integer valorTrio = null;
        List<Integer> restos = new ArrayList<>();

        for (var e : conteo.entrySet()) {
            if (e.getValue() == 3) valorTrio = e.getKey();
            else if (e.getValue() == 1) restos.add(e.getKey());
        }

        if (valorTrio == null) return Optional.empty();

        restos.sort(Comparator.reverseOrder());

        List<Integer> kickers = List.of(valorTrio, restos.get(0), restos.get(1));
        String descripcion = "Trio de " + Valor.desdeNumero(valorTrio).nombrePlural();
        return Optional.of(new ValorMano(RANKING_TRIO, kickers, descripcion));
    }
}
