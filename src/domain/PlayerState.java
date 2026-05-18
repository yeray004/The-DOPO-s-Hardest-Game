package domain;

/**
 * Interfaz para el Patrón State que define el comportamiento del jugador según su Skin.
 */
public interface PlayerState {
	/**Obtiene el multiplicador de velocidad del estado actual.
	 * @return Multiplicador de velocidad (ej. 1.0, 1.5).*/
    double getSpeedMultiplier();
    
    /**Obtiene el multiplicador de tamaño del hitbox.
     * @return Multiplicador de tamaño.*/
    double getSizeMultiplier();

    /**Obtiene el nombre del sprite asociado al estado.
     * @return Cadena con el identificador visual.*/
    String getSpriteType();

    /**Maneja la lógica cuando el jugador recibe daño.
     * @param player Referencia al jugador actual.*/
    void handleDamage(Player player);
}