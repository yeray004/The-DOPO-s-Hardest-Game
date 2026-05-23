package domain;

/**
 * Fuente de vida que otorga una vida extra al jugador y luego desaparece.
 * @author Yeray Guacheta
 * @version 1.1
 */
public class LifeSource extends SpecialElement {

    /**
     * Crea una fuente de vida en el tablero.
     * @param x Posicion en el eje X.
     * @param y Posicion en el eje Y.
     */
    public LifeSource(int x, int y) {
        super(x, y);
    }

    /**
     * Da una vida extra al jugador y consume la fuente.
     * @param player Jugador que interactua con la fuente.
     * @param level Nivel donde ocurre la interaccion.
     * @return Resultado sin muerte ni reinicio.
     */
    @Override
    public ElementUpdateResult interactWithPlayer(Player player, Level level) {
        if (!isActive() || player == null) return ElementUpdateResult.NONE;
        player.addExtraLife();
        setActive(false);
        return ElementUpdateResult.NONE;
    }

    /**
     * Los enemigos no reciben efecto de la fuente de vida.
     * @param enemy Enemigo que toca la fuente.
     * @param level Nivel donde ocurre la interaccion.
     * @return Resultado sin cambios.
     */
    @Override
    public ElementUpdateResult interactWithEnemy(Enemy enemy, Level level) {
        return ElementUpdateResult.NONE;
    }

    /**Obtiene el identificador visual de la fuente de vida.
     * @return Nombre del sprite.*/
    @Override
    public String getSpriteType() {
        return "LifeSource";
    }
}
