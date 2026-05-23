package domain;

/**
 * Moneda skin que cambia temporalmente al jugador al estado Blinky.
 * @author Yeray Guacheta
 * @version 1.0
 */
public class BlinkySkinCoin extends Coin {

    /**
     * Crea una moneda roja asociada a Blinky.
     * @param x Posicion en el eje X.
     * @param y Posicion en el eje Y.
     */
    public BlinkySkinCoin(int x, int y) {
        super(x, y, "Red");
    }

    /**
     * Obtiene el identificador visual de la moneda skin.
     * @return Nombre del sprite de la moneda.
     */
    @Override
    public String getSpriteType() {
        return "RedCoin";
    }

    /**
     * Aplica la skin Blinky al jugador que recoge la moneda.
     * @param player Jugador afectado por la moneda.
     */
    @Override
    protected void applyEffect(Player player) {
        player.changeState(new BlinkyState());
    }
}
