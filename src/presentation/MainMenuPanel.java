package presentation;

import javax.swing.*;
import java.awt.*;

/**
 * Menú inicial de la aplicación.
 * @author Yeray Adrian
 * @version 2.0
 */
public class MainMenuPanel extends JPanel {
	/**
     * Constructor que inicializa la interfaz visual del menú de inicio.
     * @param frame Ventana principal para gestionar el cambio de vista.
     * @param gamePanel Panel del juego al que se transicionará al presionar el botón.
     */
    public MainMenuPanel(JFrame frame, JPanel gamePanel, GamePanel boardPanel) {
        setBackground(new Color(180, 180, 255)); 
        setLayout(new GridBagLayout()); 

        JButton btnPlay = new JButton("JUGAR");
        btnPlay.setFont(new Font("Arial", Font.BOLD, 24));
        btnPlay.setFocusable(false);
        
        btnPlay.addActionListener(e -> {
            frame.setContentPane(gamePanel);
            frame.revalidate();
            boardPanel.requestFocusInWindow();
            
            // Iniciamos el tiempo cuando se entra al nivel
            ((GameFrame)frame).startGameTimer();
        });

        add(btnPlay);
    }
}