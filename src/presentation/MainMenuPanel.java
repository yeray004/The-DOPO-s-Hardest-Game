package presentation;

import domain.DOPOsHardestGame;
import javax.swing.*;
import java.awt.*;

/**
 * Menú inicial de la aplicación.
 * @author Yeray Guacheta
 * @version 2.0
 */
public class MainMenuPanel extends JPanel {
    /**
     * Constructor que inicializa la interfaz visual del menú de inicio.
     * @param frame Ventana principal para gestionar el cambio de vista.
     * @param gamePanel Panel contenedor que aloja el tablero de juego real.
     * @param game Fachada principal para configurar el modo de juego.
     */
    public MainMenuPanel(GameFrame frame, JPanel gamePanel, DOPOsHardestGame game) {
        setBackground(new Color(180, 180, 255)); 
        setLayout(new GridBagLayout()); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título principal alineado con la referencia visual
        JLabel lblTitle = new JLabel("THE DOPO HARDEST GAME", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Impact", Font.PLAIN, 48));
        lblTitle.setForeground(Color.BLACK);
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 40, 10); // Más espacio debajo del título
        add(lblTitle, gbc);

        // Botón para modo clásico
        JButton btnSingle = createFlatButton("MODO INDIVIDUAL");
        btnSingle.addActionListener(e -> {
            game.setGameMode("Single");
            // Transición a Selección de Personaje (PvP = false)
            frame.setContentPane(new CharacterSelectionPanel(frame, gamePanel, game, false));
            frame.revalidate();
        });
        gbc.gridy = 1;
        add(btnSingle, gbc);

        // Botón para modo multijugador
        JButton btnPvP = createFlatButton("PLAYER VS PLAYER (PvP)");
        btnPvP.addActionListener(e -> {
            game.setGameMode("PvP");
            // Transición a Selección de Personaje (PvP = true)
            frame.setContentPane(new CharacterSelectionPanel(frame, gamePanel, game, true));
            frame.revalidate();
        });
        gbc.gridy = 2;
        add(btnPvP, gbc);
        
        // Botón para salir del juego
        JButton btnExit = createFlatButton("SALIR");
        btnExit.addActionListener(e -> System.exit(0));
        gbc.gridy = 3;
        add(btnExit, gbc);
    }

    /**
     * Crea un botón sin bordes con diseño plano.
     * @param text Texto a mostrar.
     * @return Botón configurado.
     */
    private JButton createFlatButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(300, 50)); // Botones un poco más altos
        btn.setBackground(Color.BLACK);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Impact", Font.PLAIN, 20)); // Fuente más robusta
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }
}