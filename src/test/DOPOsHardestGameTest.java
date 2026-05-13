package test;

import static org.junit.Assert.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import domain.*;

public class DOPOsHardestGameTest {
	private Level level;
    private Blinky player;

    @BeforeEach
    public void setUp() {
        // Creamos un entorno controlado sin necesidad de cargar un .txt
        int[][] map = {{0, 0}, {3, 3}}; // Fila 0: Paredes, Fila 1: Libre
        level = new Level(2, 2, map);
        
        // Jugador inicia en zona libre (x=40, y=40)
        player = new Blinky(40, 40, "Red", 40, 40); 
        
        // Agregamos una pared física justo arriba del jugador (x=40, y=0)
        level.getWalls().add(new Wall(1, 0, "Black"));
    }
    
    // ==========================================
    // --- PRUEBAS FACHADA: Progreso de Nivel ---
    // ==========================================

    @Test
    public void shouldAdvanceToNextLevelWhenAvailable() {
        String[] mapas = {"nivel1.txt", "nivel2.txt"};
        DOPOsHardestGame game = new DOPOsHardestGame(mapas);
        
        game.nextLevel();
        
        assertEquals("Debería avanzar al índice 1.", 1, game.getCurrentLevel());
    }

    @Test
    public void shouldNotAdvanceLevelWhenAtLastLevel() {
        String[] mapas = {"nivel1.txt"};
        DOPOsHardestGame game = new DOPOsHardestGame(mapas);
        
        game.nextLevel(); 
        
        assertEquals("NO debería avanzar si es el último nivel.", 0, game.getCurrentLevel());
    }

    // ==========================================
    // --- PRUEBAS FACHADA: Temporizador ---
    // ==========================================

    @Test
    public void shouldDecreaseTimeLeftByOne() {
        String[] mapas = {"nivel1.txt"};
        DOPOsHardestGame game = new DOPOsHardestGame(mapas);
        int initialTime = game.getTimeLeft(); // Asume 60
        
        game.decreaseTime();
        
        assertEquals("Debería reducir el tiempo en 1.", initialTime - 1, game.getTimeLeft());
    }

    @Test
    public void shouldNotDecreaseTimeBelowZero() {
        String[] mapas = {"nivel1.txt"};
        DOPOsHardestGame game = new DOPOsHardestGame(mapas);
        
        // Reducimos el tiempo más veces de las permitidas
        for(int i = 0; i < 65; i++) {
            game.decreaseTime();
        }
        
        assertEquals("NO debería bajar de 0 segundos.", 0, game.getTimeLeft());
    }

    // ==========================================
    // --- PRUEBAS FACHADA: Reinicio ---
    // ==========================================

    @Test
    public void shouldIncrementDeathsOnRestart() {
        String[] mapas = {"nivel1.txt"};
        DOPOsHardestGame game = new DOPOsHardestGame(mapas);
        int initialDeaths = game.getTotalDeaths();
        
        game.restartLevel();
        
        assertEquals("Debería sumar 1 muerte al reiniciar.", initialDeaths + 1, game.getTotalDeaths());
    }

    @Test
    public void shouldNotKeepOldTimeOnRestart() {
        String[] mapas = {"nivel1.txt"};
        DOPOsHardestGame game = new DOPOsHardestGame(mapas);
        
        game.decreaseTime(); // El tiempo baja a 59
        game.restartLevel(); // Restablece el nivel
        
        assertEquals("El tiempo NO debería mantenerse, debe volver a 60.", 60, game.getTimeLeft());
    }
    
    
    // ==========================================
    // --- PRUEBAS CLASE LEVEL: Jugador ---
    // ==========================================

    
    /** Verifica que el jugador cambie su posición cuando intenta moverse a un espacio vacío.*/
    @Test
    public void shouldMovePlayerWhenPathIsClear() {
        int initialX = player.getX();
        level.attemptPlayerMove(player, 1, 0); 
        assertTrue("El jugador debería moverse hacia la derecha.", player.getX() > initialX);
    }
    
