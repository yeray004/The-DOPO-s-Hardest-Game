package domain;

import java.awt.*;

/**
 * Representa una pared sólida estática que bloquea el paso de jugadores y enemigos.
 * @author Yeray Guacheta
 * @version 1.1
 */
public class Wall extends Element implements Collidable {

    /**
     * Constructor para crear una pared en el mapa.
     * @param x Posición en el eje X.
     * @param y Posición en el eje Y.
     */
    public Wall(int x, int y) {
        super(x, y); 
    }
    public void reset() {};
    
    @Override
    public Shape getHitbox() {
        return new Rectangle(x * 40, y * 40, 40, 40); 
    }

    @Override
    public boolean checkCollision(Collidable other) {
        return this.getHitbox().intersects(other.getHitbox().getBounds2D());
    }
    
    @Override
    public String getSpriteType() { return "Wall"; }
}
