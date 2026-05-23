package domain;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;

/**Representa un enemigo dentro del juego manejado por una estrategia de movimiento.
 * @author Yeray Guacheta
 * @version 2.3
 */
public class Enemy extends Element implements Collidable, MovableElement {
    protected List<Point> movement;
    protected int speed;
    private final int BASE_HITBOX = 25;
    protected int dx = 0;
    protected int dy = 1;
    protected int startX, startY;
    private EnemyStrategy strategy;
    private boolean destroyed;

    /**Constructor para un enemigo basado en estrategias.
     * @param x Posición inicial X.
     * @param y Posición inicial Y.
     * @param speed Velocidad de desplazamiento.
     * @param movement Lista de puntos de patrulla.
     * @param dx Dirección inicial X.
     * @param dy Dirección inicial Y.
     * @param strategy Estrategia de movimiento.
     * @throws DOPOsHardestGameException si la estrategia recibida es nula.*/
    public Enemy(int x, int y, int speed, List<Point> movement, int dx, int dy, EnemyStrategy strategy)
            throws DOPOsHardestGameException {
        super(x, y);
        this.speed = speed;
        this.movement = movement;
        this.dx = dx;
        this.dy = dy;
        this.startX = x;
        this.startY = y;
        this.destroyed = false;
        if (strategy == null) {
            throw new DOPOsHardestGameException(DOPOsHardestGameException.NULL_ENEMY_STRATEGY);
        }
        this.strategy = strategy;
    }

    /**Regresa el enemigo a su posición inicial.*/
    @Override
    public void reset() {
        x = startX;
        y = startY;
        destroyed = false;
    }

    /**Indica si el enemigo debe dibujarse y actualizarse.
     * @return true si no fue destruido por un elemento especial.*/
    @Override
    public boolean isVisible() {
        return !destroyed;
    }

    /**Marca el enemigo como destruido por un elemento especial.*/
    public void destroy() {
        destroyed = true;
    }

    /**Obtiene la velocidad actual del enemigo.
     * @return Velocidad en pixeles por actualizacion.*/
    @Override
    public int getSpeed() {
        return speed;
    }

    /**Obtiene la direccion horizontal actual.
     * @return Direccion en X.*/
    @Override
    public int getDx() {
        return dx;
    }

    /**Obtiene la direccion vertical actual.
     * @return Direccion en Y.*/
    @Override
    public int getDy() {
        return dy;
    }

    /**Cambia la direccion actual del enemigo.
     * @param dx Nueva direccion horizontal.
     * @param dy Nueva direccion vertical.*/
    @Override
    public void setDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    /**Obtiene la ruta de patrulla configurada para el enemigo.
     * @return Lista de puntos o null si no usa patrulla.*/
    @Override
    public List<Point> getMovementRoute() {
        return movement;
    }

    /**Valida si el próximo movimiento interseca con un muro.
     * @param w Muro a evaluar.
     * @return true si colisionará, false en caso contrario.*/
    public boolean willCollideWith(Wall w) {
        return willCollideWith((Collidable) w);
    }

    /**
     * Valida si el proximo movimiento interseca con cualquier objeto colisionable.
     * @param collidable Objeto a evaluar como bloqueo.
     * @return true si el movimiento siguiente genera colision.
     */
    public boolean willCollideWith(Collidable collidable) {
        return getNextHitbox().intersects(collidable.getHitbox().getBounds2D());
    }

    /**Invierte la dirección de movimiento actual del enemigo.*/
    public void reverseDirection() {
        dx *= -1;
        dy *= -1;
    }

    /**Actualiza la posición del enemigo delegando a su estrategia.
     * @param target Jugador objetivo para estrategias de persecución.*/
    public void updatePosition(Player target) {
        strategy.updatePosition(this, target);
    }

