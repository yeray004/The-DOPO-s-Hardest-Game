package domain;

import persistence.LevelLoader;
import java.util.*;

/**
 * Fachada principal del juego. Centraliza la comunicación entre 
 * la capa de presentación y la lógica del dominio.
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
     * Constructor que inicializa el juego cargando los niveles desde sus rutas.
     * @param levelPaths Arreglo con las rutas de los archivos de texto.
     */
    public DOPOsHardestGame(String[] levelPaths) {
    	currentLevelIndex = 0;
    	totalDeaths = 0;
    	timeLeft = 60;
    	
        this.levels = new ArrayList<>();
        LevelLoader loader = new LevelLoader();
        
        for (String path : levelPaths) {
            Level lvl = loader.loadLevel(path);
            if (lvl != null) {
                this.levels.add(lvl);
            }
        }
        
    }
    
    /** Actualiza el juego mientras está en marcha. */
    public void update() {
        Level current = getCurrentLevel();
        if (current != null) {
        	if (current.updateEnemies()) {
                totalDeaths++; // Suma la muerte al marcador global
            }
        }
    }

    /** Avanza al siguiente nivel si existe. */
    public void nextLevel() {
        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
        }
    }
    
    /** Reinicia el nivel actual, el contador y suma una muerte al contador. */
    public void restartLevel() {
        Level current = getCurrentLevel();
        if (current != null) {
            current.resetLevel();
            timeLeft = 60; // Reinicia el cronómetro
            totalDeaths++; // El tiempo agotado cuenta como muerte
        }
    }
    
    /**
     * Procesa el intento de movimiento del jugador principal.
     * @param dx Cambio solicitado en el eje X.
     * @param dy Cambio solicitado en el eje Y.
     */
    public void movePlayer(int dx, int dy) {
        Level current = getCurrentLevel();
        if (current != null && !current.getPlayers().isEmpty()) {
            Player p = current.getPlayers().get(0);
            current.attemptPlayerMove(p, p.getX() + dx, p.getY() + dy);
        }
    }
    
    /** Reduce el tiempo disponible en 1 segundo. */
    public void decreaseTime() {
        if (timeLeft > 0) {
            timeLeft--;
        }
    }
    
    
    public void handlePlayerMovement(int dx, int dy) {
        Level current = getCurrentLevel();
        if (current != null && !current.getPlayers().isEmpty()) {
            Player p = current.getPlayers().get(0);
            current.attemptPlayerMove(p, dx, dy);
        }
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
    
    // --- Métodos de Fachada para la Presentación
    /**
     * Retorna la matriz que define la estructura física del mapa actual.
     * @return Matriz de enteros o null si no hay nivel cargado.
     */
    public int[][] getMapData() {
        return (getCurrentLevel() != null) ? getCurrentLevel().getMapTemplate() : null;
    }
    
    /**
     * Entrega una lista de objetos de datos simples para ser dibujados.
     * Esto evita que la presentación conozca las clases 'Element', 'Player', etc.
     * @return data Lista de elementos para ser dibujados por la vista.
     */
    public List<RenderData> getEntitiesToDraw() {
        List<RenderData> data = new ArrayList<>();
        for (Element e : getCurrentLevel().getAllElements()) {
        	if (e.isVisible()) {
                data.add(new RenderData(e.getX(), e.getY(), e.getSpriteType()));
            }
        }
        return data;
    }
    
    /**
     * Entrega la lista unificada de elementos que deben ser dibujados.
     * @return Lista de elementos renderizables filtrados por el nivel.
     */
    public List<Element> getRenderables() {
        return (getCurrentLevel() != null) ? getCurrentLevel().getAllElements() : new ArrayList<>();
    }

}