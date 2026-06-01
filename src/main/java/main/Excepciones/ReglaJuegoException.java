package main.Excepciones;

public class ReglaJuegoException extends RuntimeException {
    public ReglaJuegoException(String message) {
        super(message);
    }
}
