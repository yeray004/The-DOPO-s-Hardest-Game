package domain;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.List;

/**Representa un enemigo dentro del juego manejado por una estrategia de movimiento.
 * @author Yeray Guacheta
 * @version 2.0
 */
public class Enemy extends Element implements Collidable {
    protected List<Point> movement;
    protected int speed;
    private final int BASE_HITBOX = 25;
    protected int dx = 0;
    protected int dy = 1;
    protected int startX, startY;
    private EnemyStrategy strategy;

    /**Constructor para un enemigo basado en estrategias.
     * @param x Posición inicial X.
     * @param y Posición inicial Y.
     * @param color Color base.
     * @param speed Velocidad de desplazamiento.
     * @param movement Lista de puntos de patrulla.
     * @param dx Dirección inicial X.
     * @param dy Dirección inicial Y.
     * @param strategy Estrategia de movimiento.*/
    public Enemy(int x, int y, String color, int speed, List<Point> movement, int dx, int dy, EnemyStrategy strategy) {
        super(x, y, color);
        this.speed = speed;
        this.movement = movement;
        this.dx = dx;
        this.dy = dy;
        this.startX = x;
        this.startY = y;
        this.strategy = strategy;
    }

    /**Regresa el enemigo a su posición inicial.*/
    @Override
    public void reset() {
        x = startX;
        y = startY;
    }

    /**Valida si el próximo movimiento interseca con un muro.
     * @param w Muro a evaluar.
     * @return true si colisionará, false en caso contrario.*/
    public boolean willCollideWith(Wall w) {
        return getNextHitbox().intersects(w.getHitbox().getBounds2D());
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

    /**Verifica la colisión actual con otro objeto colisionable.
     * @param other Objeto con el cual evaluar.
     * @return true si hay colisión, false en caso contrario.*/
    @Override
    public boolean checkCollision(Collidable other) {
        return getHitbox().intersects(other.getHitbox().getBounds2D());
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
}