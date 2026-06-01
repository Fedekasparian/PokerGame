package main.Modelo.Interfaces;

import main.Modelo.Clases.Carta;
import main.Modelo.Clases.ValorMano;

import java.util.List;
import java.util.Optional;

public interface IReglaMano {

    Optional<ValorMano> evaluar(List<Carta> mano);

}
