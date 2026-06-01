package main.Vista;

import main.Controlador.Controlador;
import main.Modelo.Clases.Carta;
import main.Modelo.Clases.entidades.JugadorHistorial;
import main.Modelo.DTOs.EstadoMesaDTO;
import main.Modelo.DTOs.InfoJugadorDTO;
import main.Modelo.Enums.Fase;
import main.Vista.Grafica.mesa.BetPanel;
import main.Vista.Grafica.mesa.CenterPotPanel;
import main.Vista.Grafica.mesa.SeatPanel;
import main.Vista.Grafica.mesa.SeatsRingPanel;
import main.Vista.Grafica.mesa.TablePanel;
import main.Vista.Grafica.util.AudioManager;
import main.Vista.Grafica.util.BackgroundPanel;
import main.Vista.Grafica.util.CardIconCache;
import main.Vista.Grafica.util.CardLabel;
import main.Vista.Grafica.util.GameButton;
import main.Vista.Grafica.util.GlassPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class VistaGrafica extends JFrame implements IVista {

    private final JPanel panelPrincipal;
    private JPanel panelInicio;
    private JPanel panelAcciones;
    private JDialog ventanaMensaje;
    private JPanel panelMensaje;
    private JLabel mensaje;

    private JFrame ventanaJuego;
    private JPanel panelMisDatos;
    private JPanel panelMisCartas;
    private final CardIconCache iconCache = new CardIconCache(90, 130);

    private JDialog dialogFin;
    private JLabel lblGanador;
    private JLabel lblTimer;
    private JLabel lblJuego;
    private Timer timerFin;
    private int segundosRestantes;

    private CardLayout layoutAcciones;
    private JPanel contenedorAcciones;

    private SeatsRingPanel seatsRing;
    private CenterPotPanel centerPot;
    private TablePanel tablePanel;
    private BetPanel betPanel;

    private ImageIcon backIcon;
    private final AudioManager audio = new AudioManager();

    private JLabel lblMiAlias, lblMiFichas, lblMiApuesta, lblMiEstado, lblTuTurno;
    private JPanel panelMiInfo;
    private JButton btnRecargar;

    private Controlador controlador;

    public VistaGrafica() {
        setTitle("Poker - Vista Gráfica");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);

        panelPrincipal = new JPanel(new BorderLayout());

        construirPanelAcciones();
        construirPanelMensajes();
        construirPanelJuego();

        audio.load("click", "/audio/click.wav");
        audio.load("chip",  "/audio/click.wav");
        audio.load("card",  "/audio/card.wav");

        panelPrincipal.add(panelAcciones, BorderLayout.WEST);

        setContentPane(panelPrincipal);
        this.pack();
    }

    public void iniciar(){
        setVisible(true);
    }

    public void setControlador(Controlador controlador){
        this.controlador = controlador;
    }

    private void construirPanelAcciones() {
        layoutAcciones = new CardLayout();
        contenedorAcciones = new JPanel(layoutAcciones);
        contenedorAcciones.setPreferredSize(new Dimension(200, 200));

        panelInicio = new JPanel();
        panelInicio.setLayout(new BoxLayout(panelInicio, BoxLayout.Y_AXIS));
        JButton btnJugar = new JButton("Jugar");
        JButton btnTop5 = new JButton("Top 5");

        Dimension tamanoBoton = new Dimension(180, 40);
        JButton[] botones = {
                btnJugar, btnTop5
        };
        for (JButton boton : botones) {
            boton.setMaximumSize(tamanoBoton);
            boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        btnJugar.addActionListener(e -> {
            try {
                mostrarVentanaJugar();
            } catch (RemoteException ex) {
                mostrarMensaje("Error: " + ex.getMessage());
            }
        });
        btnTop5.addActionListener(e -> {
            try {
                mostrarTop5(controlador.getTop5());
            } catch (RemoteException ex) {
                mostrarMensaje("Error: " + ex.getMessage());
            }
        });

        int espaciado = 10;

        panelInicio.add(Box.createVerticalStrut(espaciado));
        panelInicio.add(btnJugar);
        panelInicio.add(Box.createVerticalStrut(espaciado));
        panelInicio.add(btnTop5);
        panelInicio.add(Box.createVerticalStrut(espaciado));


        contenedorAcciones.add(panelInicio, "INICIO");

        panelAcciones = contenedorAcciones;
    }

    private void construirPanelMensajes() {
        ventanaMensaje = new JDialog();
        ventanaMensaje.setUndecorated(true);
        ventanaMensaje.setAlwaysOnTop(true);

        panelMensaje = new JPanel();
        panelMensaje.setLayout(new BorderLayout());
        panelMensaje.setBackground(new Color(30, 30, 30));
        panelMensaje.setOpaque(true);
        panelMensaje.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 165, 0), 2, true),
                new EmptyBorder(8, 12, 8, 12)
        ));

        mensaje = new JLabel();
        mensaje.setBorder(null);
        mensaje.setOpaque(false);
        mensaje.setForeground(Color.WHITE);
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);
        mensaje.setFont(new Font("SansSerif", Font.BOLD, 14));
        mensaje.setFocusable(false);

        panelMensaje.add(mensaje, BorderLayout.CENTER);

        ventanaMensaje.setContentPane(panelMensaje);
        ventanaMensaje.pack();
        ventanaMensaje.setLocationRelativeTo(null);
        ventanaMensaje.setVisible(false);
    }

    private void construirPanelJuego() {
        ventanaJuego = new JFrame("POKER GAME");
        ventanaJuego.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel fondo = new BackgroundPanel("/ui/table.jpg", 0.15f);
        fondo.setLayout(new BorderLayout(12, 12));
        ventanaJuego.setContentPane(fondo);

        backIcon = new ImageIcon(getClass().getResource("/ui/card_back.png"));

        seatsRing = new SeatsRingPanel(backIcon, iconCache);
        centerPot = new CenterPotPanel();
        tablePanel = new TablePanel(seatsRing, centerPot);

        // ----------------- PANEL INFERIOR (Mis datos + cartas + botones) -----------------
        panelMisDatos = new GlassPanel();
        panelMisDatos.setLayout(new BorderLayout(12, 10));
        panelMisDatos.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        panelMisDatos.setPreferredSize(new Dimension(0, 210));

        panelMiInfo = new GlassPanel();
        panelMiInfo.setLayout(new BoxLayout(panelMiInfo, BoxLayout.Y_AXIS));
        panelMiInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelMiInfo.setPreferredSize(new Dimension(220, 140));

        lblTuTurno = new JLabel("TU TURNO");
        lblMiAlias = new JLabel("YO");
        lblMiFichas = new JLabel("Fichas: 0");
        lblMiApuesta = new JLabel("Apuesta: 0");
        lblMiEstado = new JLabel("");

        for (JLabel l : new JLabel[]{lblMiAlias, lblMiFichas, lblMiApuesta, lblMiEstado, lblTuTurno}) {
            l.setForeground(Color.WHITE);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        lblTuTurno.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTuTurno.setForeground(new Color(255, 215, 0));
        lblTuTurno.setVisible(false);

        lblMiAlias.setFont(lblMiAlias.getFont().deriveFont(Font.BOLD, 14f));
        lblMiEstado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMiEstado.setForeground(new Color(255, 215, 0));

        panelMiInfo.add(lblTuTurno);
        panelMiInfo.add(Box.createVerticalStrut(6));
        panelMiInfo.add(lblMiAlias);
        panelMiInfo.add(Box.createVerticalStrut(8));
        panelMiInfo.add(lblMiFichas);
        panelMiInfo.add(Box.createVerticalStrut(4));
        panelMiInfo.add(lblMiApuesta);
        panelMiInfo.add(Box.createVerticalStrut(6));
        panelMiInfo.add(lblMiEstado);

        panelMisDatos.add(panelMiInfo, BorderLayout.WEST);

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);

        panelMisCartas = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        panelMisCartas.setOpaque(false);

        centerWrap.add(panelMisCartas, new GridBagConstraints());

        panelMisDatos.add(centerWrap, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        panelBotones.setOpaque(false);

        JButton btnIgualar = new GameButton("IGUALAR");
        JButton btnPasar = new GameButton("PASAR");
        JButton btnDescartar = new GameButton("DESCARTAR");
        JButton btnRetirarse = new GameButton("RETIRARSE");
        btnRecargar = new GameButton("RECARGAR");
        btnRecargar.setEnabled(false);
        btnRecargar.setToolTipText("Solo durante la apuesta inicial, con saldo en 0 (una sola vez por partida)");

        panelBotones.add(btnIgualar);
        panelBotones.add(btnPasar);
        panelBotones.add(btnDescartar);
        panelBotones.add(btnRetirarse);
        panelBotones.add(btnRecargar);

        panelMisDatos.add(panelBotones, BorderLayout.SOUTH);

        // ----------------- PANEL DERECHA (Apuesta) -----------------
        betPanel = new BetPanel();
        betPanel.onConfirm(monto -> {
            audio.play("chip");
            try {
                if (controlador.esMiTurno()) {
                    controlador.apostar(monto);
                }
            } catch (Exception ex) {
                mostrarMensaje(ex.getMessage());
            }
        });

        fondo.add(tablePanel, BorderLayout.CENTER);
        fondo.add(panelMisDatos, BorderLayout.SOUTH);

        JPanel east = new JPanel();
        east.setOpaque(false);
        east.setLayout(new BorderLayout());
        east.add(betPanel, BorderLayout.NORTH);
        fondo.add(east, BorderLayout.EAST);

        // ----------------- Listeners -----------------
        btnIgualar.addActionListener(e -> {
            try {
                if (controlador.esMiTurno()) {
                    controlador.igualar();
                }
            } catch (RemoteException ex) {
                mostrarMensaje(ex.getMessage());
            }
        });

        btnPasar.addActionListener(e -> {
            audio.play("click");
            try {
                if (controlador.esMiTurno()) {
                    controlador.pasar();
                }
            } catch (Exception ex) {
                mostrarMensaje(ex.getMessage());
            }
        });

        btnRetirarse.addActionListener(e -> {
            audio.play("click");
            try {
                controlador.retirarseDeMano();
            } catch (Exception ex) {
                mostrarMensaje(ex.getMessage());
            }
        });

        btnDescartar.addActionListener(e -> {
            audio.play("card");
            try {
                if (controlador.esMiTurno() && controlador.puedeDescartar()) {
                    descartarSeleccionadas();
                }
            } catch (Exception ex) {
                mostrarMensaje(ex.getMessage());
            }
        });

        btnRecargar.addActionListener(e -> {
            audio.play("chip");
            try {
                controlador.recargarFichas();
            } catch (Exception ex) {
                mostrarMensaje(ex.getMessage());
            }
        });

        ventanaJuego.setSize(1300, 760);
        ventanaJuego.setLocationRelativeTo(null);

        ventanaJuego.addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) {
                audio.loop("ambient");
            }
            @Override public void windowClosing(WindowEvent e) {
                audio.stop("ambient");
            }
            @Override public void windowClosed(WindowEvent e) {
                audio.stop("ambient");
            }
        });
    }


    // ----------------- Login -----------------

    private void mostrarVentanaJugar() throws RemoteException {
        JDialog dialog = new JDialog(this, "Iniciar Sesión", true);
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(30, 30, 30));
        content.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 165, 0), 2, true),
                new EmptyBorder(18, 28, 18, 28)
        ));

        JLabel titulo = new JLabel("POKER GAME");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(255, 215, 0));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Iniciar sesión o registrarse");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(Color.LIGHT_GRAY);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAlias = new JLabel("Alias");
        lblAlias.setForeground(Color.WHITE);
        lblAlias.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAlias.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField alias = new JTextField();
        alias.setMaximumSize(new Dimension(260, 30));
        alias.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblContrasena = new JLabel("Contraseña");
        lblContrasena.setForeground(Color.WHITE);
        lblContrasena.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContrasena.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField contrasena = new JPasswordField();
        contrasena.setMaximumSize(new Dimension(260, 30));
        contrasena.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSaldo = new JLabel("Saldo inicial: 3000");
        lblSaldo.setForeground(Color.LIGHT_GRAY);
        lblSaldo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSaldo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnJugar = new JButton("JUGAR");
        JButton btnCancelar = new JButton("CANCELAR");
        btnJugar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setOpaque(false);
        panelBotones.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBotones.add(btnJugar);
        panelBotones.add(btnCancelar);

        content.add(titulo);
        content.add(Box.createVerticalStrut(4));
        content.add(subtitulo);
        content.add(Box.createVerticalStrut(18));
        content.add(lblAlias);
        content.add(Box.createVerticalStrut(4));
        content.add(alias);
        content.add(Box.createVerticalStrut(10));
        content.add(lblContrasena);
        content.add(Box.createVerticalStrut(4));
        content.add(contrasena);
        content.add(Box.createVerticalStrut(10));
        content.add(lblSaldo);
        content.add(Box.createVerticalStrut(16));
        content.add(panelBotones);

        btnJugar.addActionListener(e -> {
            try {
                controlador.registrarse(alias.getText(), new String(contrasena.getPassword()));
                controlador.iniciarSesion(alias.getText(), new String(contrasena.getPassword()));
                dialog.dispose();
            } catch (Exception ex) {
                mostrarMensaje(ex.getMessage());
            }
        });
        btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.getRootPane().setDefaultButton(btnJugar);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ----------------- Render -----------------

    public void mostrarPanelJuego() throws RemoteException {
        if (ventanaJuego == null) {
            construirPanelJuego();
        }

        cargarDatosPantalla();

        ventanaJuego.pack();
        ventanaJuego.setVisible(true);
    }

    public void cargarDatosPantalla() throws RemoteException {
        EstadoMesaDTO estado = controlador.getEstadoMesa();
        InfoJugadorDTO miInfo = estado.getMiInfo();

        String miAlias = controlador.getMiAlias();
        String turnoAlias = estado.getTurnoActual();
        Fase fase = estado.getFase();

        lblMiAlias.setText(miAlias);
        lblMiFichas.setText("Fichas: " + (int) miInfo.getSaldo());
        lblMiApuesta.setText("Apuesta: " + (int) miInfo.getMontoApostadoFase());

        if (miInfo.isRetirado()) {
            lblMiEstado.setText("RETIRADO");
            lblMiEstado.setForeground(new Color(200, 100, 100));
        } else if (miInfo.isAllIn()) {
            lblMiEstado.setText("ALL-IN");
            lblMiEstado.setForeground(new Color(255, 215, 0));
        } else {
            lblMiEstado.setText("");
        }

        lblTuTurno.setVisible(miAlias.equals(turnoAlias) && !miInfo.isRetirado());

        double pozo = estado.getPozo();
        double aIgualar = estado.getMontoAIgualar() - miInfo.getMontoApostadoFase();

        centerPot.update(pozo, String.valueOf(fase), aIgualar, turnoAlias);

        betPanel.setMax(miInfo.getSaldo());

        boolean habilitarApuesta = miAlias.equals(turnoAlias) && (
                fase == Fase.APUESTA_INICIAL || fase == Fase.APUESTA_FINAL
        );
        betPanel.setEnabledAll(habilitarApuesta);

        btnRecargar.setEnabled(miInfo.getSaldo() == 0 && !miInfo.isHaRecargado() && fase == Fase.APUESTA_INICIAL);

        List<InfoJugadorDTO> others = new ArrayList<>();
        for (InfoJugadorDTO j : estado.getJugadoresEnMesa()) {
            if (!Objects.equals(j.getAlias(), miAlias)) {
                others.add(j);
            }
        }

        List<SeatPanel> seatPanels = seatsRing.getOtherSeats();

        for (int i = 0; i < seatPanels.size(); i++) {
            SeatPanel seat = seatPanels.get(i);

            if (i < others.size()) {
                InfoJugadorDTO j = others.get(i);
                String alias = j.getAlias();
                boolean esTurno = alias.equals(turnoAlias) && !j.isRetirado();
                boolean mostrarCartas = j.getMano() != null;
                List<Carta> mano = mostrarCartas ? j.getMano() : Collections.emptyList();

                seat.update(
                        alias,
                        j.getSaldo(),
                        j.getMontoApostadoFase(),
                        esTurno,
                        j.isRetirado(),
                        j.isAllIn(),
                        mostrarCartas,
                        mano
                );
                seat.setVisible(true);
            } else {
                seat.setVisible(false);
            }
        }

        panelMisCartas.removeAll();
        List<Carta> miMano = miInfo.getMano();
        if (miMano != null) {
            for (Carta c : miMano) {
                ImageIcon icon = iconCache.getIcon(c);
                CardLabel lbl = new CardLabel(icon);
                panelMisCartas.add(lbl);
            }
        }

        ventanaJuego.revalidate();
        ventanaJuego.repaint();
    }

    @Override
    public void mostrarMensaje(String mensajeNuevo) {
        this.mensaje.setText(mensajeNuevo);
        this.panelMensaje.setVisible(true);

        ventanaMensaje.pack();
        ventanaMensaje.setLocationRelativeTo(ventanaJuego);
        ventanaMensaje.setVisible(true);

        Timer timer = new Timer(3000, e -> {
            panelMensaje.setVisible(false);
            ventanaMensaje.setVisible(false);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void mostrarTop5(List<JugadorHistorial> top5) {
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog(ventanaJuego, "Top 5 jugadores", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.getContentPane().setBackground(new Color(30, 30, 30));

            JLabel titulo = new JLabel("TOP 5 JUGADORES", SwingConstants.CENTER);
            titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
            titulo.setForeground(new Color(255, 215, 0));
            titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
            dialog.add(titulo, BorderLayout.NORTH);

            String[] columnas = {"#", "Alias", "Manos ganadas"};
            DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            int pos = 1;
            for (JugadorHistorial j : top5) {
                String alias = j.getAlias();
                int manosGanadas = j.getManosGanadas();
                modeloTabla.addRow(new Object[]{pos++, alias, manosGanadas});
            }

            JTable tabla = new JTable(modeloTabla);
            tabla.setFillsViewportHeight(true);
            tabla.setRowHeight(24);
            tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tabla.setForeground(Color.WHITE);
            tabla.setBackground(new Color(45, 45, 45));
            tabla.setGridColor(new Color(80, 80, 80));

            tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            tabla.getTableHeader().setBackground(new Color(60, 60, 60));
            tabla.getTableHeader().setForeground(Color.WHITE);

            JScrollPane scroll = new JScrollPane(tabla);
            scroll.getViewport().setBackground(new Color(30, 30, 30));
            dialog.add(scroll, BorderLayout.CENTER);

            JButton btnCerrar = new JButton("Cerrar");
            btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnCerrar.addActionListener(e -> dialog.dispose());

            JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panelBoton.setOpaque(false);
            panelBoton.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
            panelBoton.add(btnCerrar);

            dialog.add(panelBoton, BorderLayout.SOUTH);

            dialog.pack();
            dialog.setSize(400, dialog.getHeight());
            dialog.setLocationRelativeTo(ventanaJuego);
            dialog.setVisible(true);
        });
    }

    private void descartarSeleccionadas() {
        try {
            List<Integer> posiciones = new ArrayList<>();

            Component[] comps = panelMisCartas.getComponents();
            for (int i = 0; i < comps.length; i++) {
                if (comps[i] instanceof CardLabel cl && cl.isSelected()) {
                    posiciones.add(i + 1);
                }
            }

            if (posiciones.isEmpty()) {
                mostrarMensaje("Seleccioná al menos 1 carta para descartar.");
                return;
            }

            posiciones.sort(Comparator.reverseOrder());

            for (int pos : posiciones) {
                controlador.descartarCarta(pos);
            }

            mostrarMensaje("Descartaste posiciones: " + posiciones);
            cargarDatosPantalla();

        } catch (Exception ex) {
            mostrarMensaje("Error al descartar: " + ex.getMessage());
        }
    }

    private void construirDialogFin() {
        dialogFin = new JDialog(ventanaJuego, "Fin de la mano", true);
        dialogFin.setUndecorated(true);
        dialogFin.setAlwaysOnTop(true);

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(30, 30, 30));
        p.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 165, 0), 2, true),
                new EmptyBorder(14, 18, 14, 18)
        ));

        lblGanador = new JLabel("Ganador: -");
        lblGanador.setForeground(Color.WHITE);
        lblGanador.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblGanador.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblJuego = new JLabel("Ganó con: -");
        lblJuego.setForeground(Color.LIGHT_GRAY);
        lblJuego.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblJuego.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTimer = new JLabel("La próxima partida comienza en 10s");
        lblTimer.setForeground(Color.LIGHT_GRAY);
        lblTimer.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTimer.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(lblGanador);
        p.add(Box.createVerticalStrut(10));
        p.add(lblJuego);
        p.add(Box.createVerticalStrut(12));
        p.add(lblTimer);

        dialogFin.setContentPane(p);
        dialogFin.pack();
        dialogFin.setLocationRelativeTo(ventanaJuego);
    }

    public void mostrarFinalPartida(String ganadorAlias, String descripcionJuego, int segundos) {
        if (dialogFin == null) construirDialogFin();

        lblGanador.setText("Ganador: " + ganadorAlias);
        lblJuego.setText("Ganó con: " + (descripcionJuego != null ? descripcionJuego : "-"));

        segundosRestantes = segundos;
        lblTimer.setText("La próxima partida comienza en " + segundosRestantes + "s");

        if (timerFin != null && timerFin.isRunning()) timerFin.stop();

        timerFin = new Timer(1000, e -> {
            segundosRestantes--;
            lblTimer.setText("La próxima partida comienza en " + segundosRestantes + "s");

            if (segundosRestantes <= 0) {
                timerFin.stop();
                dialogFin.dispose();
                try {
                    cargarDatosPantalla();
                } catch (RemoteException ex) {
                    mostrarMensaje(ex.getMessage());
                }
            }
        });

        timerFin.start();

        dialogFin.pack();
        dialogFin.setLocationRelativeTo(ventanaJuego);
        dialogFin.setVisible(true);
    }
}
