// Variaciones Protegidas -> Bajo Acoplamiento - Alta Extensibilidad
package domain;

/**Clase de datos simple (DTO) para enviar información a la vista.
 * @author Yeray Guacheta
 * @version 2.0*/
public class RenderData {
    public int x, y, width, height;
    public String type;

    /**Constructor con dimensiones exactas.
     * @param x Coordenada X de dibujo.
     * @param y Coordenada Y de dibujo.
     * @param width Ancho del objeto.
     * @param height Alto del objeto.
     * @param type Identificador del sprite.*/
    public RenderData(int x, int y, int width, int height, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
    }
}