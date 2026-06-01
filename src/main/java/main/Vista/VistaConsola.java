package main.Vista;

import main.Controlador.Controlador;
import main.Modelo.Clases.Carta;
import main.Modelo.Clases.entidades.JugadorHistorial;
import main.Modelo.DTOs.EstadoMesaDTO;
import main.Modelo.DTOs.InfoJugadorDTO;
import main.Vista.Consola.VentanaInicioSesion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VistaConsola extends JFrame implements IVista {

    private VentanaInicioSesion vInicioSesion;
    private final JPanel panelPrincipal;
    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final JTextArea txtEstado;
    private final JTextArea txtLog;
    private final JTextField txtEntrada;
    private final JButton btnEnter;

    private volatile boolean salir = false;
    private volatile boolean enFinPartida = false;
    private ScheduledExecutorService timer;

    private Controlador controlador;

    private enum Pantalla { MENU, JUEGO, TOP5 }
    private Pantalla pantallaActual = Pantalla.MENU;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelCenterCards = new JPanel(cardLayout);

    private JPanel panelJuego;
    private JPanel panelMenu;
    private JPanel panelTop5;

    private JTable tablaTop5;
    private DefaultTableModel modeloTop5;

    public VistaConsola() {

        this.vInicioSesion = new VentanaInicioSesion();
        this.vInicioSesion.onClickIniciar(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                mostrarPanelJuego();
                mostrarMenuInicial();
            }
        });

        setTitle("POKER GAME - Consola Visual");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(panelPrincipal);

        txtEstado = new JTextArea();
        configurarAreaConsola(txtEstado);
        JScrollPane spEstado = new JScrollPane(txtEstado);
        spEstado.setBorder(BorderFactory.createTitledBorder("Panel principal"));

        txtLog = new JTextArea();
        configurarAreaConsola(txtLog);
        txtLog.setLineWrap(true);
        txtLog.setWrapStyleWord(true);
        JScrollPane spLog = new JScrollPane(txtLog);
        spLog.setBorder(BorderFactory.createTitledBorder("Mensajes del sistema"));
        spLog.setPreferredSize(new Dimension(320, 0));

        panelJuego = new JPanel(new BorderLayout());
        panelJuego.add(spEstado, BorderLayout.CENTER);

        panelMenu = construirPanelMenu();
        panelTop5 = construirPanelTop5();

        panelCenterCards.add(panelMenu, Pantalla.MENU.name());
        panelCenterCards.add(panelJuego, Pantalla.JUEGO.name());
        panelCenterCards.add(panelTop5, Pantalla.TOP5.name());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelCenterCards, spLog);

        panelPrincipal.add(split, BorderLayout.CENTER);

        JPanel panelEntrada = new JPanel(new BorderLayout(8, 0));
        JLabel prompt = new JLabel("> ");
        prompt.setFont(new Font("Monospaced", Font.BOLD, 14));

        txtEntrada = new JTextField();
        txtEntrada.setFont(new Font("Monospaced", Font.PLAIN, 16));

        btnEnter = new JButton("Enviar");
        btnEnter.setFont(new Font("Arial", Font.BOLD, 14));

        panelEntrada.add(prompt, BorderLayout.WEST);
        panelEntrada.add(txtEntrada, BorderLayout.CENTER);
        panelEntrada.add(btnEnter, BorderLayout.EAST);
        panelEntrada.setPreferredSize(new Dimension(0, 45));

        panelPrincipal.add(panelEntrada, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnEnter);

        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) {
                txtEntrada.requestFocusInWindow();
            }
        });

        btnEnter.addActionListener(e -> ejecutarEntrada());
        txtEntrada.addActionListener(e -> ejecutarEntrada());

        renderEstadoTexto("=== POKER GAME ===\nEsperando jugadores para comenzar a jugar...\n");
        logSistema("Bienvenido. Escribí 'help' para ver comandos.");
    }

    private JPanel construirPanelMenu() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(new EmptyBorder(10,10,10,10));

        JTextArea info = new JTextArea();
        configurarAreaConsola(info);
        info.setText("""
            === POKER GAME ===

            Elegí una opción:
            - Escribí: jugar
            - Escribí: top5

            (También podés usar botones)
            """);
        info.setEditable(false);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnJugar = new JButton("Jugar");
        JButton btnTop5 = new JButton("Ver Top 5");

        btnJugar.addActionListener(e -> {
            try { irAJuego(); } catch (RemoteException ex) { mostrarMensaje("Error remoto: " + ex.getMessage()); }
        });

        btnTop5.addActionListener(e -> {
            try { irATop5(); } catch (RemoteException ex) { mostrarMensaje("Error remoto: " + ex.getMessage()); }
        });

        botones.add(btnJugar);
        botones.add(btnTop5);

        p.add(new JScrollPane(info), BorderLayout.CENTER);
        p.add(botones, BorderLayout.SOUTH);

        return p;
    }

    private JPanel construirPanelTop5() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBorder(new EmptyBorder(10,10,10,10));

        modeloTop5 = new DefaultTableModel(new Object[]{"#", "Jugador", "Manos ganadas"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaTop5 = new JTable(modeloTop5);
        tablaTop5.setRowHeight(24);

        JScrollPane sp = new JScrollPane(tablaTop5);
        sp.setBorder(BorderFactory.createTitledBorder("Top 5 jugadores (por manos ganadas)"));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnVolver = new JButton("Volver");

        btnActualizar.addActionListener(e -> {
            try { cargarTop5(); } catch (RemoteException ex) { mostrarMensaje("Error remoto: " + ex.getMessage()); }
        });

        btnVolver.addActionListener(e -> mostrarMenuInicial());

        acciones.add(btnActualizar);
        acciones.add(btnVolver);

        p.add(sp, BorderLayout.CENTER);
        p.add(acciones, BorderLayout.SOUTH);
        return p;
    }

    private void mostrarMenuInicial() {
        pantallaActual = Pantalla.MENU;
        cardLayout.show(panelCenterCards, Pantalla.MENU.name());
        logSistema("Menú inicial: escribí 'jugar' o 'top5'.");
    }

    private void irAJuego() throws RemoteException {
        pantallaActual = Pantalla.JUEGO;
        cardLayout.show(panelCenterCards, Pantalla.JUEGO.name());
        controlador.registrarse(vInicioSesion.getGetNombreUsuario(), vInicioSesion.getContrasenaUsuario());
        controlador.iniciarSesion(vInicioSesion.getGetNombreUsuario(), vInicioSesion.getContrasenaUsuario());
        cargarDatosPantalla();
        logSistema("Entraste a JUGAR.");
    }

    private void irATop5() throws RemoteException {
        pantallaActual = Pantalla.TOP5;
        cardLayout.show(panelCenterCards, Pantalla.TOP5.name());
        cargarTop5();
        logSistema("Mostrando TOP 5.");
    }

    private void cargarTop5() throws RemoteException {
        modeloTop5.setRowCount(0);

        List<JugadorHistorial> top = controlador.getTop5();

        if (top == null || top.isEmpty()) {
            mostrarMensaje("No hay datos de ranking aún.");
            return;
        }

        int i = 1;
        for (JugadorHistorial j : top) {
            modeloTop5.addRow(new Object[]{ i++, j.getAlias(), j.getManosGanadas() });
        }
    }

    private void ejecutarEntrada() {
        String cmd = txtEntrada.getText().trim();
        if (cmd.isEmpty()) return;

        try {
            if (pantallaActual == Pantalla.MENU) {
                procesarEntradaMenu(cmd);
            } else if (pantallaActual == Pantalla.TOP5) {
                procesarEntradaTop5(cmd);
            } else {
                procesarEntrada(cmd);
            }
        } catch (RemoteException ex) {
            mostrarMensaje("Error remoto: " + ex.getMessage());
        } catch (Exception ex) {
            mostrarMensaje("Error: " + ex.getMessage());
        } finally {
            txtEntrada.setText("");
            txtEntrada.requestFocusInWindow();
        }
    }

    private void procesarEntradaMenu(String comando) throws RemoteException {
        String c = comando.trim().toLowerCase();
        switch (c) {
            case "jugar" -> irAJuego();
            case "top5", "ver top5", "ver top 5" -> irATop5();
            case "help" -> mostrarMensaje("Menú: escribí 'jugar' o 'top5'.");
            default -> mostrarMensaje("Opción inválida. Escribí 'jugar' o 'top5'.");
        }
    }

    private void procesarEntradaTop5(String comando) throws RemoteException {
        String c = comando.trim().toLowerCase();
        switch (c) {
            case "actualizar" -> cargarTop5();
            case "volver" -> mostrarMenuInicial();
            case "help" -> mostrarMensaje("Top5: 'actualizar' o 'volver'.");
            default -> mostrarMensaje("Comando inválido en Top5. Usá 'actualizar' o 'volver'.");
        }
    }

    private void configurarAreaConsola(JTextArea area) {
        area.setEditable(false);
        area.setFocusable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        area.setBackground(new Color(18, 18, 18));
        area.setForeground(new Color(225, 225, 225));
        area.setCaretColor(area.getForeground());
        area.setMargin(new Insets(8, 8, 8, 8));
        area.setLineWrap(false);
    }

    private void procesarEntrada(String comando) throws RemoteException {
        String[] partes = comando.trim().split(" ");
        if (partes.length == 0) return;

        if (timer != null && !timer.isShutdown()) {
            salir = true;
            return;
        }

        switch (partes[0].toLowerCase()) {
            case "apostar" -> {
                if (partes.length == 2) {
                    if (controlador.esMiTurno()) {
                        controlador.apostar(Double.valueOf(partes[1]));
                        cargarDatosPantalla();
                    } else {
                        mostrarMensaje("No es tu turno");
                    }
                } else {
                    mostrarMensaje("Uso: apostar <monto>");
                }
            }
            case "descartar" -> procesarDescartar(partes.length > 1 ? partes[1] : "");
            case "pasar" -> {
                if (partes.length == 1) {
                    if (controlador.esMiTurno()) {
                        controlador.pasar();
                        cargarDatosPantalla();
                    } else {
                        mostrarMensaje("No es tu turno");
                    }
                } else {
                    mostrarMensaje("Uso: pasar");
                }
            }
            case "retirarse" -> {
                if (partes.length == 1) {
                    controlador.retirarseDeMano();
                    cargarDatosPantalla();
                } else {
                    mostrarMensaje("Uso: retirarse");
                }
            }
            case "igualar" -> {
                if (partes.length == 1) {
                    controlador.igualar();
                    cargarDatosPantalla();
                } else {
                    mostrarMensaje("Uso: igualar");
                }
            }
            case "allin" -> {
                if (partes.length == 1) {
                    if (controlador.esMiTurno()) {
                        try {
                            controlador.irAllIn();
                            cargarDatosPantalla();
                        } catch (Exception ex) {
                            mostrarMensaje(ex.getMessage());
                        }
                    } else {
                        mostrarMensaje("No es tu turno");
                    }
                } else {
                    mostrarMensaje("Uso: allin");
                }
            }
            case "recargar" -> {
                if (partes.length == 1) {
                    try {
                        controlador.recargarFichas();
                    } catch (Exception ex) {
                        mostrarMensaje(ex.getMessage());
                    }
                } else {
                    mostrarMensaje("Uso: recargar");
                }
            }
            case "help" -> mostrarMensaje("""
                    Comandos disponibles:
                    • apostar <monto>
                    • igualar
                    • allin
                    • descartar <pos1>,<pos2>,...
                    • pasar
                    • retirarse
                    • recargar
                    • help
                    """);
            default -> mostrarMensaje("Comando no reconocido.");
        }
    }

    private void procesarDescartar(String args) {
        try {
            if (!controlador.esMiTurno()) {
                mostrarMensaje("No es tu turno");
                return;
            }

            String limpio = args == null ? "" : args.trim();
            if (limpio.isEmpty()) {
                mostrarMensaje("Uso: descartar <posiciones>\nEj: descartar 1,2,3");
                return;
            }

            String[] tokens = limpio.split("\\s*,\\s*");
            Set<Integer> posiciones = new HashSet<>();

            for (String t : tokens) {
                if (t.isBlank()) continue;

                int pos;
                try {
                    pos = Integer.parseInt(t.trim());
                } catch (NumberFormatException e) {
                    mostrarMensaje("Posición inválida: '" + t + "'. Ej: descartar 1,2,3");
                    return;
                }

                if (pos < 1 || pos > 5) {
                    mostrarMensaje("Posición fuera de rango: " + pos + ". Debe ser entre 1 y 5.");
                    return;
                }

                posiciones.add(pos);
            }

            if (posiciones.isEmpty()) {
                mostrarMensaje("No ingresaste posiciones válidas. Ej: descartar 1,2,3");
                return;
            }

            List<Integer> lista = new ArrayList<>(posiciones);
            lista.sort(Comparator.reverseOrder());

            for (int pos : lista) {
                controlador.descartarCarta(pos);
            }

            cargarDatosPantalla();

        } catch (Exception e) {
            mostrarMensaje(e.getMessage());
        }
    }

    // ----------------- Log del sistema -----------------

    private void appendLog(String line) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> appendLog(line));
            return;
        }
        txtLog.append(line + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    private void logSistema(String msg) {
        appendLog("[SYS " + LocalTime.now().format(TF) + "] " + msg);
    }

    @Override
    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }

    public void cargarDatosPantalla() throws RemoteException {
        if (enFinPartida) return;

        String sep = "-----------------------------------------------------------------------\n";

        EstadoMesaDTO estado = controlador.getEstadoMesa();
        InfoJugadorDTO miInfo = estado.getMiInfo();

        String jugadores = formatearJugadores(estado.getJugadoresEnMesa());
        String miPlata = "$ " + miInfo.getSaldo();
        String misCartas = formatearManoCartas(miInfo.getMano());

        double pozo = estado.getPozo();
        String turnoDe = estado.getTurnoActual();
        String ronda = estado.getFase().toString();

        double aIgualar = estado.getMontoAIgualar();
        double miApuesta = miInfo.getMontoApostadoFase();

        String pantalla =
                "=== POKER GAME ===\n" +
                        "comandos: apostar <monto> - igualar - allin - pasar - descartar <pos> - retirarse - recargar - help\n" +
                        sep +
                        "jugadores: " + jugadores + "\n" +
                        sep +
                        "mi plata disponible: " + miPlata + "\n" +
                        "mis cartas: " + misCartas + "\n" +
                        "mi apuesta actual: $ " + miApuesta + "\n" +
                        "monto a igualar: $ " + aIgualar + "\n" +
                        sep +
                        "\n" +
                        "Pozo: $ " + pozo + "\n" +
                        "Turno de: " + turnoDe + "\n" +
                        "Ronda de la partida: " + ronda + "\n" +
                        "\n" +
                        sep;

        renderEstadoTexto(pantalla);
    }

    private String formatearJugadores(List<InfoJugadorDTO> jugadores) {
        StringBuilder sb = new StringBuilder();

        for (InfoJugadorDTO j : jugadores) {
            if (!sb.isEmpty()) sb.append(" - ");
            sb.append(j.getAlias()).append(": ").append("$ ").append(j.getMontoApostadoFase());
        }

        return sb.isEmpty() ? "(sin jugadores)" : sb.toString();
    }

    private String formatearManoCartas(List<Carta> mano) {
        if (mano == null || mano.isEmpty()) return "(sin cartas)";

        StringBuilder sb = new StringBuilder();
        for (Carta c : mano) {
            String carta = c.getValor().etiqueta() + " " + c.getPalo().etiqueta();
            if (sb.length() > 0) sb.append(",");
            sb.append("[").append(carta).append("]");
        }
        return sb.toString();
    }

    private void renderEstadoTexto(String texto) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> renderEstadoTexto(texto));
            return;
        }
        txtEstado.setText(texto);
        txtEstado.setCaretPosition(0);
    }

    @Override
    public void iniciar() {
        this.vInicioSesion.setVisible(true);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        logSistema(mensaje);
    }

    @Override
    public void mostrarPanelJuego() {
        this.setVisible(true);
        this.vInicioSesion.setVisible(false);
    }

    public void mostrarFinalPartida(String ganador, String descripcionJuego, int segundos) {
        enFinPartida = true;
        salir = false;

        String juego = (descripcionJuego != null) ? descripcionJuego : "-";

        renderFinPartida(ganador, juego, segundos);

        timer = Executors.newSingleThreadScheduledExecutor();
        final int[] restante = {segundos};

        timer.scheduleAtFixedRate(() -> {
            if (salir) {
                timer.shutdown();
                try {
                    controlador.retirarseMesa();
                    dispose();
                } catch (RemoteException e) {
                }
                return;
            }

            restante[0]--;
            renderFinPartida(ganador, juego, restante[0]);

            if (restante[0] <= 0) {
                timer.shutdown();
                SwingUtilities.invokeLater(() -> {
                    enFinPartida = false;
                    try {
                        cargarDatosPantalla();
                    } catch (Exception e) {
                        mostrarMensaje("Error al iniciar nueva partida");
                    }
                });
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void renderFinPartida(String ganador, String juego, int segundos) {
        String sep = "=======================================================================\n";
        String text = sep +
                "                          FIN DE LA PARTIDA                           \n" +
                sep +
                "\n" +
                "Ganador: " + ganador + "\n" +
                "Ganó con: " + juego + "\n" +
                "\n" +
                "La próxima partida comienza en " + segundos + "s\n" +
                "\n" +
                sep;
        renderEstadoTexto(text);
    }
}

