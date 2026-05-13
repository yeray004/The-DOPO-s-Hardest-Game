package presentation;

import domain.DOPOsHardestGame;
import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal que gestiona el intercambio entre el menú y el juego.
 * @author Yeray Guacheta
 * @version 2.0
 */
public class GameFrame extends JFrame {
	private Timer gameTimer; // Atributo de clase
	/**
     * Constructor que ensambla la ventana principal, barras de estado y menús.
     * @param game Instancia principal que contiene la lógica y datos del juego.
     */
	//Modificar y añadir los prepareElements
    public GameFrame(DOPOsHardestGame game) {
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

        JPopupMenu menuPopup = new JPopupMenu();
        menuPopup.add(new JMenuItem("Guardar Partida"));
        menuPopup.add(new JMenuItem("Cargar Partida"));
        btnMenu.addActionListener(e -> menuPopup.show(btnMenu, 0, btnMenu.getHeight()));

        leftButtons.add(btnMenu);

        JLabel lblTime = new JLabel("TIEMPO: " + game.getTimeLeft(), SwingConstants.CENTER);
        lblTime.setForeground(Color.WHITE);

        JLabel lblDeaths = new JLabel("MUERTES: " + game.getTotalDeaths() + "   ", SwingConstants.RIGHT);
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
        
        bottomLeftPanel.add(btnPause);
        
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
        MainMenuPanel startMenu = new MainMenuPanel(this, mainGamePanel, boardPanel);
        setContentPane(startMenu);
        
        // Timer
        this.gameTimer = new Timer(1000, e -> {
            game.decreaseTime();
            lblTime.setText("TIEMPO: " + game.getTimeLeft());
            
            if (game.getTimeLeft() <= 0) {
                game.restartLevel();
                // Actualizamos la interfaz con los nuevos valores del reinicio
                lblTime.setText("TIEMPO: " + game.getTimeLeft());
                lblDeaths.setText("MUERTES: " + game.getTotalDeaths());
                JOptionPane.showMessageDialog(this, "¡Tiempo agotado! El nivel se ha reiniciado.");
            }
        });
    }
    /** Inicia el cronómetro del juego. */
    public void startGameTimer() {
        if (gameTimer != null) gameTimer.start();
    }
}