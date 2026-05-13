package presentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
// import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Gestor de baldosas visuales basado en el diseño Data-Driven.
 * @author Yeray Guacheta
 * @version 1.0
 */
public class TileManager {
    private Tile[] tiles;
    
    private static String pathWall = "res/wall.png";
    private static String pathFloorL = "res/lightFloor.png";
    private static String pathFloorD = "res/darkFloor.png";
    private static String pathSafe = "res/safeZone.png";

    /**
     * Constructor de la clase. 
     * Inicializa el arreglo de baldosas con un tamaño fijo y llama al método para cargar las imágenes en memoria.
     */
    public TileManager() {
        tiles = new Tile[10]; // Capacidad para 10 tipos distintos de bloques
        loadTileImages();
    }
    /**
     * Carga las imágenes PNG desde la carpeta de recursos ('res') y las asigna a su respectivo índice en el arreglo.
     * El índice corresponde al número usado en la matriz del archivo de texto (0 = piso, 1 = pared, 2 = zona segura).
     * En caso de error de lectura, imprime el mensaje de la excepción en consola.
     */
    private void loadTileImages() {
        try {
        	//Pared
        	tiles[0] = new Tile(ImageIO.read(new File(pathWall)));
        	//Piso Claro
            tiles[1] = new Tile(ImageIO.read(new File(pathFloorL)));
            //Piso Oscuro
            tiles[2] = new Tile(ImageIO.read(new File(pathFloorD)));
            //Zona Segura
            tiles[3] = new Tile(ImageIO.read(new File(pathSafe)));
            //Zona Segura
            tiles[4] = new Tile(ImageIO.read(new File(pathSafe)));
        } catch (Exception e) {
            System.out.println("Error cargando imágenes de las baldosas: " + e.getMessage());
        }
    }

    /**
     * Dibuja el mapa completo en el panel renderizando las imágenes cargadas.
     * Itera sobre la matriz del nivel y dibuja la imagen correspondiente a cada número en su posición.
     * 
     * @param g Componente gráfico de Java utilizado para dibujar las imágenes.
     * @param map Matriz bidimensional de enteros que representa la "topología" del nivel.
     * @param startX Coordenada X de origen para centrar el mapa horizontalmente.
     * @param startY Coordenada Y de origen para centrar el mapa verticalmente.
     * @param tileSize Tamaño en píxeles (ancho y alto) con el que se dibujará cada baldosa.
     */
    public void draw(Graphics g, int[][] map, int startX, int startY, int tileSize) {
        Graphics2D g2 = (Graphics2D) g;
        
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                int tileNum = map[i][j];
                
                // Si NO es pared (0), dibujamos la baldosa y verificamos sus bordes
                if (tileNum > 0 && tileNum < tiles.length && tiles[tileNum] != null) {
                    int x = startX + (j * tileSize);
                    int y = startY + (i * tileSize);
                    
                    g2.drawImage(tiles[tileNum].getImage(), x, y, tileSize, tileSize, null);

                    // Contorno negro de 3 píxeles generado con Inteligencia artificial (Para que se vea como el original).
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(3));
                    
                    if (i == 0 || map[i-1][j] == 0) g2.drawLine(x, y, x + tileSize, y); // Arriba
                    if (i == map.length - 1 || map[i+1][j] == 0) g2.drawLine(x, y + tileSize, x + tileSize, y + tileSize); // Abajo
                    if (j == 0 || map[i][j-1] == 0) g2.drawLine(x, y, x, y + tileSize); // Izquierda
                    if (j == map[0].length - 1 || map[i][j+1] == 0) g2.drawLine(x + tileSize, y, x + tileSize, y + tileSize); // Derecha
                }
            }
        }
    }
}