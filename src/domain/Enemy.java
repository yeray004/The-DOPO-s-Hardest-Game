package domain;

import java.awt.*;
import java.awt.geom.Ellipse2D; // Hitbox Circular
import java.util.List;

/**
 * Clase abstracta que define las propiedades básicas de cualquier enemigo en el juego.
 * @author Yeray Guacheta
 * @version 1.0
 */
public abstract class Enemy extends Element implements Collidable {
    protected List<Point> movement;
    protected int speed;
    protected final int HITBOX = 25;
    protected final int MARGIN = 7;
    protected int dx = 0;
    protected int dy = 1; // 1 = se mueve hacia abajo por defecto
    //Posición inicial
    protected int startX, startY;
    
    /**
     * Constructor base para un enemigo.
     * @param x Posición inicial en el eje X.
     * @param y Posición inicial en el eje Y.
     * @param color Color del enemigo.
     * @param speed Velocidad de desplazamiento.
     * @param movement Lista de puntos que definen su ruta.
     */
    public Enemy(int x, int y, String color, int speed, List<Point> movement, int dx, int dy, int startX, int startY) {
        super(x, y, color);
        this.speed = speed;
        this.movement = movement;
        this.dx = dx;
        this.dy = dy;
        this.startX = x;
        this.startY = y;
    }
    
    /** Regresa los valores iniciales del jugador */
    public void reset() {
        x = startX;
        y = startY;
    }
    
    /** El enemigo se encarga de validar su propia colisión futura */
    public boolean willCollideWith(Wall w) {
        return getNextHitbox().intersects(w.getHitbox().getBounds2D());
    }

    public void reverseDirection() {
        dx *= -1;
        dy *= -1;
    }

    /**}Actualiza la posición del enemigo según su patrón de movimiento.*/
    public abstract void updatePosition();
    
    @Override
    public boolean checkCollision(Collidable other) {
    	return getHitbox().intersects(other.getHitbox().getBounds2D());
    }
    
    public Rectangle getNextHitbox() {
        return new Rectangle((x + dx * speed) + MARGIN, (y + dy * speed) + MARGIN, HITBOX, HITBOX);
    }
    
    @Override
    public Shape getHitbox() {
    	return new Ellipse2D.Double(x + MARGIN, y + MARGIN, HITBOX, HITBOX );
    }
    
    @Override
    public String getSpriteType() { return "Enemy"; }

}