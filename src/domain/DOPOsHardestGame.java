package domain;

import persistence.LevelLoader;

import java.awt.Rectangle;
import java.util.*;

/**
 * Fachada principal del juego. Centraliza la comunicación entre 
 * la capa de presentación y la lógica del dominio.
 * 
 * @author Yeray Guacheta
 * @version 3.2
 */
public class DOPOsHardestGame {
    private int currentLevelIndex;
    private int totalDeaths;
    private int timeLeft;
    private List<Level> levels;
    private List<String> levelNames;
    
    private String gameMode = "Single";
    private String selectedSkinP1 = "Blinky";
    private String selectedSkinP2 = "Inky";
    private String selectedBorderP1 = "Black";
    private String selectedBorderP2 = "White";

    /**
     * Constructor que inicializa el juego cargando los niveles desde sus rutas.
     * @param levelPaths Arreglo con las rutas de los archivos de texto.
     */
    public DOPOsHardestGame(String[] levelPaths) {
    	currentLevelIndex = 0;
    	totalDeaths = 0;
    	timeLeft = 60;
    	
        this.levels = new ArrayList<>();
        this.levelNames = new ArrayList<>();
        LevelLoader loader = new LevelLoader();
        
        for (String path : levelPaths) {
            Level lvl = loader.loadLevel(path);
            if (lvl != null) {
                this.levels.add(lvl);
                this.levelNames.add(buildLevelName(path, this.levels.size()));
            }
        }
        if (!this.levels.isEmpty()) {
            this.timeLeft = getCurrentLevel().getTimeLimit();
        }
    }

