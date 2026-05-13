package domain;

import java.awt.*;
import java.awt.geom.Ellipse2D;
/**
 * Representa una moneda amarilla que el jugador debe recolectar para completar el nivel.
 * 
 * @author Yeray Guacheta
 * @version 1.0
 */
public class Coin extends Element implements Collidable{
    private boolean isCollected;
    protected final int HITBOX = 25;
    protected final int MARGIN = 7;

    /**
     * Constructor para una moneda estándar.
     * @param x Posición en el eje X.
     * @param y Posición en el eje Y.
     */
    public Coin(int x, int y, String color) {
        super(x, y, color);
        this.isCollected = false;
    }
     
    public void reset() {
        isCollected = false;
    }

    @Override
    public boolean isVisible() { 
    	return !isCollected; 
    }

    /**
     * Verifica si la moneda ya fue recogida por un jugador.
     * @return true si fue recogida, false si sigue en el tablero.
     */
    public boolean isCollected() {
        return isCollected;
    }

    /** Marca la moneda como recogida. */
    public void collect() {
        isCollected = true;
    }

    @Override
    public boolean checkCollision(Collidable other) {
        if (isCollected) return false;
        return this.getHitbox().intersects(other.getHitbox().getBounds2D());
    }
    
    @Override
    public Shape getHitbox() {
    	return new Ellipse2D.Double(x + MARGIN, y + MARGIN, HITBOX, HITBOX);
    }
    
    @Override
    public String getSpriteType() { return "Coin"; }
}