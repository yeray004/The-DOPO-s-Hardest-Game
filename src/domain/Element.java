package domain;

/**
 * Clase abstracta que representa cualquier elemento estático o dinámico dentro del tablero de juego.
 * 
 * @author Yeray Guacheta
 * @version 1.0
 */
public abstract class Element implements Collidable {
    protected int x;
    protected int y;
    protected String color;

    /**
     * Constructor general para un elemento del juego.
     * @param x Posición en el eje X.
     * @param y Posición en el eje Y.
     * @param color Color representativo del elemento.
     */
    public Element(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }
    /**
     * Engrega la coordenada x del objeto.
     * @return Posición en coordenada x.
     */
    public int getX() {
        return x;
    }
    /**
     * Engrega la coordenada y del objeto.
     * @return Posición en coordenada y.
     */
    public int getY() {
        return y;
    }
    /**
     * Engrega el color del objeto.
     * @return Color del objeto.
     */
    public String getColor() {
        return color;
    }
    /**
     * Verifica por defecto si este elemento comparte la misma coordenada exacta que otro.
     * Puede ser sobreescrito si un elemento necesita una colisión distinta.
     */
    @Override
    public boolean checkCollision(Element other) {
        boolean res = false;
    	if(x==other.getX() && y==other.getY()) {
        	res = true;
        }
        return res;
    }
}