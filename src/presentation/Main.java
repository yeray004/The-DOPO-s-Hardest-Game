package presentation;

import domain.DOPOsHardestGame;

/**
 * Clase principal para iniciar la aplicación.
 * @author Yeray Guacheta
 * @version 1.3
 */
public class Main {
    public static void main(String[] args) {
        // La presentación envía rutas
        String[] mapas = {"nivel1.txt", "nivel2.txt", "nivel3.txt"};
        DOPOsHardestGame game = new DOPOsHardestGame(mapas);
        
        GameFrame frame = new GameFrame(game);
        frame.setVisible(true);
    }
}
