package domain;

/**
 * Clase que representa al jugador estándar (cuadrado rojo) sin habilidades especiales.
 * 
 * @author Yeray Guahceta
 * @version 1.0
 */
public class Blinky extends Player {
	
	
	/**
     * Constructor para inicializar a Blinky con sus atributos por defecto.
     * @param x Posición inicial en el eje X.
     * @param y Posición inicial en el eje Y.
     */
    public Blinky(int x, int y, String color) {
        // Velocidad 1x y color rojo por defecto según el PDF
        super(x, y, color, 1); 
    }
}