package main.Modelo.Enums;

public enum Valor {
    DOS(2, "2", "2", "2", "2"),
    TRES(3, "3", "3", "3", "3"),
    CUATRO(4, "4", "4", "4", "4"),
    CINCO(5, "5", "5", "5", "5"),
    SEIS(6, "6", "6", "6", "6"),
    SIETE(7, "7", "7", "7", "7"),
    OCHO(8, "8", "8", "8", "8"),
    NUEVE(9, "9", "9", "9", "9"),
    DIEZ(10, "10", "T", "10", "10"),
    JOTA(11, "J", "J", "Jota", "Jotas"),
    REINA(12, "Q", "Q", "Reina", "Reinas"),
    REY(13, "K", "K", "Rey", "Reyes"),
    AS(14, "A", "A", "As", "Ases");

    private final int numero;
    private final String etiqueta;
    private final String siglaArchivo;
    private final String nombreLargo;
    private final String nombrePlural;

    Valor(int numero, String etiqueta, String siglaArchivo, String nombreLargo, String nombrePlural) {
        this.numero = numero;
        this.etiqueta = etiqueta;
        this.siglaArchivo = siglaArchivo;
        this.nombreLargo = nombreLargo;
        this.nombrePlural = nombrePlural;
    }

    public static Valor desdeNumero(int numero) {
        for (Valor v : values()) {
            if (v.numero == numero) return v;
        }
        throw new IllegalArgumentException("Numero de carta invalido: " + numero);
    }

    public int numero() {
        return numero;
    }

    public String etiqueta() {
        return etiqueta;
    }

    public String siglaArchivo() {
        return siglaArchivo;
    }

    public String nombreLargo() {
        return nombreLargo;
    }

    public String nombrePlural() {
        return nombrePlural;
    }
}
