package domain;

import persistence.LevelLoader;

import java.awt.Rectangle;
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
    
    private String gameMode = "Single";

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
    
    //------------- MODO DE JUEGO -------------
    /**Configura la modalidad de juego actual y prepara los personajes requeridos.
     * @param mode Nombre del modo de juego ("Single" o "PvP").*/
    public void setGameMode(String mode) {
        this.gameMode = mode;
        setupPlayersForMode();
    }

    /**Prepara e instancia los jugadores necesarios en el nivel activo según el modo.*/
    public void setupPlayersForMode() {
        Level current = getCurrentLevel();
        if (current != null) {
            current.getPlayers().clear();
            // Instancia al Jugador 1 base (Siempre presente)
            current.getPlayers().add(new Player(2 * 40, 4 * 40));
            // Si el modo requiere PvP, añade al segundo jugador polimórficamente
            if ("PvP".equals(gameMode)) {
                current.getPlayers().add(new Player(3 * 40, 4 * 40));
            }
            current.resetLevel();
        }
    }
    
    //------------- SKINS DE JUGADOR -------------
    /**Asigna las skins iniciales a los jugadores antes de empezar el nivel.
     * @param skinP1 Nombre de la skin del Jugador 1.
     * @param skinP2 Nombre de la skin del Jugador 2 (puede ser null en modo Single).*/
    public void setInitialSkins(String skinP1, String skinP2) {
        Level current = getCurrentLevel();
        if (current == null || current.getPlayers().isEmpty()) return;

        // Jugador 1
        applySkin(current.getPlayers().get(0), skinP1);

        // Jugador 2 (Si existe en PvP)
        if (current.getPlayers().size() > 1 && skinP2 != null) {
            applySkin(current.getPlayers().get(1), skinP2);
        }
    }

    /**Aplica el estado (Skin) polimórfico al jugador.
     * @param p Jugador a modificar.
     * @param skin Nombre de la skin.*/
    private void applySkin(Player p, String skin) {
        if (skin != null) {
            p.setInitialSkin(skin);
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
    
    /**Mueve a un jugador específico por su índice e incrementa las muertes si colisiona con un enemigo.
     * @param playerIndex Índice del jugador en la lista.
     * @param dx Dirección en el eje X (-1, 0, 1).
     * @param dy Dirección en el eje Y (-1, 0, 1).*/
    public void handlePlayerMovement(int playerIndex, int dx, int dy) {
        Level current = getCurrentLevel();
        if (current != null && current.getPlayers().size() > playerIndex) {
            if (current.attemptPlayerMove(current.getPlayers().get(playerIndex), dx, dy)) {
                totalDeaths++;
            }
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
    
    // ------------- Métodos de Fachada para la Presentación -------------
    /**
     * Retorna la matriz que define la estructura física del mapa actual.
     * @return Matriz de enteros o null si no hay nivel cargado.
     */
    public int[][] getMapData() {
        return (getCurrentLevel() != null) ? getCurrentLevel().getMapTemplate() : null;
    }
    
    /**Entrega una lista de objetos de datos simples para ser dibujados.
     * @return Lista de elementos para renderizar con sus dimensiones exactas.
     */
    public List<RenderData> getEntitiesToDraw() {
        List<RenderData> data = new ArrayList<>();
        for (Element e : getCurrentLevel().getAllElements()) {
            if (e.isVisible() && e instanceof Collidable) {
                // Se extrae la "caja" exacta que ocupa el elemento con su margen
                Rectangle bounds = ((Collidable) e).getHitbox().getBounds();
                data.add(new RenderData(bounds.x, bounds.y, bounds.width, bounds.height, e.getSpriteType()));
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