    /**Construye un nombre simple para mostrar la configuracion en la interfaz.
     * @param path Ruta original del archivo.
     * @param index Numero del nivel cargado.
     * @return Nombre legible para el usuario.*/
    private String buildLevelName(String path, int index) {
        String cleanPath = path.replace('\\', '/');
        int slashIndex = cleanPath.lastIndexOf('/');
        String fileName = slashIndex >= 0 ? cleanPath.substring(slashIndex + 1) : cleanPath;
        return "Configuración " + index + " - " + fileName;
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
            int[] initialSpawn = current.getInitialSpawnPosition();
            int[] targetSpawn = current.getTargetSpawnPosition();
            // Instancia al Jugador 1 base (Siempre presente)
            Player playerOne = new Player(initialSpawn[0], initialSpawn[1]);
            playerOne.setInvertedGoal(false);
            current.getPlayers().add(playerOne);
            // Si el modo requiere PvP, añade al segundo jugador polimórficamente
            if ("PvP".equals(gameMode)) {
                Player playerTwo = new Player(targetSpawn[0], targetSpawn[1]);
                playerTwo.setInvertedGoal(true);
                current.getPlayers().add(playerTwo);
            }
            applyStoredPlayerPreferences();
            current.resetLevelFromStart();
            timeLeft = current.getTimeLimit();
        }
    }

    /**Selecciona una configuracion cargada desde la interfaz.
     * @param levelIndex Indice de la configuracion elegida.*/
    public void selectLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levels.size()) {
            currentLevelIndex = levelIndex;
            setupPlayersForMode();
            totalDeaths = 0;
        }
    }

    /**Obtiene los nombres de configuraciones disponibles.
     * @return Arreglo con los nombres de los niveles cargados.*/
    public String[] getLevelNames() {
        return levelNames.toArray(new String[0]);
    }

    /**Obtiene el indice de la configuracion actual.
     * @return Posicion del nivel activo.*/
    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }
    
    //------------- SKINS DE JUGADOR -------------
    /**Asigna las skins iniciales a los jugadores antes de empezar el nivel.
     * @param skinP1 Nombre de la skin del Jugador 1.
     * @param skinP2 Nombre de la skin del Jugador 2 (puede ser null en modo Single).*/
    public void setInitialSkins(String skinP1, String skinP2) {
        selectedSkinP1 = skinP1 != null ? skinP1 : selectedSkinP1;
        selectedSkinP2 = skinP2 != null ? skinP2 : selectedSkinP2;
        Level current = getCurrentLevel();
        if (current == null || current.getPlayers().isEmpty()) return;

        // Jugador 1
        applySkin(current.getPlayers().get(0), selectedSkinP1);

        // Jugador 2 (Si existe en PvP)
        if (current.getPlayers().size() > 1 && selectedSkinP2 != null) {
            applySkin(current.getPlayers().get(1), selectedSkinP2);
        }
    }

    /**Asigna los colores de borde elegidos para distinguir jugadores.
     * @param borderP1 Color de borde del jugador 1.
     * @param borderP2 Color de borde del jugador 2.*/
    public void setPlayerBorders(String borderP1, String borderP2) {
        selectedBorderP1 = borderP1 != null ? borderP1 : selectedBorderP1;
        selectedBorderP2 = borderP2 != null ? borderP2 : selectedBorderP2;
        Level current = getCurrentLevel();
        if (current == null || current.getPlayers().isEmpty()) return;
        current.getPlayers().get(0).setBorderColorName(selectedBorderP1);
        if (current.getPlayers().size() > 1 && selectedBorderP2 != null) {
            current.getPlayers().get(1).setBorderColorName(selectedBorderP2);
        }
    }

    /**Aplica las preferencias guardadas al cambiar de configuracion o nivel.*/
    private void applyStoredPlayerPreferences() {
        setInitialSkins(selectedSkinP1, "PvP".equals(gameMode) ? selectedSkinP2 : null);
        setPlayerBorders(selectedBorderP1, "PvP".equals(gameMode) ? selectedBorderP2 : null);
    }

    /**Aplica el estado (Skin) polimórfico al jugador.
     * @param p Jugador a modificar.
     * @param skin Nombre de la skin.*/
    private void applySkin(Player p, String skin) {
        if (skin != null) {
            p.setInitialSkin(skin);
        }
    }
    
    /** Actualiza el juego mientras esta en marcha. */
    public void update() {
        Level current = getCurrentLevel();
        if (current != null && !current.hasPendingVictory()) {
        	current.updateElements();
            totalDeaths += current.consumeDeathCount(); // Suma las muertes registradas por el nivel
        }
    }

    /** Avanza al siguiente nivel si existe. */
    public void nextLevel() {
        if (currentLevelIndex < levels.size() - 1) {
            getCurrentLevel().clearCompletion();
            currentLevelIndex++;
            Level current = getCurrentLevel();
            if (current != null) {
                setupPlayersForMode();
                timeLeft = current.getTimeLimit();
            }
        }
    }

    /**
     * Indica si existe un nivel posterior disponible.
     * @return true si se puede continuar al siguiente nivel.
     */
    public boolean hasNextLevel() {
        return currentLevelIndex < levels.size() - 1;
    }

    /**
     * Continua despues de una victoria, avanzando de nivel si es posible.
     * @return true si avanzo a otro nivel, false si no hay mas niveles.
     */
    public boolean continueAfterVictory() {
        if (hasNextLevel()) {
            nextLevel();
            return true;
        }
        clearVictory();
        return false;
    }

    /**
     * Indica si el nivel actual tiene una victoria pendiente de mostrar.
     * @return true si hay mensaje de victoria pendiente.
     */
    public boolean hasPendingVictory() {
        Level current = getCurrentLevel();
        return current != null && current.hasPendingVictory();
    }

    /**
     * Obtiene el mensaje de victoria del nivel actual.
     * @return Mensaje construido en el dominio.
     */
    public String getVictoryMessage() {
        Level current = getCurrentLevel();
        return current != null ? current.getWinnerMessage() : "¡Nivel completado!";
    }

    /**Limpia la victoria pendiente del nivel actual.*/
    public void clearVictory() {
        Level current = getCurrentLevel();
        if (current != null) {
            current.clearCompletion();
        }
    }
    
    /** Reinicia el nivel actual, el contador y suma una muerte al contador. */
    public void restartLevel() {
        Level current = getCurrentLevel();
        if (current != null) {
            current.resetLevelFromStart();
            timeLeft = current.getTimeLimit(); // Reinicia el cronómetro
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
        if (current != null && !current.hasPendingVictory() && !current.getPlayers().isEmpty()) {
            Player p = current.getPlayers().get(0);
            current.attemptPlayerMove(p, dx, dy);
            totalDeaths += current.consumeDeathCount();
        }
    }
    
    /** Reduce el tiempo disponible en 1 segundo. */
    public void decreaseTime() {
        if (timeLeft > 0) {
            timeLeft--;
        }
    }
    
    /**Mueve a un jugador específico por su índice e incrementa las muertes si colisiona con un enemigo.
     * @param playerIndex Indice del jugador en la lista.
     * @param dx Dirección en el eje X (-1, 0, 1).
     * @param dy Dirección en el eje Y (-1, 0, 1).*/
    public void handlePlayerMovement(int playerIndex, int dx, int dy) {
        Level current = getCurrentLevel();
        if (current != null && !current.hasPendingVictory() && current.getPlayers().size() > playerIndex) {
            current.attemptPlayerMove(current.getPlayers().get(playerIndex), dx, dy);
            totalDeaths += current.consumeDeathCount();
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
        Level current = getCurrentLevel();
        if (current == null) return data;
        for (Element e : current.getAllElements()) {
            if (e.isVisible() && e instanceof Collidable) {
                if (e instanceof Player) {
                    Player player = (Player) e;
                    // Se dibuja el sprite completo, no solo la caja interna de colisión.
                    Rectangle bounds = new Rectangle(player.getX() + player.getMargin(),
                            player.getY() + player.getMargin(),
                            player.getCurrentHitboxSize(),
                            player.getCurrentHitboxSize());
                    data.add(new RenderData(bounds.x, bounds.y, bounds.width, bounds.height,
                            e.getSpriteType(), player.getBorderColorName()));
                } else {
                    // Se extrae la "caja" exacta que ocupa el elemento con su margen
                    Rectangle bounds = ((Collidable) e).getHitbox().getBounds();
                    data.add(new RenderData(bounds.x, bounds.y, bounds.width, bounds.height, e.getSpriteType()));
                }
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
    
    // ------------- Métodos de Peristencia -------------

    /**
     * Coordina el proceso de guardado enviando únicamente el nivel activo y sus métricas.
     * @param filePath Ruta donde se almacenará el archivo de texto.
     * @return true si la operación en la capa de persistencia fue exitosa.
     */
    public boolean saveCurrentGame(String filePath) {
        persistence.GameSaver saver = new persistence.GameSaver();
        // Se desacopla el objeto del juego pasando solo el nivel y sus tipos primitivos
        return saver.save(getCurrentLevel(), this.timeLeft, this.totalDeaths, filePath);
    }
    
    public void setTimeLeft(int timeLeft) { this.timeLeft = timeLeft; }
    public void setTotalDeaths(int totalDeaths) { this.totalDeaths = totalDeaths; }

    /**
     * Carga el estado de una partida guardada modificando el nivel activo.
     */
    public boolean loadCurrentGame(String filePath) {
        persistence.GameLoader loader = new persistence.GameLoader();
        return loader.load(getCurrentLevel(), this, filePath);
    }
}
