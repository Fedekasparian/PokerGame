package main.Modelo.Clases.ReglasJuego;
import main.Modelo.Clases.Carta;

import java.util.*;
import java.util.stream.Collectors;

public final class ManoUtils {

    private ManoUtils() {}

    public static Map<Integer, Long> conteoValores(List<Carta> mano) {
        return mano.stream().collect(Collectors.groupingBy(
                Carta::getValorNumerico,
                Collectors.counting()
        ));
    }

    public static boolean esColor(List<Carta> mano) {
        return mano.stream()
                .map(Carta::getPalo)
                .distinct()
                .count() == 1;
    }

    public static List<Integer> valoresOrdenadosDesc(List<Carta> mano) {
        return mano.stream()
                .map(Carta::getValorNumerico)
                .sorted(Comparator.reverseOrder())
                .toList();
    }

    /**
     Devuelve:
     - Optional.of(alta) si es escalera (alta = carta más alta)
     - Optional.empty() si no es escalera
         Caso especial: A-2-3-4-5 => alta = 5
     */
    public static Optional<Integer> altaEscalera(List<Carta> mano) {
        List<Integer> vals = mano.stream()
                .map(Carta::getValorNumerico)
                .distinct()
                .sorted()
                .toList();

        if (vals.size() != 5) return Optional.empty();

        // Normal: consecutivos
        boolean consecutivos = true;
        for (int i = 1; i < 5; i++) {
            if (vals.get(i) != vals.get(i - 1) + 1) {
                consecutivos = false;
                break;
            }
        }
        if (consecutivos) return Optional.of(vals.get(4)); // el mayor

        // Caso especial: A-2-3-4-5 => [2,3,4,5,14]
        if (vals.equals(List.of(2, 3, 4, 5, 14))) {
            return Optional.of(5);
        }

        return Optional.empty();
    }
}

