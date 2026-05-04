package domain;

import java.util.*;

/**
 * Representa un nivel del juego gestionando el mapa y los elementos dinámicos.
 * 
 * @author Yeray Guacheta
 * @version 1.0
 */
public class Level {
    private int width;
    private int height;
    
    private int[][] mapTemplate; 
    
    private List<Player> players;
    private List<Enemy> enemies;
    private List<Coin> coins;
    private List<SafeZone> safeZones;
    private List<Wall> walls;

    /**
     * Constructor del nivel.
     * @param width Ancho del mapa.
     * @param height Alto del mapa.
     * @param mapTemplate Matriz con la estructura del mapa.
     */
    public Level(int width, int height, int[][] mapTemplate) {
        this.width = width;
        this.height = height;
        this.mapTemplate = mapTemplate;
        
        players = new ArrayList<>();
        enemies = new ArrayList<>();
        coins = new ArrayList<>();
        safeZones = new ArrayList<>();
        walls = new ArrayList<>();
    }

    /**
     * Verifica las colisiones de los jugadores con el entorno.
     * @return true si hubo una colisión mortal (enemigo).
     */
    public boolean checkCollisions() {
        boolean collisionDetected = false;
        // lógica de colisiones
        return collisionDetected;
    }

    /**
     * Verifica si el nivel fue completado.
     * @return true si se recogieron las monedas y se llegó a la meta.
     */
    public boolean isCompleted() {
        boolean completed = false;
        // lógica de victoria
        return completed;
    }

    /**Obtiene la matriz bidimensional que define la estructura física del nivel.
     * @return plantilla numérica del mapa.*/
    public int[][] getMapTemplate() { return mapTemplate; }

    /**Obtiene la lista de jugadores que participan actualmente en el nivel.
     * @return lista de objetos Player.*/
    public List<Player> getPlayers() { return players; }

    /**Obtiene la lista de enemigos activos que representan obstáculos en el mapa.
     * @return lista de objetos Enemy.*/
    public List<Enemy> getEnemies() { return enemies; }

    /**Obtiene la lista de monedas amarillas que deben ser recolectadas.
     * @return lista de objetos Coin.*/
    public List<Coin> getCoins() { return coins; }

    /**Obtiene la lista de zonas seguras (verdes) disponibles en el mapa.
     * @return lista de objetos SafeZone.*/
    public List<SafeZone> getSafeZones() { return safeZones; }

    /**Obtiene la lista de paredes sólidas que limitan el movimiento en el tablero.
     * @return lista de objetos Wall.*/
    public List<Wall> getWalls() { return walls; }
}