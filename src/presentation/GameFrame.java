package presentation;

import domain.DOPOsHardestGame;
import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal que gestiona el intercambio entre el menú y el juego.
 * @author Yeray Guacheta
 * @version 2.2
 */
public class GameFrame extends JFrame {
	private DOPOsHardestGame game;
	private JLabel lblCoins;
	private JLabel lblDeaths;
	private Timer gameTimer;
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

        JPopupMenu menuPopup = new JPopupMenu();
        menuPopup.add(new JMenuItem("Guardar Partida"));
        menuPopup.add(new JMenuItem("Cargar Partida"));
        btnMenu.addActionListener(e -> menuPopup.show(btnMenu, 0, btnMenu.getHeight()));

        leftButtons.add(btnMenu);

        JLabel lblTime = new JLabel("TIEMPO: " + game.getTimeLeft(), SwingConstants.CENTER);
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
        GamePanel boardPanel = new GamePanel(game);
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
                lblDeaths.setText("MUERTES " + game.getTotalDeaths());
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
    /** Inicia el cronómetro del juego. */
    public void startGameTimer() {
        if (gameTimer != null) gameTimer.start();
    }
    /**Sincroniza los contadores de la interfaz visual con los datos del dominio.*/
    public void refreshCounters() {
    	lblDeaths.setText("MUERTES: " + game.getTotalDeaths() + "   ");
        if (game.getCurrentLevel() != null && !game.getCurrentLevel().getPlayers().isEmpty()) {
            int coins = game.getCurrentLevel().getPlayers().get(0).getCollectedCoins();
            int totalCoins = game.getCurrentLevel().getCoins().size();
            lblCoins.setText("MONEDAS: " + coins + "/" + totalCoins);
        }
    }
}