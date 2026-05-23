package domain;

/**
 * Representa el resultado de aplicar dano a un jugador.
 * Permite diferenciar inmunidad, dano absorbido y muerte real.
 *
 * @author Yeray Guacheta
 * @support Assisted by Gemini (Google AI) - May 2026
 * @version 1.0
 */
public enum DamageResult {
    /**El jugador no recibio dano porque estaba protegido temporalmente.*/
    NO_DAMAGE,

    /**El jugador recibio dano, pero no murio por proteccion o vida extra.*/
    NON_LETHAL,

    /**El jugador recibio dano y murio.*/
    LETHAL;

    /**
     * Indica si el dano produjo una muerte real.
     * @return true si el resultado corresponde a muerte.
     */
    public boolean isLethal() {
        return this == LETHAL;
    }

    /**
     * Indica si el dano fue aplicado de alguna manera.
     * @return true si el jugador perdio proteccion, vida extra o murio.
     */
    public boolean wasApplied() {
        return this != NO_DAMAGE;
    }
}
