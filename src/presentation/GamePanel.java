package presentation;

import domain.DOPOsHardestGame;
import domain.RenderData; //Intermedia

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

/**
 * Panel principal donde se dibuja la maqueta del juego.
 * 
 * @author Yeray Guacheta
 * @version 1.0
 */
public class GamePanel extends JPanel implements Runnable {
    private DOPOsHardestGame game;
    private boolean isPaused = true;
    private final int TILE_SIZE = 40;
    //private final int SPRITE_SIZE = 25; // tamaño visual de los personajes y objetos
    //private final int OFFSET = (TILE_SIZE - SPRITE_SIZE) / 2; // Margen para centrar
    private TileManager tileManager;
    private Map<String, BufferedImage> sprites;
    // Atributos de movimiento
    private Thread gameThread;
    private final int FPS = 60;
    // Interruptores de dirección
    private boolean up, down, left, right;
    private boolean up2, down2, left2, right2;
    
    private static String pathPlayer = "res/blinky.png";
    private static String pathEnemy = "res/enemy.png";
    private static String pathCoin = "res/coin.png";
    
    /**
     * Constructor que inicializa el lienzo y configura su color de fondo.
     * @param game Instancia del juego para extraer los elementos a dibujar.
     */
    public GamePanel(DOPOsHardestGame game) {
        this.game = game;
        tileManager = new TileManager();
        sprites = new HashMap<>();
        setBackground(new Color(184, 185, 254));
        
        try {
            // Mapeamos el color (identificador) de cada Elemento a su PNG
        	sprites.put("Player", ImageIO.read(new File(pathPlayer)));
            sprites.put("Enemy", ImageIO.read(new File(pathEnemy)));
            sprites.put("Coin", ImageIO.read(new File(pathCoin)));
            
        } catch (Exception e) {
            System.out.println("Error cargando imágenes de entidades: " + e.getMessage());
        }
        
        //Key listener
        requestFocusInWindow();
        loadSprites();
        setFocusable(true);
        
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
            	// Controles Jugador 1 (Flechas)
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) up = true;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) down = true;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) left = true;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) right = true;
                
                // Controles Jugador 2 (WASD)
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_W) up2 = true;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_S) down2 = true;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A) left2 = true;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_D) right2 = true;
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
            	// Controles Jugador 1 (Flechas)
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) up = false;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) down = false;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) left = false;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) right = false;
                
                // Controles Jugador 2 (WASD)
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_W) up2 = false;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_S) down2 = false;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A) left2 = false;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_D) right2 = false;
            }
        });
        startGameThread();
    }
    //Movimiento
    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();   // 1 Calcula las nuevas posiciones
                repaint();  // 2 Dibuja los cambios
                delta--;
            }
        }
    }

    /**Calcula de forma independiente las intenciones de movimiento de ambos jugadores y actualiza el dominio.*/
    private void update() {
        if (isPaused) return;

        // Procesar Jugador 1 (Índice 0)
        int dx1 = 0, dy1 = 0;
        if (up) dy1 = -1;
        if (down) dy1 = 1;
        if (left) dx1 = -1;
        if (right) dx1 = 1;
        if (dx1 != 0 || dy1 != 0) {
            game.handlePlayerMovement(0, dx1, dy1);
        }

        // Procesar Jugador 2 (Índice 1)
        int dx2 = 0, dy2 = 0;
        if (up2) dy2 = -1;
        if (down2) dy2 = 1;
        if (left2) dx2 = -1;
        if (right2) dx2 = 1;
        if (dx2 != 0 || dy2 != 0) {
            game.handlePlayerMovement(1, dx2, dy2);
        }

        game.update();
        
        GameFrame parentFrame = (GameFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame != null) {
            parentFrame.refreshCounters();
        }
    }
    
    private void loadSprites() {
        try {
        	sprites.put("Blinky", ImageIO.read(new File("res/blinky.png")));
            sprites.put("Inky", ImageIO.read(new File("res/inky.png")));
            sprites.put("Clyde", ImageIO.read(new File("res/clyde.png")));
            sprites.put("ClydeDamaged", ImageIO.read(new File("res/clyde_damaged.png")));
            sprites.put("BasicEnemy", ImageIO.read(new File("res/enemy.png")));
            sprites.put("Yellow", ImageIO.read(new File("res/coin.png")));
            
            // --- Mapeos temporales para que dibujen el mismo sprite azul ---
            sprites.put("Orange", ImageIO.read(new File("res/enemy.png")));
            sprites.put("Pink", ImageIO.read(new File("res/enemy.png")));
            sprites.put("Red", ImageIO.read(new File("res/enemy.png")));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error cargando sprites de entidades.");
        }
    }
    
    /**
     * Método sobreescrito que dibuja en pantalla el mapa y las entidades del nivel.
     * @param g Componente gráfico de Java utilizado para renderizar formas y colores.
     */
    //Método implementado con apoyo de inteligencia artificial.
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int[][] map = game.getMapData(); 
        if (map == null) return;
        
        int mapW = map[0].length * TILE_SIZE;
        int mapH = map.length * TILE_SIZE;
        int startX = (getWidth() - mapW) / 2;
        int startY = (getHeight() - mapH) / 2;

        tileManager.draw(g, map, startX, startY, TILE_SIZE);

        for (RenderData entity : game.getEntitiesToDraw()) {
            BufferedImage img = sprites.get(entity.type);
            if (img != null) {
                // Dibujado dinámico: Usa exactamente las medidas proporcionadas por el dominio
                g.drawImage(img, 
                    startX + entity.x, 
                    startY + entity.y, 
                    entity.width, 
                    entity.height, 
                    null);
            }
        }
    }
    
    //PAUSA
    /**Alterna el estado de pausa del juego.*/
    public void togglePause() {
        isPaused = !isPaused;
    }

    /**Retorna si el juego se encuentra pausado.
     * @return true si está en pausa.*/
    public boolean isPaused() {
        return isPaused;
    }
    
    /**Reanuda las mecánicas y físicas del juego.*/
    public void resumeGame() {
        isPaused = false;
    }
}