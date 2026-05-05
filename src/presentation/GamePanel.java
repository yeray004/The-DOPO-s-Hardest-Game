package presentation;

import domain.DOPOsHardestGame;
import domain.Level;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel principal donde se dibuja la maqueta del juego.
 * 
 * @author Yeray Guacheta
 * @version 1.0
 */
public class GamePanel extends JPanel {
    private DOPOsHardestGame game;
    private final int TILE_SIZE = 25;
    private TileManager tileManager;
    private Map<String, BufferedImage> sprites;
    
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
            sprites.put("Red", ImageIO.read(new File("res/blinky.png"))); // Blinky
            sprites.put("Blue", ImageIO.read(new File("res/enemy.png"))); // Enemigo básico
            sprites.put("Yellow", ImageIO.read(new File("res/coin.png"))); // Moneda
            
            // Futura extensión: sprites.put("Pink", ImageIO.read(new File("res/inky.png")));
        } catch (Exception e) {
            System.out.println("Error cargando imágenes de entidades: " + e.getMessage());
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
        domain.Level currentLevel = game.getCurrentLevel();
        if (currentLevel != null) {
            
            // centrado automático del nivel cargado
            int mapWidth = currentLevel.getMapTemplate()[0].length * TILE_SIZE;
            int mapHeight = currentLevel.getMapTemplate().length * TILE_SIZE;
            int startX = (getWidth() - mapWidth) / 2;
            int startY = (getHeight() - mapHeight) / 2;

            tileManager.draw(g, currentLevel.getMapTemplate(), startX, startY, TILE_SIZE);
            
            int offset = 7; 
            int size = 25;

            // dibujar monedas
            for (domain.Coin c : currentLevel.getCoins()) {
                if (!c.isCollected()) {
                    g.drawImage(sprites.get(c.getColor()), startX + (c.getX() * TILE_SIZE), startY + (c.getY() * TILE_SIZE), TILE_SIZE, TILE_SIZE, null);
                }
            }

            // dibujar enemigos
            for (domain.Enemy e : currentLevel.getEnemies()) {
                g.drawImage(sprites.get(e.getColor()), startX + (e.getX() * TILE_SIZE), startY + (e.getY() * TILE_SIZE), TILE_SIZE, TILE_SIZE, null);
            }

            //dibujar jugadores
            for (domain.Player p : currentLevel.getPlayers()) {
                g.drawImage(sprites.get(p.getColor()), startX + (p.getX() * TILE_SIZE), startY + (p.getY() * TILE_SIZE), TILE_SIZE, TILE_SIZE, null);
            }
        }
    }
}