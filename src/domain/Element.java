package domain;

/**
 * Clase abstracta que representa cualquier elemento estático o dinámico dentro del tablero de juego.
 * 
 * @author Yeray Guacheta
 * @version 1.1
 */
public abstract class Element{
    protected int x;
    protected int y;

    /**
     * Constructor general para un elemento del juego.
     * @param x Posición en el eje X.
     * @param y Posición en el eje Y.
     */
    public Element(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /** Establece una posición en x. */
    public void setX(int x) { this.x = x; }
    
    /** Establece una posición en y. */
    public void setY(int y) { this.y = y; }
    
    /** Devuelve el elemento a su estado y posición original. */
    public abstract void reset();
    
    /** Establrece si un elemento es o no visible. */
    public boolean isVisible() { return true; }
    
    /**
     * Actualiza el comportamiento del elemento dentro del nivel.
     * Por defecto no hace nada para que los elementos estaticos no dependan de logica adicional.
     * @param level Nivel al que pertenece el elemento.
     * @return Resultado de la actualizacion del elemento.
     */
    public ElementUpdateResult updateElement(Level level) {
        return ElementUpdateResult.NONE;
    }
    
    /**Engrega la coordenada x del objeto.
     * @return Posición en coordenada x.*/
    public int getX() { return x; }
    
    /**Engrega la coordenada y del objeto.
     * @return Posición en coordenada y.*/
    public int getY() { return y; }
    
    /**Entrega el tipo de elemento.
     * @return Tipo de identidad*/
    public abstract String getSpriteType();
}
