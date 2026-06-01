package main.Modelo.Clases.ReglasJuego;

import main.Modelo.Clases.Carta;
import main.Modelo.Clases.ValorMano;
import main.Modelo.Enums.Valor;
import main.Modelo.Interfaces.IReglaMano;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReglaPoker implements IReglaMano {

    private static final int RANKING_POKER = 7;

    @Override
    public Optional<ValorMano> evaluar(List<Carta> mano) {
        Map<Integer, Long> conteo = ManoUtils.conteoValores(mano);

        Integer valorPoker = null;
        Integer kicker = null;

        for (var e : conteo.entrySet()) {
            if (e.getValue() == 4) valorPoker = e.getKey();
            if (e.getValue() == 1) kicker = e.getKey();
        }

        if (valorPoker == null) return Optional.empty();

        String descripcion = "Poker de " + Valor.desdeNumero(valorPoker).nombrePlural();
        return Optional.of(new ValorMano(RANKING_POKER, List.of(valorPoker, kicker), descripcion));
    }
}
