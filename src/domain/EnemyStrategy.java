package domain;

/**
 * Interfaz para el patrón Strategy que define el comportamiento del enemigo.
 */
public interface EnemyStrategy {
	/**Actualiza la posición del elemento móvil.
     * @param element Referencia al elemento que se va a actualizar.
     * @param target Jugador objetivo para estrategias de persecución (puede ser null).*/
    void updatePosition(MovableElement element, Player target);
    
    /**Retorna el tipo de sprite asociado a este enemigo.
     * @return Nombre del sprite.*/
    String getSpriteType();
    
    /**Obtiene el multiplicador de tamaño del enemigo.
     * @return Multiplicador de tamaño (ej. 1.0).*/
    double getSizeMultiplier();
}
