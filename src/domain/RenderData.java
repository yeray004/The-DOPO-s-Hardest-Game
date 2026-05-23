// Variaciones Protegidas -> Bajo Acoplamiento - Alta Extensibilidad
package domain;

/**Clase de datos simple (DTO) para enviar información a la vista.
 * @author Yeray Guacheta
 * @version 2.1*/
public class RenderData {
    public int x, y, width, height;
    public String type;
    public String borderType;

    /**Constructor con dimensiones exactas.
     * @param x Coordenada X de dibujo.
     * @param y Coordenada Y de dibujo.
     * @param width Ancho del objeto.
     * @param height Alto del objeto.
     * @param type Identificador del sprite.*/
    public RenderData(int x, int y, int width, int height, String type) {
        this(x, y, width, height, type, null);
    }

    /**Constructor con dimensiones exactas y borde opcional.
     * @param x Coordenada X de dibujo.
     * @param y Coordenada Y de dibujo.
     * @param width Ancho del objeto.
     * @param height Alto del objeto.
     * @param type Identificador del sprite.
     * @param borderType Identificador del color de borde.*/
    public RenderData(int x, int y, int width, int height, String type, String borderType) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.borderType = borderType;
    }
}
