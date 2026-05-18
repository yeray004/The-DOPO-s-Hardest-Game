package domain;

/**
 * Interfaz para el patrón Strategy que define el comportamiento del enemigo.
 */
public interface EnemyStrategy {
	/**Actualiza la posición del enemigo.
     * @param enemy Referencia al enemigo a actualizar.
     * @param target Jugador objetivo para estrategias de persecución (puede ser null).*/
    void updatePosition(Enemy enemy, Player target);
    
    /**Retorna el tipo de sprite asociado a este enemigo.
     * @return Nombre del sprite.*/
    String getSpriteType();
    
    /**Obtiene el multiplicador de tamaño del enemigo.
     * @return Multiplicador de tamaño (ej. 1.0).*/
    double getSizeMultiplier();
}