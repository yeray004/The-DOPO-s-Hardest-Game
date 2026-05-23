package domain;

/**
 * Representa el resultado de actualizar un elemento del nivel.
 * Permite que la clase Level tome decisiones sin depender de un tipo concreto de elemento.
 *
 * @author Yeray Guacheta
 * @version 1.0
 */
public enum ElementUpdateResult {
    /** No ocurrio ningun evento importante durante la actualizacion. */
    NONE(false, false),

    /** El nivel debe reiniciarse, pero no se confirmo una muerte. */
    RESET_REQUIRED(true, false),

    /** Un jugador murio y el nivel debe reiniciarse. */
    PLAYER_DIED(true, true);

    private final boolean resetRequired;
    private final boolean playerDied;

    /**
     * Construye un resultado de actualizacion.
     * @param resetRequired Indica si el nivel debe separar o reiniciar sus elementos.
     * @param playerDied Indica si la actualizacion produjo una muerte real.
     */
    ElementUpdateResult(boolean resetRequired, boolean playerDied) {
        this.resetRequired = resetRequired;
        this.playerDied = playerDied;
    }

    /**
     * Indica si el nivel debe reiniciar sus elementos despues de la actualizacion.
     * @return true si se requiere reinicio del nivel, false en caso contrario.
     */
    public boolean requiresReset() {
        return resetRequired;
    }

    /**
     * Indica si un jugador murio durante la actualizacion.
     * @return true si hubo una muerte real, false en caso contrario.
     */
    public boolean hasPlayerDied() {
        return playerDied;
    }
}
