package main;
import main.Modelo.Clases.PokerGame;
import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.servidor.Servidor;
import java.rmi.RemoteException;

public class ServerPoker {
    public static void main(String[] args) throws RemoteException {
        PokerGame modelo = PokerGame.getInstanciaJuego(); // modelo
        Servidor servidor = new Servidor("127.0.0.1", 8888);
        try {
            servidor.iniciar(modelo);
            System.out.println("Servidor iniciado.");
        } catch (RemoteException e) {
            // error de conexión
            e.printStackTrace();
        } catch (RMIMVCException e) {
            // error al crear el objeto de acceso remoto del modelo
            e.printStackTrace();
        }
    }
}
