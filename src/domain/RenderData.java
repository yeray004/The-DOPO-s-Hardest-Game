package domain;

/**
 * Clase de datos simple (DTO) para enviar información a la vista.
 * No contiene lógica ni dependencias gráficas.
 */
// Variaciones Protegidas -> Bajo Acoplamiento - Alta Extensibilidad
public class RenderData {
    public int x, y;
    public String type;

    public RenderData(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}