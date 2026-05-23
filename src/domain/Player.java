package domain;
import java.awt.*;
/**
 * Clase abstracta que representa a un jugador controlable dentro del nivel.
 * @author Yeray Guacheta
 * @version 1.3
 */
public class Player extends Element implements Collidable {
    private PlayerState state;
    private final int BASE_SPEED = 4; // Velocidad 1x según requerimientos
    private final int BASE_HITBOX = 25;
    
    protected int collectedCoins = 0;
    protected int startX, startY;
    private int initialX;
    private int initialY;
    
    private String initialSkin = "Blinky"; // Guarda el nombre en texto, no el objeto
    private boolean isDead = false; // Bandera de control
    private int extraLives = 0;
    private int damageCooldownTicks = 0;
    private static final int DAMAGE_COOLDOWN_DURATION = 8; // Evita doble daño inmediato
    private String borderColorName = "Black";
    private boolean invertedGoal = false;
    
    public Player(int x, int y) {
        super(x, y);
        this.startX = x;
        this.startY = y;
        this.initialX = x;
        this.initialY = y;
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
        return receiveDamage().isLethal();
    }

    /**
     * Aplica dano al jugador usando las mismas reglas para enemigos y bombas.
     * @return Resultado detallado del dano recibido.
     */
    public DamageResult receiveDamage() {
        if (isDamageCooldownActive()) {
            return DamageResult.NO_DAMAGE;
        }
        if (extraLives > 0) {
            extraLives--;
            startDamageCooldown();
            return DamageResult.NON_LETHAL;
        }
        isDead = false;
        state.handleDamage(this);
        if (!isDead) {
            startDamageCooldown();
            return DamageResult.NON_LETHAL;
        }
        return DamageResult.LETHAL;
    }

    /**Actualiza estados temporales del jugador como la inmunidad breve.
     * @param level Nivel al que pertenece el jugador.
     * @return Resultado sin eventos para el nivel.*/
    @Override
    public ElementUpdateResult updateElement(Level level) {
        if (damageCooldownTicks > 0) {
            damageCooldownTicks--;
        }
        return ElementUpdateResult.NONE;
    }

    /**Inicia una ventana corta donde el jugador no recibe otro golpe inmediato.*/
    private void startDamageCooldown() {
        damageCooldownTicks = DAMAGE_COOLDOWN_DURATION;
    }

    /**Indica si el jugador esta temporalmente protegido despues de recibir daño.
     * @return true si todavia no puede recibir otro golpe.*/
    public boolean isDamageCooldownActive() {
        return damageCooldownTicks > 0;
    }
    
    /**Maneja la lógica de muerte y restaura el estado original.*/
    public void die() {
        // Aquí la fachada o nivel se encargará de reubicarlo.
        isDead = true;
    }
    
    /**Regresa los valores iniciales y limpia el progreso del jugador para un nuevo intento.*/
    @Override
    public void reset() {
        this.x = startX;
        this.y = startY;
        this.collectedCoins = 0;
        this.extraLives = 0;
        this.damageCooldownTicks = 0;
        applyInitialSkin(); // Vuelve a su estado original
        this.isDead = false;
    }

    /**
     * Reubica al jugador sin limpiar monedas ni la skin temporal actual.
     * Se usa cuando se necesita separar cuerpos sin registrar muerte.
     */
    public void resetPositionOnly() {
        this.x = startX;
        this.y = startY;
    }

    /**
     * Cambia el punto de reaparicion del jugador al entrar en un checkpoint.
     * @param x Nueva posicion de reaparicion en X.
     * @param y Nueva posicion de reaparicion en Y.
     */
    public void setRespawnPoint(int x, int y) {
        this.startX = x;
        this.startY = y;
    }

    /**Restablece el punto de reaparicion al inicio original del jugador.*/
    public void resetRespawnPoint() {
        this.startX = initialX;
        this.startY = initialY;
    }

    /**Mueve al jugador en el tablero basado en su velocidad actual.
     * @param dx Dirección en el eje X.
     * @param dy Dirección en el eje Y.*/
    public void move(int dx, int dy) {
        x += dx * getSpeed();
        y += dy * getSpeed();
    }
    

    /**Agrega una vida extra al jugador cuando toma una fuente de vida.*/
    public void addExtraLife() { extraLives++; }

    /**Obtiene las vidas extra acumuladas por elementos especiales.
     * @return Cantidad de vidas extra disponibles.*/
    public int getExtraLives() { return extraLives; }

    /**Establece las vidas extra al cargar una partida.
     * @param extraLives Cantidad de vidas extra restauradas.*/
    public void setExtraLives(int extraLives) { this.extraLives = Math.max(0, extraLives); }

    /**Lógica para recolectar una moneda y sumar al puntaje.*/
    public void collectCoin() { collectedCoins++; }
    
    /**Obtiene la cantidad de monedas recolectadas.
     * @return Número de monedas.*/
    public int getCollectedCoins() { return collectedCoins; }

    /**Establece la cantidad de monedas recolectadas al cargar una partida.
     * @param collectedCoins Número de monedas ya recogidas.*/
    public void setCollectedCoins(int collectedCoins) { this.collectedCoins = collectedCoins; }

    /**Establece el color de borde usado para distinguir al jugador.
     * @param borderColorName Nombre del color elegido en la interfaz.*/
    public void setBorderColorName(String borderColorName) {
        if (borderColorName != null && !borderColorName.trim().isEmpty()) {
            this.borderColorName = borderColorName;
        }
    }

    /**Obtiene el color de borde configurado para el jugador.
     * @return Nombre del color de borde.*/
    public String getBorderColorName() { return borderColorName; }

    /**Define si el jugador debe completar el nivel llegando a la zona inicial.
     * @param invertedGoal true cuando juega desde la zona final hacia la inicial.*/
    public void setInvertedGoal(boolean invertedGoal) {
        this.invertedGoal = invertedGoal;
    }

    /**Indica si el jugador usa la meta opuesta en modo PvP.
     * @return true si su objetivo final es la zona inicial.*/
    public boolean usesInvertedGoal() { return invertedGoal; }

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
    
    /**Entrega los datos visuales del jugador usando el tamaño completo del sprite.
     * @return Datos de renderizado con el borde del jugador.*/
    @Override
    public RenderData getRenderData() {
        Rectangle bounds = new Rectangle(getX() + getMargin(),
                getY() + getMargin(),
                getCurrentHitboxSize(),
                getCurrentHitboxSize());

        return new RenderData(bounds.x, bounds.y, bounds.width, bounds.height,
                getSpriteType(), getBorderColorName());
    }
}
