package domain;
import java.awt.*;
/**
 * Clase abstracta que representa a un jugador controlable dentro del nivel.
 * @author Yeray Guacheta
 * @version 1.0
 */
public class Player extends Element implements Collidable {
    private PlayerState state;
    private final int BASE_SPEED = 4; // Velocidad 1x según requerimientos
    private final int BASE_HITBOX = 25;
    
    protected int collectedCoins = 0;
    protected int startX, startY;
    
    private String initialSkin = "Blinky"; // Guarda el nombre en texto, no el objeto
    private boolean isDead = false; // Bandera de control
    
    public Player(int x, int y) {
        super(x, y, "Red"); // El color base, aunque el sprite lo define el estado
        this.startX = x;
        this.startY = y;
        this.state = new BlinkyState(); // por defecto
    }
    
    /**Establece y guarda el nombre de la skin elegida en el menú.
     * @param skin Nombre de la skin (ej. "Clyde").*/
    public void setInitialSkin(String skin) {
        this.initialSkin = skin;
        applyInitialSkin();
    }
    
    /**Aplica una nueva instancia fresca del estado inicial guardado.*/
    private void applyInitialSkin() {
        switch (initialSkin) {
            case "Inky": changeState(new InkyState()); break;
            case "Clyde": changeState(new ClydeState()); break;
            default: changeState(new BlinkyState()); break;
        }
    }
    
    /**Cambia el estado (Skin) actual del jugador.
     * @param newState Nuevo estado a asignar.*/
    public void changeState(PlayerState newState) {
        this.state = newState;
    }

    // --- CÁLCULOS DINÁMICOS BASADOS EN EL ESTADO ---
    /**Calcula la velocidad actual basándose en el estado.
     * @return Velocidad dinámica del jugador.*/
    public int getSpeed() {
        return (int) (BASE_SPEED * state.getSpeedMultiplier());
    }

    /**Calcula el tamaño actual del hitbox basándose en el estado.
     * @return Tamaño del hitbox en píxeles.*/
    public int getCurrentHitboxSize() {
        return (int) (BASE_HITBOX * state.getSizeMultiplier());
    }

    /**Calcula el margen para centrar el hitbox dinámicamente.
     * @return Píxeles de margen.*/
    public int getMargin() {
        // Asumiendo que el tile base mide 40x40. Centra el hitbox dinámicamente.
        return (40 - getCurrentHitboxSize()) / 2; 
    }

    // --- ACCIONES ---
    /**Delega la acción de recibir daño y verifica si resultó en muerte.
     * @return true si el jugador murió, false si el daño fue absorbido.*/
    public boolean hitByEnemy() {
        isDead = false;
        state.handleDamage(this);
        return isDead;
    }
    
    /**Maneja la lógica de muerte y restaura el estado original.*/
    public void die() {
        System.out.println("Jugador murió");
        // Aquí la fachada o nivel se encargará de reubicarlo.
        // Al morir, debe recuperar su skin original[cite: 45].
        isDead = true;
    }
    
    /**Regresa los valores iniciales y limpia el estado del jugador únicamente si falleció.*/
    @Override
    public void reset() {
        this.x = startX;
        this.y = startY;
        
        // Si la bandera de muerte real está activa, se penaliza y limpia la skin
        if (isDead) {
            this.collectedCoins = 0;
            applyInitialSkin(); // Vuelve a su estado original
            this.isDead = false;
        }
    }

    /**Mueve al jugador en el tablero basado en su velocidad actual.
     * @param dx Dirección en el eje X.
     * @param dy Dirección en el eje Y.*/
    public void move(int dx, int dy) {
        x += dx * getSpeed();
        y += dy * getSpeed();
    }
    
    /**Lógica para recolectar una moneda y sumar al puntaje.*/
    public void collectCoin() { collectedCoins++; }
    
    /**Obtiene la cantidad de monedas recolectadas.
     * @return Número de monedas.*/
    public int getCollectedCoins() { return collectedCoins; }

    // --- COLISIONES DINÁMICAS ---
    
    /**Verifica si el jugador colisiona con otro objeto.
     * @param other Objeto con el cual evaluar colisión.
     * @return true si hay colisión, false en caso contrario.*/
    @Override
    public boolean checkCollision(Collidable other) {
        return this.getHitbox().intersects(other.getHitbox().getBounds2D());
    }
    
    /**Obtiene el hitbox actual ajustado al margen y tamaño del estado.
     * @return Forma geométrica del hitbox.*/
    @Override
    public Shape getHitbox() {
        int currentHitbox = getCurrentHitboxSize();
        int margin = getMargin();
        return new Rectangle(x + margin + 2, y + margin + 2, currentHitbox - 4, currentHitbox - 4);
    }

    /**Calcula cómo sería el hitbox si el jugador se moviera en una dirección.
     * @param dx Dirección X (-1, 0, 1).
     * @param dy Dirección Y (-1, 0, 1).
     * @return Forma del hitbox en la posición futura.*/
    public Rectangle getNextHitbox(int dx, int dy) {
        int currentHitbox = getCurrentHitboxSize();
        int margin = getMargin();
        int speed = getSpeed();
        return new Rectangle((x + dx * speed) + margin + 2, 
                             (y + dy * speed) + margin + 2, 
                             currentHitbox - 4, currentHitbox - 4);
    }
    
    /**Obtiene el tipo de sprite delegando al estado actual.
     * @return Nombre del sprite.*/
    @Override
    public String getSpriteType() { 
        return state.getSpriteType(); // El render ahora sabe exactamente qué imagen pedir
    }
}