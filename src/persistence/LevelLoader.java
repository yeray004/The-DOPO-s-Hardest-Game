package persistence;

import domain.*;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Clase encargada de cargar la configuración física y las entidades de los niveles 
 * desde archivos de texto de forma polimórfica.
 * @author Yeray Guacheta
 * @support Assisted by Gemini (Google AI) - May 2026
 * @version 4.0
 */
public class LevelLoader {
    private final int TILE_SIZE = 40;
    
    // Diccionario que vincula una palabra clave (TOKEN) con su lógica de creación
    private final Map<String, EntityParser> parsers = new HashMap<>();

    /**
     * Interfaz funcional interna para delegar la construcción de entidades sin acoplamiento.
     */
    private interface EntityParser {
        /**
         * Parsea los datos de texto y añade la entidad correspondiente al nivel.
         * @param tokens Arreglo de strings con la línea segmentada.
         * @param level Nivel en proceso de construcción.
         */
        void parse(String[] tokens, Level level);
    }

    /**
     * Constructor que registra los parsers específicos para cada entidad usando Lambdas.
     * Esto elimina por completo los bloques condicionales (if-else) complejos.
     */
    public LevelLoader() {
        
        // Registro para construir Jugadores
        parsers.put("PLAYER", (tokens, level) -> {
            // Convierte coordenadas de matriz a píxeles en pantalla
            int x = Integer.parseInt(tokens[1]) * TILE_SIZE;
            int y = Integer.parseInt(tokens[2]) * TILE_SIZE;
            level.getPlayers().add(new Player(x, y));
        });

        // Registro para construir Monedas dinámicas por color/tipo
        parsers.put("COIN", (tokens, level) -> {
            int x = Integer.parseInt(tokens[1]) * TILE_SIZE;
            int y = Integer.parseInt(tokens[2]) * TILE_SIZE;
            String color = tokens[3]; // Lee el identificador visual ("Yellow", "Blue", etc.)
            level.getCoins().add(new Coin(x, y, color));
        });

        // Registro polimórfico para procesar cualquier tipo de Enemigo y su Estrategia
        parsers.put("ENEMY", (tokens, level) -> {
            String strategyType = tokens[1]; // BASIC, ORANGE, PINK, RED
            String color = tokens[2];        // Nombre de la imagen/sprite
            int x = Integer.parseInt(tokens[3]) * TILE_SIZE;
            int y = Integer.parseInt(tokens[4]) * TILE_SIZE;
            int speed = Integer.parseInt(tokens[5]);

            // Se delega la creación de la estrategia a la Fábrica Pura
            EnemyStrategy strategy = EnemyStrategyFactory.getStrategy(strategyType);
            int dx = 0, dy = 0;
            List<Point> route = null;

            // Parsea atributos específicos según el comportamiento requerido
            if ("BASIC".equals(strategyType)) {
                dx = Integer.parseInt(tokens[6]);
                dy = Integer.parseInt(tokens[7]);
            } else if ("ORANGE".equals(strategyType)) {
                route = parseRoute(tokens[6]); // Parsea la cadena de coordenadas de patrulla
            }

            // Inyección de dependencias: se añade el enemigo completamente configurado
            level.getEnemies().add(new Enemy(x, y, color, speed, route, dx, dy, strategy));
        });
    }

    /**
     * Construye un objeto Level leyendo y procesando un archivo de configuración estructurado.
     * @param filePath Ruta del archivo de texto del nivel.
     * @return Un nuevo nivel configurado con su mapa y entidades, o null si falla.
     */
    public Level loadLevel(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            
            // Primera línea: Lectura de dimensiones lógicas del mapa
            String[] dim = br.readLine().split(" ");
            int width = Integer.parseInt(dim[0]);
            int height = Integer.parseInt(dim[1]);

            Level nuevoNivel = new Level(width, height, new int[height][width]);

            // SECCIÓN 1: Procesamiento de la matriz física del escenario
            for (int i = 0; i < height; i++) {
                String[] row = br.readLine().split(" ");
                for (int j = 0; j < width; j++) {
                    int val = Integer.parseInt(row[j]);
                    nuevoNivel.getMapTemplate()[i][j] = val;
                    
                    // Clasificación e instanciación de objetos estáticos del mapa
                    if (val == 0) {
                        nuevoNivel.getWalls().add(new Wall(j, i, "Black"));
                    } else if (val == 3) {
                        nuevoNivel.getSafeZones().add(new SafeZone(j, i, "Green", false));
                    } else if (val == 4) {
                        nuevoNivel.getSafeZones().add(new SafeZone(j, i, "Green", true));
                    }
                }
            }

            // SECCIÓN 2: Lectura secuencial de las entidades dinámicas restantes
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Salta líneas en blanco de seguridad
                
                String[] tokens = line.split(" ");
                String keyToken = tokens[0]; // Clave primaria (PLAYER, COIN, ENEMY)
                
                // Si la palabra clave está registrada en el mapa, ejecuta su Lambda asociada
                if (parsers.containsKey(keyToken)) {
                    parsers.get(keyToken).parse(tokens, nuevoNivel);
                }
            }
            return nuevoNivel;
            
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error crítico parseando el archivo de nivel: " + e.getMessage());
            return null;
        }
    }

    /**
     * Convierte una cadena de texto de coordenadas en una lista de puntos escalados.
     * @param routeToken Cadena con formato serializado "x1,y1;x2,y2;...".
     * @return Lista de puntos geométricos Point traducidos a píxeles del juego.
     */
    private List<Point> parseRoute(String routeToken) {
        List<Point> points = new ArrayList<>();
        // Divide primero los pares de coordenadas separados por punto y coma
        for (String pair : routeToken.split(";")) {
            // Separa la componente X de la Y mediante la coma
            String[] coord = pair.split(",");
            int rX = Integer.parseInt(coord[0]) * TILE_SIZE;
            int rY = Integer.parseInt(coord[1]) * TILE_SIZE;
            points.add(new Point(rX, rY));
        }
        return points;
    }
}