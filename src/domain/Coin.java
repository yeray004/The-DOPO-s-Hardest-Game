package domain;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**Representa una moneda que puede otorgar puntos o cambiar el estado (Skin) del jugador.
 * @author Yeray Guacheta
 * @version 2.0*/
public class Coin extends Element implements Collidable {
    private boolean isCollected;
    private final int BASE_HITBOX = 25;

    /**Constructor para una moneda.
     * @param x Posición en el eje X.
     * @param y Posición en el eje Y.
     * @param color Color/Tipo de la moneda (Yellow, Blue, Green).*/
    public Coin(int x, int y, String color) {
        super(x, y, color);
        this.isCollected = false;
    }
     
    /**Reinicia la moneda a su estado no recolectado en el tablero.*/
    @Override
    public void reset() {
        isCollected = false;
    }

    /**Indica si la moneda debe dibujarse en pantalla.
     * @return true si no ha sido recogida.*/
    @Override
    public boolean isVisible() { 
        return !isCollected; 
    }

    /**Verifica si la moneda ya fue recogida por un jugador.
     * @return true si fue recogida, false si sigue activa.*/
    public boolean isCollected() {
        return isCollected;
    }

    /**Marca la moneda como recogida y la oculta del tablero.*/
    public void collect() {
        isCollected = true;
    }

    /**Verifica la colisión geométrica si la moneda sigue activa.
     * @param other Objeto con el cual evaluar el contacto.
     * @return true si hay colisión, false si no la hay o ya fue recogida.*/
    @Override
    public boolean checkCollision(Collidable other) {
        if (isCollected) return false;
        return this.getHitbox().intersects(other.getHitbox().getBounds2D());
    }

    /**Calcula el margen dinámico para centrar la moneda en su celda.
     * @return Píxeles de margen.*/
    public int getMargin() {
        return (40 - BASE_HITBOX) / 2;
    }

    /**Obtiene la forma geométrica exacta de la moneda.
     * @return Elipse que representa el hitbox ajustado.*/
    @Override
    public Shape getHitbox() {
        int margin = getMargin();
        return new Ellipse2D.Double(x + margin, y + margin, BASE_HITBOX, BASE_HITBOX);
    }

    /**Obtiene el identificador visual para el renderizado.
     * @return String con el color/tipo de moneda.*/
    @Override
    public String getSpriteType() {
        return color; // Retorna "Yellow", "Blue" o "Green"
    }
}