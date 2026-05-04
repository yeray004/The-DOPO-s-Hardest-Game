package presentation;

import domain.DOPOsHardestGame;
import domain.Level;
import persistence.LevelLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase principal para arrancar la aplicación.
 * 
 * @author Yeray Adrian
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        LevelLoader loader = new LevelLoader();
        Level level1 = loader.loadLevel("nivel1.txt");
        
        List<Level> levels = new ArrayList<>();
        if (level1 != null) {
            levels.add(level1);
        }
        
        DOPOsHardestGame game = new DOPOsHardestGame(levels);
        
        GameFrame frame = new GameFrame(game);
        frame.setVisible(true);
    }
}