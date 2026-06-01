package main.Modelo.Clases;

import main.Modelo.Enums.Palo;
import main.Modelo.Enums.Valor;

import java.io.Serializable;

public class Carta implements Serializable {

    private final Palo palo;
    private final Valor valor;


    public Carta(Palo palo, Valor valor) {
        this.palo = palo;
        this.valor = valor;
    }

    public Palo getPalo() {
        return this.palo;
    }

    public Valor getValor() {
        return this.valor;
    }

    public int getValorNumerico(){
        return valor.numero();
    }

}
