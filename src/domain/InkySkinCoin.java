package domain;

/**
 * Moneda skin que cambia temporalmente al jugador al estado Inky.
 * @author Yeray Guacheta
 * @version 1.0
 */
public class InkySkinCoin extends Coin {

    /**
     * Crea una moneda azul asociada a Inky.
     * @param x Posicion en el eje X.
     * @param y Posicion en el eje Y.
     */
    public InkySkinCoin(int x, int y) {
        super(x, y, "Blue");
    }

    /**
     * Obtiene el identificador visual de la moneda skin.
     * @return Nombre del sprite de la moneda.
     */
    @Override
    public String getSpriteType() {
        return "BlueCoin";
    }

    /**
     * Aplica la skin Inky al jugador que recoge la moneda.
     * @param player Jugador afectado por la moneda.
     */
    @Override
    protected void applyEffect(Player player) {
        player.changeState(new InkyState());
    }
}
