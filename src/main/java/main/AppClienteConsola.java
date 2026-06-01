package main;
import main.Controlador.Controlador;
import main.Vista.IVista;
import main.Vista.VistaConsola;
import ar.edu.unlu.rmimvc.RMIMVCException;
import ar.edu.unlu.rmimvc.Util;
import ar.edu.unlu.rmimvc.cliente.Cliente;
import javax.swing.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class AppClienteConsola {

    private static final String IP = "127.0.0.1";
    private static final int PORT = 9999;

    public static void main(String[] args) throws RemoteException {
        ArrayList<String> ips = Util.getIpDisponibles();
        String port = (String) JOptionPane.showInputDialog(
                null,
                "Seleccione el puerto en el que escuchará peticiones el cliente", "Puerto del cliente",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                9999
        );

        IVista vista = new VistaConsola();
        Controlador controlador = new Controlador(vista);
        vista.setControlador(controlador);
        Cliente c = new Cliente(AppClienteConsola.IP, Integer.parseInt(port), "127.0.0.1", 8888);
        try {
            c.iniciar(controlador);
            vista.iniciar();
        } catch (RemoteException e) {
            SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "Ha ocurrido un error de red, revise la configuracion.!!!",
                    "Error Red", JOptionPane.ERROR_MESSAGE));
//            System.exit(1);
            e.printStackTrace();
        } catch (RMIMVCException e) {
            SwingUtilities.invokeLater(()->JOptionPane.showMessageDialog(null, "Ha ocurrido un error !!!",
                    "Error", JOptionPane.ERROR_MESSAGE));
//            System.exit(1);
            e.printStackTrace();
        }
    }
}

