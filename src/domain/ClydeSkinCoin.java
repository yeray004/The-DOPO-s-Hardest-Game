package domain;

/**
 * Moneda skin que cambia temporalmente al jugador al estado Clyde.
 * @author Yeray Guacheta
 * @version 1.0
 */
public class ClydeSkinCoin extends Coin {

    /**
     * Crea una moneda verde asociada a Clyde.
     * @param x Posicion en el eje X.
     * @param y Posicion en el eje Y.
     */
    public ClydeSkinCoin(int x, int y) {
        super(x, y, "Green");
    }

    /**
     * Obtiene el identificador visual de la moneda skin.
     * @return Nombre del sprite de la moneda.
     */
    @Override
    public String getSpriteType() {
        return "GreenCoin";
    }

    /**
     * Aplica la skin Clyde al jugador que recoge la moneda.
     * @param player Jugador afectado por la moneda.
     */
    @Override
    protected void applyEffect(Player player) {
        player.changeState(new ClydeState());
    }
}
