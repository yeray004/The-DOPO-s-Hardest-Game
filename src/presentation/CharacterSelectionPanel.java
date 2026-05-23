package presentation;

import domain.DOPOsHardestGame;
import javax.swing.*;
import java.awt.*;

/**Panel intermedio para la selección de personajes antes de iniciar el nivel.
 * @author Yeray Guacheta
 * @version 1.1*/
public class CharacterSelectionPanel extends JPanel {

    /**Construye la interfaz de selección de skins.
     * @param frame Ventana principal.
     * @param gamePanel Panel del juego real.
     * @param game Fachada del juego.
     * @param isPvP Booleano que indica si se deben mostrar controles para el Jugador 2.*/
    public CharacterSelectionPanel(GameFrame frame, JPanel gamePanel, DOPOsHardestGame game, boolean isPvP) {
        setBackground(new Color(180, 180, 255));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        JLabel lblTitle = new JLabel("SELECCIÓN DE PERSONAJE", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Impact", Font.PLAIN, 40));
        add(lblTitle, gbc);

        String[] skins = {"Blinky", "Inky", "Clyde"};
        String[] borders = {"Black", "White", "Yellow", "Orange", "Cyan", "Magenta"};
        
        // Selector Jugador 1
        gbc.gridy = 1;
        add(new JLabel("Skin Jugador 1 (Flechas):"), gbc);
        JComboBox<String> comboP1 = new JComboBox<>(skins);
        gbc.gridy = 2;
        add(comboP1, gbc);

        // Selector de borde Jugador 1
        gbc.gridy = 3;
        add(new JLabel("Borde Jugador 1:"), gbc);
        JComboBox<String> borderP1 = new JComboBox<>(borders);
        gbc.gridy = 4;
        add(borderP1, gbc);

        // Selector Jugador 2 (Oculto si es SinglePlayer)
        JComboBox<String> comboP2 = new JComboBox<>(skins);
        JComboBox<String> borderP2 = new JComboBox<>(borders);
        if (isPvP) {
            gbc.gridy = 5;
            add(new JLabel("Skin Jugador 2 (WASD):"), gbc);
            gbc.gridy = 6;
            comboP2.setSelectedIndex(1); // Por defecto elige otro diferente
            add(comboP2, gbc);

            gbc.gridy = 7;
            add(new JLabel("Borde Jugador 2:"), gbc);
            gbc.gridy = 8;
            borderP2.setSelectedIndex(1); // Usa un borde diferente por defecto
            add(borderP2, gbc);
        }

        // Botón Jugar
        JButton btnPlay = new JButton("INICIAR JUEGO");
        btnPlay.setPreferredSize(new Dimension(250, 50));
        btnPlay.setBackground(Color.BLACK);
        btnPlay.setForeground(Color.WHITE);
        btnPlay.setFont(new Font("Impact", Font.PLAIN, 22));
        btnPlay.setFocusPainted(false);
        btnPlay.addActionListener(e -> {
            String skin2 = isPvP ? (String) comboP2.getSelectedItem() : null;
            String selectedBorderP2 = isPvP ? (String) borderP2.getSelectedItem() : null;
            game.setInitialSkins((String) comboP1.getSelectedItem(), skin2);
            game.setPlayerBorders((String) borderP1.getSelectedItem(), selectedBorderP2);
            
            frame.setContentPane(gamePanel);
            frame.revalidate();
            frame.startGameTimer();
            frame.refreshCounters();
            
            // Reanuda el hilo del juego usando el nuevo método
            GamePanel board = (GamePanel) gamePanel.getComponent(1);
            board.clearMovementState();
            board.resumeGame();
            board.requestFocusInWindow();
        });
        
        gbc.gridy = isPvP ? 9 : 5;
        gbc.insets = new Insets(30, 10, 10, 10);
        add(btnPlay, gbc);
    }
}
