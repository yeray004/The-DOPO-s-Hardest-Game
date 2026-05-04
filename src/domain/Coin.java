package domain;

/**
 * Representa una moneda amarilla que el jugador debe recolectar para completar el nivel.
 * 
 * @author Yeray Guacheta
 * @version 1.0
 */
public class Coin extends Element {
    private boolean isCollected;

    /**
     * Constructor para una moneda estándar.
     * @param x Posición en el eje X.
     * @param y Posición en el eje Y.
     */
    public Coin(int x, int y, String color) {
        super(x, y, color);
        this.isCollected = false;
    }

    /**
     * Verifica si la moneda ya fue recogida por un jugador.
     * @return true si fue recogida, false si sigue en el tablero.
     */
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Marca la moneda como recogida.
     */
    public void collect() {
        isCollected = true;
    }

    @Override
    public boolean checkCollision(Element other) {
        boolean res = false;
        if (!isCollected && x==other.getX() && y==other.getY()) {
            res = true;
        }
        return res;
    }
}