package presentation;

import java.awt.image.BufferedImage;

/**
 * Representa la imagen visual de una baldosa del mapa.
 * @author Yeray Guacheta
 * @version 1.0
 */
public class Tile {
    private BufferedImage image;
    
    public Tile(BufferedImage image) {
        this.image = image;
    }
    
    public BufferedImage getImage() { return image; }
}