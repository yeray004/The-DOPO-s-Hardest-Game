package domain;

import java.awt.Rectangle;
import java.awt.Shape;

/**
 * Clase base para elementos especiales estaticos con efectos sobre jugadores o enemigos.
 * Nuevos poderes pueden extender esta clase sin modificar la logica principal del nivel.
 *
 * @author Yeray Guacheta
 * @version 1.1
 */
public abstract class SpecialElement extends Element implements Collidable {
    private boolean active;

    /**
     * Crea un elemento especial en coordenadas de pixeles.
     * @param x Posicion en el eje X.
     * @param y Posicion en el eje Y.
     */
    public SpecialElement(int x, int y) {
        super(x, y);
        this.active = true;
    }

    /**Devuelve el elemento especial a su estado despues de una muerte.
     * Por defecto conserva si ya fue consumido para que no reaparezca inmediatamente.*/
    @Override
    public void reset() {
        // Conserva el estado activo actual durante el reinicio del intento.
    }

    /**Reactiva el elemento cuando la configuracion del nivel inicia desde cero.*/
    public void reactivate() {
        active = true;
    }

    /**Indica si el elemento especial debe mostrarse.
     * @return true si no ha sido consumido.*/
    @Override
    public boolean isVisible() {
        return active;
    }

    /**Establece el estado activo del elemento especial.
     * @param active Nuevo estado de actividad.*/
    public void setActive(boolean active) {
        this.active = active;
    }

    /**Verifica si el elemento sigue disponible.
     * @return true si puede aplicar su efecto.*/
    public boolean isActive() {
        return active;
    }

    /**Obtiene el tamaño base del elemento especial.
     * @return Tamaño base en pixeles antes de aplicar multiplicador.*/
    protected int getBaseHitboxSize() {
        return 25;
    }

    /**Obtiene el multiplicador de tamaño del elemento especial.
     * @return Multiplicador de tamaño actual.*/
    protected double getSizeMultiplier() {
        return 1.0;
    }

    /**Obtiene el tamaño de la celda donde se centra el elemento.
     * @return Tamaño de celda en pixeles.*/
    protected int getTileSize() {
        return 40;
    }

    /**Obtiene el espacio interno usado para suavizar la colision.
     * @return Margen interno en pixeles.*/
    protected int getCollisionPadding() {
        return 2;
    }

    /**Calcula el tamaño actual del hitbox usando el multiplicador.
     * @return Tamaño del hitbox en pixeles.*/
    public int getCurrentHitboxSize() {
        return (int) (getBaseHitboxSize() * getSizeMultiplier());
    }

    /**Calcula el margen para centrar el sprite en la celda.
     * @return Margen en pixeles.*/
    public int getMargin() {
        return (getTileSize() - getCurrentHitboxSize()) / 2;
    }

    /**
     * Aplica el efecto del elemento cuando un jugador lo toca.
     * @param player Jugador que interactua con el elemento.
     * @param level Nivel donde ocurre la interaccion.
     * @return Resultado de la interaccion.
     */
    public abstract ElementUpdateResult interactWithPlayer(Player player, Level level);

    /**
     * Aplica el efecto del elemento cuando un enemigo lo toca.
     * @param enemy Enemigo que interactua con el elemento.
     * @param level Nivel donde ocurre la interaccion.
     * @return Resultado de la interaccion.
     */
    public abstract ElementUpdateResult interactWithEnemy(Enemy enemy, Level level);

    /**Verifica colision con otro objeto del juego.
     * @param other Objeto a evaluar.
     * @return true si hay contacto y el elemento sigue activo.*/
    @Override
    public boolean checkCollision(Collidable other) {
        return active && getHitbox().intersects(other.getHitbox().getBounds2D());
    }

    /**Obtiene el area de colision del elemento especial.
     * @return Rectangulo centrado con el mismo criterio usado por el jugador.*/
    @Override
    public Shape getHitbox() {
        int currentHitbox = getCurrentHitboxSize();
        int margin = getMargin();
        int padding = getCollisionPadding();
        return new Rectangle(x + margin + padding,
                y + margin + padding,
                currentHitbox - padding * 2,
                currentHitbox - padding * 2);
    }
}