    /**
     * Actualiza este enemigo como un elemento general del nivel.
     * El movimiento sigue delegado a la estrategia y las colisiones se reportan al nivel.
     * @param level Nivel que contiene al enemigo y sus objetos de referencia.
     * @return Resultado de la actualizacion para que el nivel decida si debe reiniciar.
     */
    @Override
    public ElementUpdateResult updateElement(Level level) {
        if (level == null || destroyed) {
            return ElementUpdateResult.NONE;
        }

        int previousX = x;
        int previousY = y;

        if (willCollideWithBlockedZone(level)) {
            reverseDirection();
        } else {
            Player target = level.getNearestPlayerTo(getX(), getY());
            updatePosition(target);
        }

        // Evita que estrategias de persecucion entren a paredes o zonas seguras despues de moverse.
        if (isInsideBlockedZone(level)) {
            x = previousX;
            y = previousY;
            reverseDirection();
        }

        level.handleEnemySpecialElementInteractions(this);
        if (destroyed) {
            return ElementUpdateResult.NONE;
        }

        boolean anyPlayerDied = false;

        // Evalua el impacto con cada jugador activo del nivel
        for (Player p : level.getPlayers()) {
            if (checkCollision(p) && p.hitByEnemy()) {
                level.registerDeath();
                anyPlayerDied = true;
            }
        }

        if (anyPlayerDied) {
            return ElementUpdateResult.PLAYER_DIED;
        }
        return ElementUpdateResult.NONE;
    }

    /**
     * Valida si el siguiente paso toca una pared o cualquier zona segura.
     * @param level Nivel que contiene los bloqueos.
     * @return true si debe evitar el movimiento.
     */
    private boolean willCollideWithBlockedZone(Level level) {
        for (Wall w : level.getWalls()) {
            if (willCollideWith(w)) {
                return true;
            }
        }
        for (SafeZone safeZone : level.getSafeZones()) {
            if (willCollideWith(safeZone)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica si el enemigo quedo dentro de una pared o zona segura luego de moverse.
     * @param level Nivel que contiene los bloqueos.
     * @return true si la posicion actual no es valida.
     */
    private boolean isInsideBlockedZone(Level level) {
        for (Wall w : level.getWalls()) {
            if (checkCollision(w)) {
                return true;
            }
        }
        for (SafeZone safeZone : level.getSafeZones()) {
            if (checkCollision(safeZone)) {
                return true;
            }
        }
        return false;
    }

    /**Verifica la colisión actual con otro objeto colisionable.
     * @param other Objeto con el cual evaluar.
     * @return true si hay colisión, false en caso contrario.*/
    @Override
    public boolean checkCollision(Collidable other) {
        return !destroyed && getHitbox().intersects(other.getHitbox().getBounds2D());
    }

    /**Calcula el tamaño actual del hitbox según la estrategia.
     * @return Tamaño en píxeles.*/
    public int getCurrentHitboxSize() {
        return (int) (BASE_HITBOX * strategy.getSizeMultiplier());
    }

    /**Calcula el margen dinámico para el renderizado.
     * @return Píxeles de margen.*/
    public int getMargin() {
        return (40 - getCurrentHitboxSize()) / 2; 
    }
    
    /**Calcula la caja de colisión predictiva para el siguiente cuadro.
     * @return Rectángulo de la posición futura.*/
    public Rectangle getNextHitbox() {
    	int currentHitbox = getCurrentHitboxSize();
        int margin = getMargin();
        return new Rectangle((x + dx * speed) + margin,
        		(y + dy * speed) + margin,
        		currentHitbox,
        		currentHitbox);
    }

    /**Obtiene la forma geométrica de la colisión actual del enemigo.
     * @return Elipse que representa la hitbox.*/
    @Override
    public Shape getHitbox() {
    	int currentHitbox = getCurrentHitboxSize();
        int margin = getMargin();
        return new Ellipse2D.Double(x + margin,
        		y + margin,
        		currentHitbox,
        		currentHitbox);
    }

    /**Obtiene el tipo de sprite delegando a la estrategia actual.
     * @return Nombre identificador del sprite.*/
    @Override
    public String getSpriteType() {
        return strategy.getSpriteType();
    }
    
    /**Entrega los datos visuales del enemigo a partir de su hitbox actual.
     * @return Datos de renderizado del enemigo.*/
    @Override
    public RenderData getRenderData() {
        Rectangle bounds = getHitbox().getBounds();
        return new RenderData(bounds.x, bounds.y, bounds.width, bounds.height, getSpriteType());
    }
}
