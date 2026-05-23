package domain;

import java.awt.Point;
import java.util.List;

/**
 * Define la informacion minima que necesita una estrategia para mover un elemento.
 * Permite reutilizar estrategias de movimiento en enemigos u otros elementos dinamicos.
 *
 * @author Yeray Guacheta
 * @version 1.0
 */
public interface MovableElement {

    /**Obtiene la posicion actual en X.
     * @return Coordenada X en pixeles.*/
    int getX();

    /**Obtiene la posicion actual en Y.
     * @return Coordenada Y en pixeles.*/
    int getY();

    /**Actualiza la posicion en X.
     * @param x Nueva coordenada X.*/
    void setX(int x);

    /**Actualiza la posicion en Y.
     * @param y Nueva coordenada Y.*/
    void setY(int y);

    /**Obtiene la velocidad base del elemento movil.
     * @return Velocidad en pixeles por actualizacion.*/
    int getSpeed();

    /**Obtiene la direccion horizontal actual.
     * @return Direccion en X.*/
    int getDx();

    /**Obtiene la direccion vertical actual.
     * @return Direccion en Y.*/
    int getDy();

    /**Cambia la direccion del elemento movil.
     * @param dx Nueva direccion horizontal.
     * @param dy Nueva direccion vertical.*/
    void setDirection(int dx, int dy);

    /**Obtiene la ruta de patrulla del elemento, si aplica.
     * @return Lista de puntos de ruta o null si no usa patrulla.*/
    List<Point> getMovementRoute();
}
