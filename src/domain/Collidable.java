package domain;

/**
 * Interfaz que define el comportamiento de los objetos que pueden colisionar en el juego.
 * 
 * @author Yeray Guacheta
 * @version 1.0
 */
public interface Collidable {
	
	/**
     * Verifica si este objeto colisiona con otro elemento.
     * @param other El otro elemento a evaluar.
     * @return true si hay colisión, false en caso contrario.
     */
	boolean checkCollision(Element other);
}
