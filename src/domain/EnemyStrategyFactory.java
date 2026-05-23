package domain;

import java.util.Locale;

/**Fabrica para instanciar estrategias de enemigos polimorficamente.
 * @author Yeray Guacheta
 * @version 1.3*/
public class EnemyStrategyFactory {

    /**Devuelve la estrategia correspondiente al tipo especificado.
     * @param type Identificador de la estrategia en texto.
     * @return Instancia concreta de EnemyStrategy.
     * @throws DOPOsHardestGameException si el tipo solicitado no corresponde a una estrategia registrada.*/
    public static EnemyStrategy getStrategy(String type) throws DOPOsHardestGameException {
        String normalizedType = normalize(type);
        switch (normalizedType) {
            case "LINEAR":
                return new LinearEnemyStrategy();
            case "PATROL":
                return new PatrolEnemyStrategy();
            case "SEARCH":
                return new SearchEnemyStrategy();
            case "AMBUSH":
                return new AmbushEnemyStrategy();
            default:
                throw new DOPOsHardestGameException(DOPOsHardestGameException.INVALID_STRATEGY_ERROR + type);
        }
    }

    /**
     * Normaliza el texto para comparar estrategias sin depender de mayusculas o separadores.
     * @param type Texto recibido desde configuracion o codigo.
     * @return Texto normalizado para busqueda.
     * @throws DOPOsHardestGameException si el texto esta vacio o es nulo.
     */
    private static String normalize(String type) throws DOPOsHardestGameException {
        if (type == null || type.trim().isEmpty()) {
            throw new DOPOsHardestGameException(DOPOsHardestGameException.EMPTY_STRATEGY_ERROR);
        }
        return type.trim()
                   .replace("_", "")
                   .replace("-", "")
                   .toUpperCase(Locale.ROOT);
    }
}
