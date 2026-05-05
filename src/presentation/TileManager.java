package presentation;

import java.awt.Graphics;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Gestor de baldosas visuales basado en el diseño Data-Driven.
 * @author Yeray Guacheta
 * @version 1.0
 */
public class TileManager {
    public Tile[] tiles;

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
            tiles[0] = new Tile();
            tiles[0].image = ImageIO.read(new File("res/wall.png"));
            //Piso Claro
            tiles[1] = new Tile();
            tiles[1].image = ImageIO.read(new File("res/lightFloor.png"));
            //Piso Oscuro
            tiles[2] = new Tile();
            tiles[2].image = ImageIO.read(new File("res/darkFloor.png"));
            //Zona Segura
            tiles[3] = new Tile();
            tiles[3].image = ImageIO.read(new File("res/safeZone.png"));
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
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                int tileNum = map[i][j];
                
                if (tiles[tileNum] != null && tiles[tileNum].image != null) {
                    g.drawImage(tiles[tileNum].image, startX + (j * tileSize), startY + (i * tileSize), tileSize, tileSize, null);
                }
            }
        }
    }
}