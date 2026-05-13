package domain;
import java.awt.*;
/**
 * Clase abstracta que representa a un jugador controlable dentro del nivel.
 * @author Yeray Guacheta
 * @version 1.0
 */
public abstract class Player extends Element implements Collidable {
    protected int speed;
    protected final int HITBOX = 25;
    protected final int MARGIN = 7; //(40-25)/2 = 7
    protected int collectedCoins = 0;
    //Posición inicial
    protected int startX, startY;

    /**
     * Constructor base para un jugador.
     * @param x Posición inicial en el eje X.
     * @param y Posición inicial en el eje Y.
     * @param color Color del jugador.
     * @param speed Velocidad de movimiento.
     */
    public Player(int x, int y, String color, int speed, int startX, int startY) {
        super(x, y, color);
        this.speed = speed;
        this.startX = startX;
        this.startY = startY;
    }
    
    /** Regresa los valores iniciales del jugador */
    public void reset() {
        x = startX;
        y = startY;
        collectedCoins = 0;
    }
    
    /**
     * Mueve al jugador en el tablero basado en su velocidad.
     * @param dx Dirección en el eje X.
     * @param dy Dirección en el eje Y.
     */
    public void move(int dx, int dy) {
        x += dx * speed;
        y += dy * speed;
    }
    /**Maneja la lógica cuando el jugador es eliminado.*/
    public void die() {
    	System.out.println("Jugador murió");
    }
    /**Lógica para recolectar una moneda y sumar al puntaje.*/
    public void collectCoin() { collectedCoins++; }

    @Override
    public boolean checkCollision(Collidable other) {
        return this.getHitbox().intersects(other.getHitbox().getBounds2D());
    }
    
    @Override
    public Shape getHitbox() {
    	return new Rectangle(x + MARGIN + 2, y + MARGIN + 2, HITBOX - 4, HITBOX - 4); //+2 y -4 para que el hitbox sea un poco menor que el contorno del jugador
    }

    /**
     * Calcula cómo sería el hitbox si el jugador se moviera en una dirección.
     * @param dx Dirección X (-1, 0, 1)
     * @param dy Dirección Y (-1, 0, 1)
     * @return Forma del hitbox en la posición futura.
     */
    public Rectangle getNextHitbox(int dx, int dy) {
        return new Rectangle((x + dx * speed) + MARGIN + 2, 
                             (y + dy * speed) + MARGIN + 2, 
                             HITBOX - 4, HITBOX - 4);
    }
    
    public int getCollectedCoins() { return collectedCoins; }
    
    @Override
    public String getSpriteType() { return "Player"; }
}