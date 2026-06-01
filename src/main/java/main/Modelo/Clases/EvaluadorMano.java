package main.Modelo.Clases;

import main.Excepciones.ReglaJuegoException;
import main.Modelo.Clases.ReglasJuego.*;
import main.Modelo.Interfaces.IReglaMano;

import java.util.List;

public class EvaluadorMano {
    private final List<IReglaMano> reglas;

    public EvaluadorMano() {
        this.reglas = List.of(
            new ReglaEscaleraReal(),
            new ReglaEscaleraColor(),
            new ReglaPoker(),
            new ReglaFull(),
            new ReglaColor(),
            new ReglaEscalera(),
            new ReglaTrio(),
            new ReglaDoblePar(),
            new ReglaPar(),
            new ReglaCartaAlta()
            );
    }

    public ValorMano evaluar(List<Carta> manoJugador){
        for (IReglaMano r : reglas){
            var resultado = r.evaluar(manoJugador);
            if(resultado.isPresent()){
                return resultado.get();
            }
        }

        throw new ReglaJuegoException("Debe existir carta alta al final");
    }
}
