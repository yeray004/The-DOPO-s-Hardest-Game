package presentation;

import domain.DOPOsHardestGame;
import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal que gestiona el intercambio entre el menú y el juego.
 * @author Yeray Guacheta
 * @version 2.3
 */
public class GameFrame extends JFrame {
	private DOPOsHardestGame game;
	private JLabel lblCoins;
	private JLabel lblDeaths;
    private JLabel lblTime;
	private Timer gameTimer;
    private GamePanel boardPanel;
	/**
     * Constructor que ensambla la ventana principal, barras de estado y menús.
     * @param game Instancia principal que contiene la lógica y datos del juego.
     */
	//Modificar y añadir los prepareElements
    public GameFrame(DOPOsHardestGame game) {
    	this.game = game;
        setTitle("The DOPO Hardest Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 520); 
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainGamePanel = new JPanel(new BorderLayout());

        // Barra superior
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.BLACK);
        topBar.setPreferredSize(new Dimension(900, 30));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftButtons.setBackground(Color.BLACK);

        JButton btnMenu = new JButton("MENÚ");
        btnMenu.setBackground(Color.BLACK);
        btnMenu.setForeground(Color.WHITE);
        btnMenu.setFocusable(false);
        btnMenu.setFocusPainted(false);
        btnMenu.setBorderPainted(false);
        
        //Guardar y cargar
        JPopupMenu menuPopup = new JPopupMenu();
        JMenuItem saveMenuItem = new JMenuItem("Guardar Partida");
        JMenuItem loadMenuItem = new JMenuItem("Cargar Partida");
        JMenuItem exitGameItem = new JMenuItem("Terminar Juego");

        saveMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (game.saveCurrentGame(filePath)) {
                    JOptionPane.showMessageDialog(this, "¡Partida guardada exitosamente!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        loadMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (game.loadCurrentGame(filePath)) {
                    refreshCounters();
                    repaint();
                    JOptionPane.showMessageDialog(this, "¡Partida cargada exitosamente!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al cargar la partida.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        exitGameItem.addActionListener(e -> finishGame());

        menuPopup.add(saveMenuItem);
        menuPopup.add(loadMenuItem);
        menuPopup.addSeparator();
        menuPopup.add(exitGameItem);
        
        btnMenu.addActionListener(e -> menuPopup.show(btnMenu, 0, btnMenu.getHeight()));

        leftButtons.add(btnMenu);

        lblTime = new JLabel("TIEMPO: " + game.getTimeLeft(), SwingConstants.CENTER);
        lblTime.setForeground(Color.WHITE);

        lblDeaths = new JLabel("MUERTES: " + game.getTotalDeaths() + "   ", SwingConstants.RIGHT);
        lblDeaths.setForeground(Color.WHITE);

        topBar.add(leftButtons, BorderLayout.WEST);
        topBar.add(lblTime, BorderLayout.CENTER);
        topBar.add(lblDeaths, BorderLayout.EAST);

        // Barra iferior
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(Color.BLACK);
        bottomBar.setPreferredSize(new Dimension(900, 30));
        
        JPanel bottomLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        bottomLeftPanel.setBackground(Color.BLACK);
        
        JButton btnPause = new JButton("PAUSA");
        btnPause.setBackground(Color.BLACK);
        btnPause.setForeground(Color.WHITE);
        btnPause.setFocusable(false);
        btnPause.setFocusPainted(false);
        btnPause.setBorderPainted(false);
        
        bottomLeftPanel.add(btnPause);
        
        //Etiqueta de moneda
        
        lblCoins = new JLabel("MONEDAS: 0", SwingConstants.CENTER);
        lblCoins.setForeground(Color.WHITE);
        bottomBar.add(lblCoins, BorderLayout.CENTER);
        
        JLabel lblRightBottom = new JLabel("DOPO 2026-1   ", SwingConstants.RIGHT);
        lblRightBottom.setForeground(Color.WHITE);
        
        bottomBar.add(bottomLeftPanel, BorderLayout.WEST);
        bottomBar.add(lblRightBottom, BorderLayout.EAST);

        //vista del juego
        boardPanel = new GamePanel(game);
        mainGamePanel.add(topBar, BorderLayout.NORTH);
        mainGamePanel.add(boardPanel, BorderLayout.CENTER);
        mainGamePanel.add(bottomBar, BorderLayout.SOUTH);

	    // Iniciar en el Menú Principal pasando el boardPanel como tercer argumento
        MainMenuPanel startMenu = new MainMenuPanel(this, mainGamePanel, game);
        setContentPane(startMenu);
        
        // Timer
        this.gameTimer = new Timer(1000, e -> {
            game.decreaseTime();
            lblTime.setText("TIEMPO: " + game.getTimeLeft());
            
            if (game.getTimeLeft() <= 0) {
                game.restartLevel();
                // Actualizamos la interfaz con los nuevos valores del reinicio
                lblTime.setText("TIEMPO: " + game.getTimeLeft());
                lblDeaths.setText("MUERTES: " + game.getTotalDeaths() + "   ");
                JOptionPane.showMessageDialog(this, "¡Tiempo agotado! El nivel se ha reiniciado.");
            }
        });
        
        //PAUSE
        btnPause.addActionListener(e -> {
            boardPanel.togglePause();
            if (boardPanel.isPaused()) {
                gameTimer.stop(); // Detiene el contador de tiempo
                btnPause.setText("REANUDAR");
            } else {
                gameTimer.start(); // Reanuda el contador de tiempo
                btnPause.setText("PAUSA");
            }
        });
    }

    /**
     * Muestra el mensaje de victoria y permite continuar o terminar el juego.
     */
    public void handleVictory() {
        if (gameTimer != null) gameTimer.stop();
        if (boardPanel != null) boardPanel.clearMovementState();
        Object[] options = {"Continuar", "Terminar juego"};
        int selected = JOptionPane.showOptionDialog(
                this,
                game.getVictoryMessage() + "\n¿Deseas continuar?",
                "Victoria",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (selected == JOptionPane.YES_OPTION) {
            boolean advanced = game.continueAfterVictory();
            refreshCounters();
            lblTime.setText("TIEMPO: " + game.getTimeLeft());
            repaint();
            if (advanced) {
                gameTimer.start();
                boardPanel.clearMovementState();
                boardPanel.resumeGame();
                boardPanel.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "No hay más niveles disponibles.");
            }
        } else {
            System.exit(0);
        }
    }

    /**Permite terminar el juego desde la partida actual.*/
    private void finishGame() {
        if (gameTimer != null) gameTimer.stop();
        int option = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas terminar el juego?",
                "Terminar juego",
                JOptionPane.YES_NO_OPTION
        );
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        } else if (boardPanel != null && !boardPanel.isPaused()) {
            gameTimer.start();
            boardPanel.requestFocusInWindow();
        }
    }

    /** Inicia el cronómetro del juego. */
    public void startGameTimer() {
        refreshCounters();
        if (gameTimer != null) gameTimer.start();
    }
    /**Sincroniza los contadores de la interfaz visual con los datos del dominio.*/
    public void refreshCounters() {
    	lblDeaths.setText("MUERTES: " + game.getTotalDeaths() + "   ");
        lblTime.setText("TIEMPO: " + game.getTimeLeft());
        if (game.getCurrentLevel() != null && !game.getCurrentLevel().getPlayers().isEmpty()) {
            int totalCoins = game.getCurrentLevel().getCoins().size();
            int boardCoins = game.getCurrentLevel().getCollectedCoinCount();
            int playerOneCoins = game.getCurrentLevel().getPlayers().get(0).getCollectedCoins();
            if (game.getCurrentLevel().getPlayers().size() > 1) {
                int playerTwoCoins = game.getCurrentLevel().getPlayers().get(1).getCollectedCoins();
                lblCoins.setText("P1: " + playerOneCoins + "   P2: " + playerTwoCoins
                        + "   TABLERO: " + boardCoins + "/" + totalCoins);
            } else {
                lblCoins.setText("MONEDAS: " + playerOneCoins + "/" + totalCoins);
            }
        }
    }
}
