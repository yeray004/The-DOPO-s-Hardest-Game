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
    private final int TILE_SIZE = 40;
    private final int SPRITE_SIZE = 25; // tamaño visual de los personajes y objetos
    private final int OFFSET = (TILE_SIZE - SPRITE_SIZE) / 2; // Margen para centrar
    private TileManager tileManager;
    private Map<String, BufferedImage> sprites;
    // Atributos de movimiento
    private Thread gameThread;
    private final int FPS = 60;
    // Interruptores de dirección
    private boolean up, down, left, right;
    
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
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) up = true;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) down = true;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) left = true;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) right = true;
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) up = false;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) down = false;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) left = false;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) right = false;
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

    private void update() {
        int dx = 0, dy = 0;
        if (up) dy = -1;
        if (down) dy = 1;
        if (left) dx = -1;
        if (right) dx = 1;

        if (dx != 0 || dy != 0) {
            game.handlePlayerMovement(dx, dy);
        }
        game.update();
    }
    
    private void loadSprites() {
        try {
            sprites.put("Player", ImageIO.read(new File(pathPlayer)));
            sprites.put("Enemy", ImageIO.read(new File(pathEnemy)));
            sprites.put("Coin", ImageIO.read(new File(pathCoin)));
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
        int[][] map = game.getMapData(); // Fachada
        if (map == null) return;
        
        int mapW = map[0].length * TILE_SIZE;
        int mapH = map.length * TILE_SIZE;
        int startX = (getWidth() - mapW) / 2;
        int startY = (getHeight() - mapH) / 2;

        tileManager.draw(g, map, startX, startY, TILE_SIZE);

        // Uso de polimorfismo: el objeto dice qué sprite necesita
        for (RenderData entity : game.getEntitiesToDraw()) {
            BufferedImage img = sprites.get(entity.type);
            if (img != null) {
                g.drawImage(img, 
                    startX + entity.x + OFFSET, // x es píxel, solo sumamos el inicio del mapa
                    startY + entity.y + OFFSET, // y es píxel
                    SPRITE_SIZE, SPRITE_SIZE, null);
            }
            //g.setColor(Color.GREEN);
            //g.drawRect(startX + entity.x + OFFSET + 3, startY + entity.y + OFFSET + 3, SPRITE_SIZE - 6, SPRITE_SIZE - );
        }
    }
}