package domain;

/**
 * Bomba estatica que aplica dano al jugador al contacto.
 * @author Yeray Guacheta
 * @version 1.2
 */
public class Bomb extends SpecialElement {

    /**
     * Crea una bomba en el tablero.
     * @param x Posicion en el eje X.
     * @param y Posicion en el eje Y.
     */
    public Bomb(int x, int y) {
        super(x, y);
    }

    /**
     * Aplica dano al jugador usando las mismas reglas que una colision con enemigo.
     * @param player Jugador que interactua con la bomba.
     * @param level Nivel donde ocurre la interaccion.
     * @return Resultado indicando si el jugador murio o solo recibio dano.
     */
    @Override
    public ElementUpdateResult interactWithPlayer(Player player, Level level) {
        if (!isActive() || player == null) return ElementUpdateResult.NONE;
        DamageResult damageResult = player.receiveDamage();
        if (!damageResult.wasApplied()) return ElementUpdateResult.NONE;

        setActive(false);
        if (damageResult.isLethal()) {
            level.registerDeath();
            return ElementUpdateResult.PLAYER_DIED;
        }
        return ElementUpdateResult.NONE;
    }

    /**
     * Los enemigos no activan ni consumen la bomba en esta version.
     * @param enemy Enemigo que interactua con la bomba.
     * @param level Nivel donde ocurre la interaccion.
     * @return Resultado sin cambios.
     */
    @Override
    public ElementUpdateResult interactWithEnemy(Enemy enemy, Level level) {
        return ElementUpdateResult.NONE;
    }

    /**Obtiene el identificador visual de la bomba.
     * @return Nombre del sprite.*/
    @Override
    public String getSpriteType() {
        return "Bomb";
    }
}