    /** Verifica que el nivel se complete solo cuando el jugador tiene todas las monedas y toca la meta. */
    @Test
    public void shouldWinWhenRequirementsMet() {
        Level level = new Level(2, 2, new int[][]{{0,0},{0,0}});
        Blinky player = new Blinky(40, 40, "Red", 40, 40);
        Coin coin = new Coin(40, 40, "Yellow");
        SafeZone goal = new SafeZone(1, 1, "Green", true); // Meta en X:40, Y:40

        level.getPlayers().add(player);
        level.getCoins().add(coin);
        level.getSafeZones().add(goal);

        // Debería ser falso porque no ha recogido la moneda
        assertFalse("No debería ganar sin la moneda.", level.isCompleted(player));

        // Recoge moneda y toca meta
        level.attemptPlayerMove(player, 0, 0); 
        
        assertTrue("Debería ganar con todos los requisitos.", level.isCompleted(player));
    }
    
    // ==========================================
    // --- PRUEBAS CLASE LEVEL: Monedas ---
    // ==========================================

    /**Verifica que el jugador incremente su contador de monedas al pasar sobre una.*/
    @Test
    public void shouldCollectCoinWhenTouching() {
        Level level = new Level(2, 2, new int[][]{{0,0},{0,0}});
        Blinky player = new Blinky(40, 40, "Red", 40, 40);
        Coin coin = new Coin(40, 40, "Yellow"); // Misma posición

        level.getPlayers().add(player);
        level.getCoins().add(coin);
        level.attemptPlayerMove(player, 0, 0); // Actualiza interacciones

        assertEquals("Debería sumar 1 moneda.", 1, player.getCollectedCoins());
        assertTrue("La moneda debe marcarse como recogida.", coin.isCollected());
    }

    /**Verifica que no se recolecten monedas si el jugador no está en contacto con ellas.*/
    @Test
    public void shouldNotCollectCoinWhenFar() {
        Level level = new Level(2, 2, new int[][]{{0,0},{0,0}});
        Blinky player = new Blinky(40, 40, "Red", 40, 40);
        Coin coin = new Coin(100, 100, "Yellow"); // Posición lejana

        level.getPlayers().add(player);
        level.getCoins().add(coin);
        level.attemptPlayerMove(player, 0, 0); 

        assertEquals("No debería tener monedas.", 0, player.getCollectedCoins());
    }

    // ==========================================
    // --- PRUEBAS CLASE LEVEL: Enemigos y Muerte ---
    // ==========================================

    /**Verifica que el jugador regrese a su coordenada de origen exacta tras tocar a un enemigo.*/
    @Test
    public void shouldDieAndResetWhenTouchingEnemy() {
        Level level = new Level(2, 2, new int[][]{{0,0},{0,0}});
        // Jugador fuera de su origen (startX=40, actual=60)
        Blinky player = new Blinky(60, 60, "Red", 40, 40); 
        BasicEnemy enemy = new BasicEnemy(60, 60, "Blue", 0, null, 0, 0, 60, 60);

        level.getPlayers().add(player);
        level.getEnemies().add(enemy);
        level.updateEnemies(); // El enemigo valida la colisión al moverse

        assertEquals("El jugador debería volver a startX (40) al morir.", 40, player.getX());
    }

    /**Verifica que el jugador no sea reiniciado si el enemigo se encuentra fuera de su hitbox.*/
    @Test
    public void shouldNotDieWhenEnemyIsFar() {
        Level level = new Level(2, 2, new int[][]{{0,0},{0,0}});
        Blinky player = new Blinky(60, 60, "Red", 40, 40);
        BasicEnemy enemy = new BasicEnemy(150, 150, "Blue", 0, null, 0, 0, 150, 150);

        level.getPlayers().add(player);
        level.getEnemies().add(enemy);
        level.updateEnemies(); 

        assertEquals("El jugador NO debería volver al inicio.", 60, player.getX());
    }
}
