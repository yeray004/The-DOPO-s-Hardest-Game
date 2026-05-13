package domain;

import java.awt.Point;
import java.util.List;

/**
 * Clase que representa al punto azul básico que se mueve en líneas rectas horizontales o verticales.
 * 
 * @author Yeray Guacheta
 * @version 1.0
 */
public class BasicEnemy extends Enemy {

    /**
     * Constructor para el enemigo básico azul.
     * @param x Posición inicial en el eje X.
     * @param y Posición inicial en el eje Y.
     * @param speed Velocidad constante de desplazamiento.
     * @param movement Ruta lineal predefinida.
     */
    public BasicEnemy(int x, int y, String color, int speed, List<Point> movement, int dx, int dy, int startX, int startY) {
        super(x, y, color, speed, movement, dx, dy, startX, startY);
    }

    // Lógica para iterar sobre la lista movement rebotando en las paredes
    @Override
    public void updatePosition() {
    	x += dx * speed;
        y += dy * speed;
    }
}