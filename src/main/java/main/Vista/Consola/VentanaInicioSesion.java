package main.Vista.Consola;

import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class VentanaInicioSesion extends JFrame {

    private final JPanel contentPane;
    private final JTextField textAlias;
    private final JPasswordField textContrasena;
    private final JButton btnIniciar;


    public VentanaInicioSesion() {
        setTitle("Inicio de sesión");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 280, 150);
        setLocationRelativeTo(null);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);

        // 2 columnas: label / campo. 3 filas: alias / contraseña / botón
        contentPane.setLayout(new MigLayout("insets 10", "[right][grow, fill]", "[][][grow]"));

        // --- Alias ---
        JLabel lblAlias = new JLabel("Alias");
        contentPane.add(lblAlias, "cell 0 0");

        textAlias = new JTextField(15);
        contentPane.add(textAlias, "cell 1 0");

        // --- Contraseña ---
        JLabel lblContrasena = new JLabel("Contraseña");
        contentPane.add(lblContrasena, "cell 0 1");

        textContrasena = new JPasswordField(15);
        contentPane.add(textContrasena, "cell 1 1");

        // --- Botón ---
        btnIniciar = new JButton("Iniciar");
        contentPane.add(btnIniciar, "cell 1 2, alignx right");

        // Enter dispara "Iniciar"
        getRootPane().setDefaultButton(btnIniciar);

        // Foco inicial en alias
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowOpened(java.awt.event.WindowEvent e) {
                textAlias.requestFocusInWindow();
            }
        });
    }

    public void onClickIniciar(ActionListener listener) {
        this.btnIniciar.addActionListener(listener);
    }

    public String getGetNombreUsuario() {
        return this.textAlias.getText();
    }

    public String getContrasenaUsuario() {
        return this.textContrasena.getText();
    }
}