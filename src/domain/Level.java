package domain;

import java.awt.Rectangle;
import java.util.*;

/**
 * Representa un nivel del juego gestionando el mapa y los elementos dinámicos.
 * 
 * @author Yeray Guacheta
 * @version 1.3
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
    private List<SpecialElement> specialElements;
    private boolean completed;
    private String winnerMessage;
    private int timeLimit;
    private int lastDeathCount;

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
        specialElements = new ArrayList<>();
        completed = false;
        winnerMessage = null;
        timeLimit = 60;
        lastDeathCount = 0;
    }
    
    /** Reinicia el estado y posición de todos los elementos del nivel. */
    public void resetLevel() {
        completed = false;
        winnerMessage = null;
        for (Element e : getAllElements()) {
            e.reset();
        }
    }

    /**
     * Reinicia el nivel desde el inicio original de cada jugador.
     * Se usa cuando se agota el tiempo o se empieza de nuevo la configuracion.
     */
    public void resetLevelFromStart() {
        lastDeathCount = 0;
        for (Player p : players) {
            p.resetRespawnPoint();
        }
        resetLevel();
        reactivateSpecialElements();
    }

    /**Reactiva los elementos especiales cuando el nivel empieza desde cero.*/
    private void reactivateSpecialElements() {
        for (SpecialElement specialElement : specialElements) {
            specialElement.reactivate();
        }
    }

    /**
     * Reubica jugadores y enemigos sin limpiar monedas ni skins temporales.
     * Sirve para separar entidades despues de un contacto no mortal.
     */
    private void resetActorsPositionOnly() {
        for (Player p : players) {
            p.resetPositionOnly();
        }
        for (Enemy e : enemies) {
            if (e.isVisible()) {
                e.reset();
            }
        }
    }

    /**Limpia el contador de muertes del evento actual.*/
    private void clearLastDeathCount() {
        lastDeathCount = 0;
    }

    /**Registra una muerte producida durante el evento actual.*/
    public void registerDeath() {
        registerDeaths(1);
    }

    /**Registra varias muertes producidas durante el evento actual.
     * @param deaths Cantidad de muertes a sumar.*/
    public void registerDeaths(int deaths) {
        lastDeathCount += Math.max(0, deaths);
    }

    /**Consume las muertes registradas para que la fachada actualice el marcador.
     * @return Cantidad de muertes producidas en el ultimo evento.*/
    public int consumeDeathCount() {
        int deaths = lastDeathCount;
        lastDeathCount = 0;
        return deaths;
    }

    /**Obtiene las muertes registradas sin limpiarlas.
     * @return Cantidad de muertes del evento actual.*/
    public int getLastDeathCount() {
        return lastDeathCount;
    }

    /**
     * Intenta mover al jugador validando colisiones con muros y activando interacciones.
     * @param p Jugador que se desea mover.
     * @param dx Dirección en el eje X (-1, 0, 1).
     * @param dy Dirección en el eje Y (-1, 0, 1).
     * @return true si el jugador murió durante el movimiento.*/
    public boolean attemptPlayerMove(Player p, int dx, int dy) {
        clearLastDeathCount();
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
            return checkInteractions(p);
        }
        return false;
    }

    // ELEMENTOS
    /** Actualiza todos los elementos del nivel usando polimorfismo.
     * Esto evita que el nivel dependa solo de enemigos y permite agregar elementos especiales actualizables.
     * @return true si algun jugador sufrio una muerte definitiva.*/
    public boolean updateElements() {
        clearLastDeathCount();
        boolean resetRequired = false;
        boolean anyPlayerDied = false;

        // La copia evita errores si en el futuro algun elemento modifica las listas durante su actualizacion.
        List<Element> elementsSnapshot = new ArrayList<>(getAllElements());
        for (Element element : elementsSnapshot) {
            ElementUpdateResult result = element.updateElement(this);
            resetRequired = resetRequired || result.requiresReset();
            anyPlayerDied = anyPlayerDied || result.hasPlayerDied();
        }

        if (!anyPlayerDied) {
            anyPlayerDied = handleAnyPlayerCollision();
        }
        
        if (anyPlayerDied) {
            resetLevel();
        } else if (resetRequired) {
            resetActorsPositionOnly();
        }
        return anyPlayerDied;
    }
    
    /**
     * Verifica si el nivel fue completado.
     * @param p Jugador a verificar.
     * @return true si se recogieron las monedas y se llegó a la meta.
     */
    public boolean isCompleted(Player p) {
        if (!areAllCoinsCollected()) return false;
        for (SafeZone sz : safeZones) {
            if (isGoalForPlayer(p, sz) && p.checkCollision(sz)) return true;
        }
        return false;
    }

    /**Verifica si todas las monedas obligatorias ya desaparecieron del tablero.
     * @return true si no queda ninguna moneda activa por recoger.*/
    private boolean areAllCoinsCollected() {
        for (Coin coin : coins) {
            if (!coin.isCollected()) return false;
        }
        return true;
    }

    /**
     * Verifica si una zona segura es la meta correcta para un jugador.
     * @param player Jugador evaluado.
     * @param safeZone Zona segura candidata.
     * @return true si la zona corresponde a la meta del jugador.
     */
    private boolean isGoalForPlayer(Player player, SafeZone safeZone) {
        if (player.usesInvertedGoal()) {
            return safeZone.isInitialZone();
        }
        return safeZone.isTarget();
    }

    //------------- INTERACCIONES EN EL NIVEL -------------
    /**Coordina las validaciones de recolección de monedas, muerte y victoria para un jugador específico.
     * @param p Jugador actual para evaluar interacciones.
     * @return true si el jugador murió por impacto.*/
    private boolean checkInteractions(Player p) {
        handleCheckpoint(p);
        handleCoinCollection(p);
        boolean diedBySpecialElement = handlePlayerSpecialElementInteractions(p);
        if (diedBySpecialElement) return true;
        boolean diedByEnemy = handleEnemyCollision(p);
        if (diedByEnemy) return true;
        boolean diedByPlayer = handlePlayerCollision(p);
        if (diedByPlayer) return true;
        if (!completed && isCompleted(p)) markCompleted(p);
        return false;
    }

    /**
     * Activa un checkpoint cuando el jugador entra a una zona segura intermedia.
     * @param p Jugador que puede actualizar su reaparicion.
     */
    private void handleCheckpoint(Player p) {
        for (SafeZone safeZone : safeZones) {
            if (safeZone.isCheckpoint() && p.checkCollision(safeZone)) {
                p.setRespawnPoint(safeZone.getSpawnX(), safeZone.getSpawnY());
            }
        }
    }

    /**Gestiona la recolección de monedas si el jugador entra en contacto con ellas.
     * @param p Jugador que recolecta.
     */
    private void handleCoinCollection(Player p) {
        for (Coin c : coins) {
            if (!c.isCollected() && p.checkCollision(c)) {
                c.collect(p);
            }
        }
    }

    /**
     * Gestiona el efecto de elementos especiales sobre un jugador.
     * @param p Jugador que puede activar elementos especiales.
     * @return true si el jugador murio por el elemento.
     */
    private boolean handlePlayerSpecialElementInteractions(Player p) {
        for (SpecialElement specialElement : specialElements) {
            if (specialElement.checkCollision(p)) {
                ElementUpdateResult result = specialElement.interactWithPlayer(p, this);
                if (result.hasPlayerDied()) {
                    resetLevel();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gestiona el efecto de elementos especiales sobre un enemigo.
     * @param enemy Enemigo que puede activar elementos especiales.
     * @return Resultado de la interaccion.
     */
    public ElementUpdateResult handleEnemySpecialElementInteractions(Enemy enemy) {
        for (SpecialElement specialElement : specialElements) {
            if (specialElement.checkCollision(enemy)) {
                ElementUpdateResult result = specialElement.interactWithEnemy(enemy, this);
                if (result != ElementUpdateResult.NONE) {
                    return result;
                }
            }
        }
        return ElementUpdateResult.NONE;
    }

    /**Verifica si un jugador específico colisionó con algún enemigo para procesar el daño.
     * @param p Jugador a evaluar.
     * @return true si ocurrió una colisión mortal, false si se absorbió o no hubo contacto.*/
    private boolean handleEnemyCollision(Player p) {
        for (Enemy e : enemies) {
            if (e.checkCollision(p)) {
                boolean lethal = p.hitByEnemy();
                if (lethal) {
                    registerDeath();
                    resetLevel();
                }
                return lethal;
            }
        }
        return false;
    }

    /**
     * Evalua si el jugador actual choco contra otro jugador en modo multijugador.
     * @param player Jugador que acaba de moverse.
     * @return true si la colision produjo muerte para ambos jugadores.
     */
    private boolean handlePlayerCollision(Player player) {
        for (Player other : players) {
            if (player != other && player.checkCollision(other)) {
                player.die();
                other.die();
                registerDeaths(2);
                resetLevel();
                return true;
            }
        }
        return false;
    }

    /**
     * Evalua colisiones entre todos los jugadores activos.
     * @return true si dos jugadores chocaron entre si.
     */
    private boolean handleAnyPlayerCollision() {
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                Player first = players.get(i);
                Player second = players.get(j);
                if (first.checkCollision(second)) {
                    first.die();
                    second.die();
                    registerDeaths(2);
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Registra la victoria del nivel para que la presentacion muestre el mensaje correspondiente.
     * @param p Jugador ganador.
     */
    private void markCompleted(Player p) {
        completed = true;
        winnerMessage = "¡Victoria para: " + p.getSpriteType() + "!";
    }

    /**
     * Indica si existe una victoria pendiente por mostrar.
     * @return true si el nivel fue completado y aun debe notificarse.
     */
    public boolean hasPendingVictory() {
        return completed;
    }

    /**
     * Obtiene el mensaje de victoria construido por el dominio.
     * @return Mensaje para mostrar en la interfaz.
     */
    public String getWinnerMessage() {
        return winnerMessage != null ? winnerMessage : "¡Nivel completado!";
    }

    /**Limpia el estado de victoria pendiente del nivel.*/
    public void clearCompletion() {
        completed = false;
        winnerMessage = null;
    }

    /**Establece el tiempo limite configurado para el nivel.
     * @param timeLimit Segundos maximos disponibles.*/
    public void setTimeLimit(int timeLimit) {
        if (timeLimit > 0) {
            this.timeLimit = timeLimit;
        }
    }

    /**Obtiene el tiempo limite del nivel.
     * @return Segundos configurados para completar el nivel.*/
    public int getTimeLimit() { return timeLimit; }

    /**Obtiene una posicion inicial basada en la primera zona segura inicial.
     * @return Arreglo con coordenadas X y Y en pixeles.*/
    public int[] getInitialSpawnPosition() {
        for (SafeZone safeZone : safeZones) {
            if (safeZone.isInitialZone()) {
                return new int[] {safeZone.getSpawnX(), safeZone.getSpawnY()};
            }
        }
        return new int[] {2 * 40, 4 * 40};
    }

    /**Obtiene una posicion inicial inversa basada en la zona segura final.
     * @return Arreglo con coordenadas X y Y en pixeles.*/
    public int[] getTargetSpawnPosition() {
        for (SafeZone safeZone : safeZones) {
            if (safeZone.isTarget()) {
                return new int[] {safeZone.getSpawnX(), safeZone.getSpawnY()};
            }
        }
        return new int[] {3 * 40, 4 * 40};
    }

    /**Cuenta cuantas monedas ya fueron tomadas del tablero.
     * @return Cantidad de monedas recolectadas globalmente en el nivel.*/
    public int getCollectedCoinCount() {
        int collected = 0;
        for (Coin coin : coins) {
            if (coin.isCollected()) collected++;
        }
        return collected;
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
        all.addAll(specialElements);
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


    /**
     * Obtiene el jugador mas cercano a una posicion del tablero.
     * Se usa para que los enemigos de persecucion no dependan siempre del jugador uno.
     * @param x Coordenada X de referencia.
     * @param y Coordenada Y de referencia.
     * @return Jugador mas cercano o null si no hay jugadores activos.
     */
    public Player getNearestPlayerTo(int x, int y) {
        Player nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Player player : players) {
            double distance = Math.hypot(player.getX() - x, player.getY() - y);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = player;
            }
        }
        return nearest;
    }

    /**Obtiene la lista de elementos especiales activos del nivel.
     * @return lista de objetos SpecialElement.*/
    public List<SpecialElement> getSpecialElements() { return specialElements; }
}
