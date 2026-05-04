package domain;

/**
 * Clase abstracta que representa a un jugador controlable dentro del nivel.
 * @author Yeray Guacheta
 * @version 1.0
 */
public abstract class Player extends Element {
    protected int speed;

    /**
     * Constructor base para un jugador.
     * @param x Posición inicial en el eje X.
     * @param y Posición inicial en el eje Y.
     * @param color Color del jugador.
     * @param speed Velocidad de movimiento.
     */
    public Player(int x, int y, String color, int speed) {
        super(x, y, color);
        this.speed = speed;
    }
    /**
     * Mueve al jugador en el tablero basado en su velocidad.
     * @param dx Dirección en el eje X.
     * @param dy Dirección en el eje Y.
     */
    public void move(int dx, int dy) {
        x += dx * speed;
        y += dy * speed;
    }
    /**
     * Maneja la lógica cuando el jugador es eliminado.
     */
    public void die() {
        
    }
    /**
     * Lógica para recolectar una moneda y sumar al puntaje.
     */
    public void collectCoin() {
        
    }
}