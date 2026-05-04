package presentation;

import domain.DOPOsHardestGame;
import domain.Level;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

/**
 * Panel principal donde se dibuja la maqueta del juego.
 * 
 * @author Yeray Guacheta
 * @version 1.0
 */
public class GamePanel extends JPanel {
    private DOPOsHardestGame game;
    private final int TILE_SIZE = 25;
    
    /**
     * Constructor que inicializa el lienzo y configura su color de fondo.
     * @param game Instancia del juego para extraer los elementos a dibujar.
     */
    public GamePanel(DOPOsHardestGame game) {
        this.game = game;
        setBackground(new Color(180, 180, 255)); //fondo violeta claro
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

            drawMap(g, currentLevel.getMapTemplate(), startX, startY);
            
            int offset = 7; 
            int size = 25;

            // Dibujar monedas
            for (domain.Coin c : currentLevel.getCoins()) {
                if (!c.isCollected()) {
                    g.setColor(Color.YELLOW);
                    g.fillOval(startX + (c.getX() * TILE_SIZE) + offset, startY + (c.getY() * TILE_SIZE) + offset, size, size);
                    g.setColor(Color.BLACK);
                    g.drawOval(startX + (c.getX() * TILE_SIZE) + offset, startY + (c.getY() * TILE_SIZE) + offset, size, size);
                }
            }

            // Dibujar enemigos
            for (domain.Enemy e : currentLevel.getEnemies()) {
                g.setColor(Color.BLUE);
                g.fillOval(startX + (e.getX() * TILE_SIZE) + offset, startY + (e.getY() * TILE_SIZE) + offset, size, size);
                g.setColor(Color.BLACK);
                g.drawOval(startX + (e.getX() * TILE_SIZE) + offset, startY + (e.getY() * TILE_SIZE) + offset, size, size);
            }

            // Dibujar Jugadores (Blinky)
            for (domain.Player p : currentLevel.getPlayers()) {
                g.setColor(Color.RED);
                g.fillRect(startX + (p.getX() * TILE_SIZE) + offset, startY + (p.getY() * TILE_SIZE) + offset, size, size);
                g.setColor(Color.BLACK);
                g.drawRect(startX + (p.getX() * TILE_SIZE) + offset, startY + (p.getY() * TILE_SIZE) + offset, size, size);
            }
        }
    }
    /**
     * Renderiza la cuadrícula del mapa utilizando coordenadas dinámicas para el centrado.
     * @param g Componente gráfico de Java.
     * @param map Matriz de enteros con la topología del nivel.
     * @param startX Coordenada X de origen para centrar el mapa.
     * @param startY Coordenada Y de origen para centrar el mapa.
     */
    //preguntar como añadir correctamente polimorfismo
    private void drawMap(Graphics g, int[][] map, int startX, int startY) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                int cell = map[i][j];
                
                // Si es pared {1}, no dibujamos nada para que se vea el fondo
                if (cell != 1) {
                    if (cell == 2) {
                        g.setColor(new Color(180, 255, 180)); // Zona Segura
                    } else {
                        if ((i + j) % 2 == 0) {
                            g.setColor(new Color(245, 245, 255)); // Piso claro
                        } else {
                            g.setColor(Color.WHITE); // Piso oscuro
                        }
                    }
                    g.fillRect(startX + (j * TILE_SIZE), startY + (i * TILE_SIZE), TILE_SIZE, TILE_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(startX + (j * TILE_SIZE), startY + (i * TILE_SIZE), TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }
}