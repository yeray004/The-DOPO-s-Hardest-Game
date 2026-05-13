package domain;

import java.awt.Rectangle;
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
    
    /** Reinicia el estado y posición de todos los elementos del nivel. */
    public void resetLevel() {
        for (Element e : getAllElements()) {
            e.reset();
        }
    }

    /**
     * Intenta mover al jugador validando colisiones con muros y activando interacciones.
     * @param p Jugador que se desea mover.
     * @param dx Dirección en el eje X (-1, 0, 1).
     * @param dy Dirección en el eje Y (-1, 0, 1).
     */
    public void attemptPlayerMove(Player p, int dx, int dy) {
    	Rectangle nextHitbox = p.getNextHitbox(dx, dy);
        boolean canMove = true;
        
        for (Wall w : walls) {
            if (nextHitbox.intersects(w.getHitbox().getBounds2D())) {
                canMove = false; 
                break;
            }
        }
        
        if (canMove) {
            p.move(dx, dy);
            checkInteractions(p);
        }
    }
    
    // ENEMIGOS
    /** Actualiza la posición de todos los enemigos y gestiona colisiones con el jugador. */
    public boolean updateEnemies() {
        for (Enemy e : enemies) {
            boolean hitWall = false;
            for (Wall w : walls) {
                if (e.willCollideWith(w)) {
                    hitWall = true;
                    break;
                }
            }
            
            if (hitWall) {
                e.reverseDirection();
            } else {
                e.updatePosition();
            }
            
            if (!players.isEmpty() && e.checkCollision(players.get(0))) {
                players.get(0).die();
                resetLevel(); 
                return true; // Notifica la muerte a la fachada
            }
        }
        return false;
    }
    
    /**
     * Verifica si el nivel fue completado.
     * @param p Jugador a verificar.
     * @return true si se recogieron las monedas y se llegó a la meta.
     */
    public boolean isCompleted(Player p) {
        if (p.getCollectedCoins() < coins.size()) return false;
        for (SafeZone sz : safeZones) {
            if (sz.isTarget() && p.checkCollision(sz)) return true;
        }
        return false;
    }
    
    //INTERACCIONES EN EL NIVEL
    
    /**Coordina las validaciones de recolección de monedas, muerte y victoria.
     * @param p Jugador actual para evaluar interacciones.
     */
    private void checkInteractions(Player p) {
    	handleCoinCollection(p);
        handleEnemyCollision(p);
        if (isCompleted(p)) announceWinner(p);
    }

    /**Gestiona la recolección de monedas si el jugador entra en contacto con ellas.
     * @param p Jugador que recolecta.
     */
    private void handleCoinCollection(Player p) {
        for (Coin c : coins) {
            if (p.checkCollision(c)) {
                c.collect();
                p.collectCoin();
            }
        }
    }

    /**Verifica si el jugador colisionó con algún enemigo para reiniciar el nivel.
     * @param p Jugador a evaluar.
     * @return true si ocurrió una colisión mortal, false de lo contrario.
     */
    private boolean handleEnemyCollision(Player p) {
        for (Enemy e : enemies) {
            if (p.checkCollision(e)) {
                p.die();
                resetLevel(); 
                return true;
            }
        }
        return false;
    }
    
    /**
     * Muestra un mensaje informativo al alcanzar la victoria.
     * @param p Jugador ganador.
     */
    private void announceWinner(Player p) {
        // En PvP - el nombre o color de 'p' para decir quién ganó
        System.out.println("¡Victoria para: " + p.getSpriteType() + "!");
    }

    /**
     * Obtiene una lista unificada de todos los elementos presentes en el nivel.
     * @return Lista completa de objetos tipo Element.
     */
    public List<Element> getAllElements() {
    	List<Element> all = new ArrayList<>();
        all.addAll(players);
        all.addAll(enemies);
        all.addAll(coins);
        all.addAll(safeZones);
        return all;
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