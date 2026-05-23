package domain;

import java.util.Locale;

/**
 * Fabrica de elementos especiales usados por la carga de niveles.
 * Centraliza la creacion para mantener abierto el sistema a nuevos poderes.
 *
 * @author Yeray Guacheta
 * @version 1.0
 */
public class SpecialElementFactory {

    /**
     * Crea un elemento especial segun el tipo indicado.
     * @param x Posicion en el eje X.
     * @param y Posicion en el eje Y.
     * @param type Tipo del elemento especial.
     * @return Elemento especial concreto.
     * @throws DOPOsHardestGameException si el tipo no esta soportado.
     */
    public static SpecialElement createElement(int x, int y, String type) throws DOPOsHardestGameException {
        String normalizedType = normalize(type);
        switch (normalizedType) {
            case "LIFESOURCE":
            case "LIFE":
                return new LifeSource(x, y);
            case "BOMB":
                return new Bomb(x, y);
            default:
                throw new DOPOsHardestGameException(DOPOsHardestGameException.INVALID_SPECIAL_ELEMENT_ERROR + type);
        }
    }

    /**
     * Normaliza el texto del elemento especial para compararlo con seguridad.
     * @param type Texto recibido desde archivo.
     * @return Texto normalizado.
     * @throws DOPOsHardestGameException si el texto esta vacio.
     */
    private static String normalize(String type) throws DOPOsHardestGameException {
        if (type == null || type.trim().isEmpty()) {
            throw new DOPOsHardestGameException(DOPOsHardestGameException.INVALID_SPECIAL_ELEMENT_ERROR + type);
        }
        return type.trim()
                   .replace("_", "")
                   .replace("-", "")
                   .toUpperCase(Locale.ROOT);
    }
}
