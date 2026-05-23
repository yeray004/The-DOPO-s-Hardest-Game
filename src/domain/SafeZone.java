package domain;

import java.awt.*;

/**
 * Representa una zona verde segura que puede ser inicial, intermedia o meta.
 * @author Yeray Guacheta
 * @version 1.2
 */
public class SafeZone extends Element implements Collidable {
    private boolean isTarget;
    private boolean isCheckpoint;

    /**
     * Constructor para crear una zona segura.
     * @param x Posición en el eje X.
     * @param y Posición en el eje Y.
     * @param isTarget Determina si es la zona segura final (meta) para terminar el nivel.
     */
    public SafeZone(int x, int y, boolean isTarget) {
        this(x, y, isTarget, false);
    }

    /**
     * Constructor para crear una zona segura clasificando si es checkpoint.
     * @param x Posición en el eje X.
     * @param y Posición en el eje Y.
     * @param isTarget Determina si es la zona segura final.
     * @param isCheckpoint Determina si actualiza el punto de reaparicion.
     */
    public SafeZone(int x, int y, boolean isTarget, boolean isCheckpoint) {
        super(x, y);
        this.isTarget = isTarget;
        this.isCheckpoint = isCheckpoint;
    }
    
    public void reset() {};

    /**
     * Verifica si esta zona segura es la meta del nivel.
     * @return true si es la zona final, false si es inicial o intermedia.
     */
    public boolean isTarget() { return isTarget; }

    /**
     * Verifica si esta zona segura funciona como punto intermedio de reaparicion.
     * @return true si es una zona segura intermedia.
     */
    public boolean isCheckpoint() { return isCheckpoint; }

    /**
     * Verifica si esta zona corresponde a una zona inicial o segura normal.
     * @return true si no es meta ni checkpoint.
     */
    public boolean isInitialZone() { return !isTarget && !isCheckpoint; }

    /**
     * Obtiene la posicion X en pixeles para reubicar al jugador.
     * @return Coordenada X en pixeles.
     */
    public int getSpawnX() { return x * 40; }

    /**
     * Obtiene la posicion Y en pixeles para reubicar al jugador.
     * @return Coordenada Y en pixeles.
     */
    public int getSpawnY() { return y * 40; }
    
    @Override
    public boolean checkCollision(Collidable other) {
    	return this.getHitbox().intersects(other.getHitbox().getBounds2D());
    }
    
    @Override
    public Shape getHitbox() {
        return new Rectangle(x * 40, y * 40, 40, 40); 
    }
    
    @Override
    public String getSpriteType() { return isCheckpoint ? "CheckpointZone" : "SafeZone"; }
}
