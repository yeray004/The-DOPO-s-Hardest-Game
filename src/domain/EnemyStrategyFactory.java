package domain;

/**Fábrica para instanciar estrategias de enemigos polimórficamente.
 * @author Yeray Guacheta
 * @version 1.0*/
public class EnemyStrategyFactory {

    /**Devuelve la estrategia correspondiente al tipo especificado.
     * @param type Identificador de la estrategia en texto.
     * @return Instancia concreta de EnemyStrategy.*/
    public static EnemyStrategy getStrategy(String type) {
        switch (type) {
            case "ORANGE": return new OrangeEnemyStrategy();
            case "PINK": return new PinkEnemyStrategy();
            case "RED": return new RedEnemyStrategy();
            default: return new BasicEnemyStrategy();
        }
    }
}