package domain;

import java.util.*;

/**
 * Clase principal que gestiona el estado general del juego, niveles y estadísticas.
 * 
 * @author Yeray Guacheta
 * @version 1.0
 */
public class DOPOsHardestGame {
    private int currentLevelIndex;
    private int totalDeaths;
    private int timeLeft;
    private List<Level> levels;

    /**
     * Constructor para iniciar el juego con los niveles cargados.
     * @param levels Lista de niveles que componen el juego.
     */
    public DOPOsHardestGame(List<Level> levels) {
        this.levels = levels;
        this.currentLevelIndex = 0;
        this.totalDeaths = 0;
        this.timeLeft = 60; //60 segundos por defecto (se ajustará por nivel).
    }

    /**
     * Avanza al siguiente nivel si existe.
     */
    public void nextLevel() {
        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
        }
    }

    /**
     * Reinicia el nivel actual y suma una muerte al contador.
     */
    public void restartLevel() {
        totalDeaths++;
        // luego se llama a un método del nivel para reiniciar posiciones
    }

    /**
     * Obtiene el nivel que se está jugando actualmente.
     * @return El nivel actual o null si no hay niveles.
     */
    public Level getCurrentLevel() {
        if (levels != null && !levels.isEmpty()) {
            return levels.get(currentLevelIndex);
        }
        return null;
    }
    /**
     * Obtiene el número total de muertes acumuladas durante la partida.
     * @return cantidad de muertes totales.
     */
    public int getTotalDeaths() { return totalDeaths; }
    /**
     * Obtiene el tiempo restante disponible para completar el nivel actual.
     * @return segundos restantes en el temporizador.
     */
    public int getTimeLeft() { return timeLeft; }
}