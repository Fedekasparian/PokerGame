package main.Modelo.Clases;

import java.util.List;

public class ValorMano implements Comparable<ValorMano> {

    private final int ranking;
    private final List<Integer> kickers;
    private final String descripcion;

    public ValorMano(int ranking, List<Integer> kickers, String descripcion) {
        this.ranking = ranking;
        this.kickers = kickers;
        this.descripcion = descripcion;
    }

    public int getRanking() {
        return ranking;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public int compareTo(ValorMano other) {
        if (this.ranking != other.ranking) {
            return Integer.compare(this.ranking, other.ranking);
        }
        int size = Math.min(this.kickers.size(), other.kickers.size());
        for (int i = 0; i < size; i++) {
            int cmp = Integer.compare(this.kickers.get(i), other.kickers.get(i));
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }
}
