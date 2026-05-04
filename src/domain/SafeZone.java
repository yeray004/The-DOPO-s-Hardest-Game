package domain;

/**
 * Representa una zona verde segura que puede ser inicial, intermedia o meta.
 * @author Yeray Guacheta
 * @version 1.0
 */
public class SafeZone extends Element {
    private boolean isTarget;

    /**
     * Constructor para crear una zona segura.
     * @param x Posición en el eje X.
     * @param y Posición en el eje Y.
     * @param isTarget Determina si es la zona segura final (meta) para terminar el nivel.
     */
    public SafeZone(int x, int y, String color, boolean isTarget) {
        super(x, y, color);
        this.isTarget = isTarget;
    }

    /**
     * Verifica si esta zona segura es la meta del nivel.
     * @return true si es la zona final, false si es inicial o intermedia.
     */
    public boolean isTarget() {
        return isTarget;
    }
}