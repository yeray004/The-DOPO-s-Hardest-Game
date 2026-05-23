package persistence;

import domain.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

/**
 * Clase encargada de serializar y guardar el estado específico del nivel actual.
 * @author Yeray Guacheta
 * @version 2.2
 */
public class GameSaver {
    private static final Logger LOGGER = Logger.getLogger(GameSaver.class.getName());

    /**
     * Guarda únicamente la información del nivel activo y las estadísticas esenciales.
     * @param current Instancia del nivel actual.
     * @param timeLeft Tiempo restante de la partida.
     * @param totalDeaths Contador global de muertes.
     * @param filePath Ruta de destino del archivo.
     * @return true si se guardó con éxito, false si ocurrió un error.
     */
    public boolean save(Level current, int timeLeft, int totalDeaths, String filePath) {
        if (current == null) return false;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // guarda solo las métricas numéricas de la partida
            writer.println("STATE " + timeLeft + " " + totalDeaths);

            // serializa únicamente los elementos vivos contenidos en este nivel específico
            for (Element e : current.getAllElements()) {
                if (e instanceof Player) {
                    Player p = (Player) e;
                    writer.println("PLAYER " + p.getX() + " " + p.getY() + " "
                            + p.getExtraLives() + " " + p.getBorderColorName() + " "
                            + p.getCollectedCoins());
                } 
                else if (e instanceof Coin) {
                    Coin c = (Coin) e;
                    writer.println("COIN " + c.getX() + " " + c.getY() + " " + c.getColor() + " " + c.isCollected());
                } 
                else if (e instanceof Enemy) {
                    writer.println("ENEMY " + e.getX() + " " + e.getY() + " " + e.getSpriteType());
                }
                else if (e instanceof SpecialElement) {
                    SpecialElement special = (SpecialElement) e;
                    writer.println("SPECIAL " + special.getX() + " " + special.getY() + " "
                            + special.getSpriteType() + " " + special.isActive());
                }
            }
            return true;
        } catch (IOException ex) {
            DOPOsHardestGameException gameException = new DOPOsHardestGameException(DOPOsHardestGameException.GAME_SAVE_ERROR);
            LOGGER.severe(gameException.getMessage() + " " + DOPOsHardestGameException.IO_ERROR
                    + " Cause: " + ex.getMessage());
            System.out.println(gameException.getMessage());
            return false;
        }
    }
}
