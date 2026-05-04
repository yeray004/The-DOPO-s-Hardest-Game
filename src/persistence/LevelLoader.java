package persistence;

import domain.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase encargada de cargar la configuración de los niveles desde archivos de texto.
 * 
 * @author Yeray Guacheta
 * @support Assisted by Gemini (Google AI) - May 2026
 * @version 2.0
 */
public class LevelLoader {

    /**
     * Construye un objeto Level leyendo un archivo de texto.
     * @param filePath Ruta del archivo de texto. //por el momento la raíz
     * @return Un nuevo nivel configurado, o null si ocurre un error.
     */
    public Level loadLevel(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            
            // Primera línea: Ancho y Alto del mapa separados por espacio
            String[] dimensions = br.readLine().split(" ");
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);

            int[][] mapTemplate = new int[height][width];

            // Leer las siguientes líneas para llenar la matriz
            for (int i = 0; i < height; i++) {
                String[] row = br.readLine().split(" ");
                for (int j = 0; j < width; j++) {
                    mapTemplate[i][j] = Integer.parseInt(row[j]);
                }
            }
            
            Level nuevoNivel = new Level(width, height, mapTemplate);
	
	         // Blinky en la zona segura X: 2, Y: 4
	         nuevoNivel.getPlayers().add(new Blinky(2, 4, "Red"));
	
	         // Moneda en el centro del corredor X: 10, Y: 4
	         nuevoNivel.getCoins().add(new Coin(10, 4, "Yellow"));
	
	         // Tres enemigos azules de prueba en el corredor central
	         nuevoNivel.getEnemies().add(new BasicEnemy(6, 3, "Blue", 1, null));
	         nuevoNivel.getEnemies().add(new BasicEnemy(8, 5, "Blue", 1, null));
	         nuevoNivel.getEnemies().add(new BasicEnemy(10, 2, "Blue", 1, null));
	
	         return nuevoNivel;

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al cargar el archivo del nivel: " + e.getMessage());
            return null;
        }
    }
}