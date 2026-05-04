package domain;

/**
 * Representa una pared sólida estática que bloquea el paso de jugadores y enemigos.
 * @author Yeray Guacheta
 * @version 1.0
 */
public class Wall extends Element {

    /**
     * Constructor para crear una pared en el mapa.
     * @param x Posición en el eje X.
     * @param y Posición en el eje Y.
     */
    public Wall(int x, int y, String color) {
        super(x, y, color); 
    }
}