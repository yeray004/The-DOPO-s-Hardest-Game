package domain;
import java.awt.Shape;
/**
 * Interfaz que define el comportamiento de los objetos que pueden colisionar en el juego.
 * 
 * @author Yeray Guacheta
 * @version 1.1
 */
public interface Collidable {
	
	/** Devuelve la forma física exacta (cuadrado o círculo) en píxeles. */
    Shape getHitbox();
	
	/**
     * Verifica si este objeto colisiona con otro elemento.
     * @param other El otro elemento a evaluar.
     * @return true si hay colisión, false en caso contrario.
     */
	boolean checkCollision(Collidable other);
}
