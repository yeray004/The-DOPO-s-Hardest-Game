package domain;

import java.util.Locale;

/**
 * Fabrica de monedas usada para crear objetivos desde archivos de configuracion.
 * Mantiene la carga desacoplada de las clases concretas de moneda.
 *
 * @author Yeray Guacheta
 * @version 1.0
 */
public class CoinFactory {

    /**
     * Crea una moneda concreta segun el tipo indicado.
     * @param x Posicion en el eje X.
     * @param y Posicion en el eje Y.
     * @param type Tipo o color de la moneda.
     * @return Moneda concreta lista para agregarse al nivel.
     * @throws DOPOsHardestGameException si el tipo de moneda no existe.
     */
    public static Coin createCoin(int x, int y, String type) throws DOPOsHardestGameException {
        String normalizedType = normalize(type);
        switch (normalizedType) {
            case "YELLOW":
            case "NORMAL":
                return new YellowCoin(x, y);
            case "RED":
            case "BLINKY":
                return new BlinkySkinCoin(x, y);
            case "BLUE":
            case "INKY":
                return new InkySkinCoin(x, y);
            case "GREEN":
            case "CLYDE":
                return new ClydeSkinCoin(x, y);
            default:
                throw new DOPOsHardestGameException(DOPOsHardestGameException.INVALID_COIN_ERROR + type);
        }
    }

    /**
     * Normaliza el tipo de moneda leido desde configuracion.
     * @param type Texto recibido desde archivo o codigo.
     * @return Texto listo para comparar.
     * @throws DOPOsHardestGameException si el tipo es nulo o vacio.
     */
    private static String normalize(String type) throws DOPOsHardestGameException {
        if (type == null || type.trim().isEmpty()) {
            throw new DOPOsHardestGameException(DOPOsHardestGameException.INVALID_COIN_ERROR + type);
        }
        return type.trim()
                   .replace("_", "")
                   .replace("-", "")
                   .toUpperCase(Locale.ROOT);
    }
}
