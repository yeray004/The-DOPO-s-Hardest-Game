package domain;

/**
 * Moneda normal que solo aumenta el contador de objetivos recolectados.
 * @author Yeray Guacheta
 * @version 1.0
 */
public class YellowCoin extends Coin {

    /**
     * Crea una moneda amarilla normal.
     * @param x Posicion en el eje X.
     * @param y Posicion en el eje Y.
     */
    public YellowCoin(int x, int y) {
        super(x, y, "Yellow");
    }
}
