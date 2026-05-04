package domain;

import java.awt.Point;
import java.util.List;

/**
 * Clase abstracta que define las propiedades básicas de cualquier enemigo en el juego.
 * @author Yeray Guacheta
 * @version 1.0
 */
public abstract class Enemy extends Element {
    protected List<Point> movement;
    protected int speed;

    /**
     * Constructor base para un enemigo.
     * @param x Posición inicial en el eje X.
     * @param y Posición inicial en el eje Y.
     * @param color Color del enemigo.
     * @param speed Velocidad de desplazamiento.
     * @param movement Lista de puntos que definen su ruta.
     */
    public Enemy(int x, int y, String color, int speed, List<Point> movement) {
        super(x, y, color);
        this.speed = speed;
        this.movement = movement;
    }

    /**
     * Actualiza la posición del enemigo según su patrón de movimiento.
     */
    public abstract void updatePosition();
}