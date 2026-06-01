package main.Modelo.Enums;

public enum Palo {
    DIAMANTE("diamantes", "D", "Diamante", "Diamantes"),
    PICA("pica", "P", "Pica", "Picas"),
    CORAZONES("corazones", "C", "Corazones", "Corazones"),
    TREBOL("trebol", "T", "Trebol", "Treboles");

    private final String carpeta;
    private final String letra;
    private final String etiqueta;
    private final String nombrePlural;

    Palo(String carpeta, String letra, String etiqueta, String nombrePlural) {
        this.carpeta = carpeta;
        this.letra = letra;
        this.etiqueta = etiqueta;
        this.nombrePlural = nombrePlural;
    }

    public String carpeta() {
        return carpeta;
    }

    public String letra() {
        return letra;
    }

    public String etiqueta() {
        return etiqueta;
    }

    public String nombrePlural() {
        return nombrePlural;
    }
}
