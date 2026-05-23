package persistence;

import domain.*;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Clase encargada de cargar la configuración física y las entidades de los niveles 
 * desde archivos de texto de forma polimórfica.
 * @author Yeray Guacheta
 * @support Assisted by Gemini (Google AI) - May 2026
 * @version 4.2
 */
public class LevelLoader {
    private static final Logger LOGGER = Logger.getLogger(LevelLoader.class.getName());
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
         * @throws DOPOsHardestGameException si la entidad no puede construirse correctamente.
         */
        void parse(String[] tokens, Level level) throws DOPOsHardestGameException;
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


        // Registro para configurar el tiempo limite del nivel
        parsers.put("TIME", (tokens, level) -> level.setTimeLimit(Integer.parseInt(tokens[1])));
        parsers.put("TIMER", (tokens, level) -> level.setTimeLimit(Integer.parseInt(tokens[1])));

        // Registro para construir Monedas dinámicas por color/tipo
        parsers.put("COIN", (tokens, level) -> {
            int x = Integer.parseInt(tokens[1]) * TILE_SIZE;
            int y = Integer.parseInt(tokens[2]) * TILE_SIZE;
            String coinType = tokens[3]; // Lee el identificador visual o de skin ("Yellow", "Blue", etc.)
            level.getCoins().add(CoinFactory.createCoin(x, y, coinType));
        });

        // Registro polimórfico para procesar cualquier tipo de Enemigo y su Estrategia
        parsers.put("ENEMY", (tokens, level) -> {
            String strategyType = tokens[1]; // LINEAR, PATROL, SEARCH, AMBUSH
            String normalizedStrategyType = normalizeStrategyType(strategyType);
            int dataStart = getEnemyDataStart(tokens);
            int x = Integer.parseInt(tokens[dataStart]) * TILE_SIZE;
            int y = Integer.parseInt(tokens[dataStart + 1]) * TILE_SIZE;
            int speed = Integer.parseInt(tokens[dataStart + 2]);

            // Se delega la creación de la estrategia a la Fábrica Pura
            EnemyStrategy strategy = EnemyStrategyFactory.getStrategy(strategyType);
            int dx = 0, dy = 0;
            List<Point> route = null;

            // Parsea atributos específicos según el comportamiento requerido
            if (isLinearStrategy(normalizedStrategyType)) {
                dx = Integer.parseInt(tokens[dataStart + 3]);
                dy = Integer.parseInt(tokens[dataStart + 4]);
            } else if (isPatrolStrategy(normalizedStrategyType)) {
                route = parseRoute(tokens[dataStart + 3]); // Parsea la cadena de coordenadas de patrulla
            }

            // Inyección de dependencias: se añade el enemigo completamente configurado
            level.getEnemies().add(new Enemy(x, y, speed, route, dx, dy, strategy));
        });

        // Registro para elementos especiales como fuentes de vida y bombas
        parsers.put("SPECIAL", (tokens, level) -> addSpecialElement(tokens, level));
        parsers.put("ELEMENT", (tokens, level) -> addSpecialElement(tokens, level));
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
                        nuevoNivel.getWalls().add(new Wall(j, i));
                    } else if (val == 3) {
                        nuevoNivel.getSafeZones().add(new SafeZone(j, i, false));
                    } else if (val == 4) {
                        nuevoNivel.getSafeZones().add(new SafeZone(j, i, true));
                    } else if (val == 5) {
                        nuevoNivel.getSafeZones().add(new SafeZone(j, i, false, true));
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
            
        } catch (FileNotFoundException e) {
            return handleLoadError(new DOPOsHardestGameException(DOPOsHardestGameException.FILE_NOT_FOUND_ERROR), e);
        } catch (IOException e) {
            return handleLoadError(new DOPOsHardestGameException(DOPOsHardestGameException.IO_ERROR), e);
        } catch (NumberFormatException e) {
            return handleLoadError(new DOPOsHardestGameException(DOPOsHardestGameException.NUMBER_FORMAT_ERROR), e);
        } catch (ArrayIndexOutOfBoundsException e) {
            return handleLoadError(new DOPOsHardestGameException(DOPOsHardestGameException.INVALID_LINE_ERROR + filePath), e);
        } catch (DOPOsHardestGameException e) {
            return handleLoadError(e, e);
        }
    }

    /**
     * Registra un error de carga para programadores y devuelve null a la fachada.
     * @param gameException Excepcion especifica del juego con mensaje controlado.
     * @param cause Causa original que produjo el error.
     * @return null porque el nivel no pudo construirse.
     */
    private Level handleLoadError(DOPOsHardestGameException gameException, Exception cause) {
        LOGGER.severe(DOPOsHardestGameException.LEVEL_LOAD_ERROR + " " + gameException.getMessage()
                + " Cause: " + cause.getMessage());
        //System.out.println(DOPOsHardestGameException.LEVEL_LOAD_ERROR + " " + gameException.getMessage());
        return null;
    }

    /**
     * Normaliza el nombre de la estrategia para trabajar solo con nombres por comportamiento.
     * @param strategyType Texto leido desde el archivo de nivel.
     * @return Texto normalizado para comparaciones internas.
     * @throws DOPOsHardestGameException si el tipo no es valido.
     */
    private String normalizeStrategyType(String strategyType) throws DOPOsHardestGameException {
        if (strategyType == null) {
            throw new DOPOsHardestGameException(DOPOsHardestGameException.EMPTY_STRATEGY_ERROR);
        }
        return strategyType.trim()
                           .replace("_", "")
                           .replace("-", "")
                           .toUpperCase(Locale.ROOT);
    }

    /**
     * Calcula desde que posicion empiezan las coordenadas del enemigo.
     * @param tokens Datos de la linea del enemigo.
     * @return Indice donde aparece la coordenada X.
     */
    private int getEnemyDataStart(String[] tokens) {
        return isInteger(tokens[2]) ? 2 : 3;
    }

    /**
     * Indica si un texto puede convertirse a entero.
     * @param value Texto a evaluar.
     * @return true si representa un numero entero.
     */
    private boolean isInteger(String value) {
        if (value == null || value.isEmpty()) return false;
        int start = value.charAt(0) == '-' ? 1 : 0;
        for (int i = start; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) return false;
        }
        return start < value.length();
    }

    /**
     * Indica si la estrategia corresponde a movimiento lineal.
     * @param strategyType Tipo normalizado de estrategia.
     * @return true si el enemigo requiere direccion inicial dx/dy.
     */
    private boolean isLinearStrategy(String strategyType) {
        return "LINEAR".equals(strategyType);
    }

    /**
     * Indica si la estrategia corresponde a movimiento por patrulla.
     * @param strategyType Tipo normalizado de estrategia.
     * @return true si el enemigo requiere una ruta de puntos.
     */
    private boolean isPatrolStrategy(String strategyType) {
        return "PATROL".equals(strategyType);
    }

    /**
     * Agrega un elemento especial desde una linea del archivo.
     * @param tokens Datos de texto del elemento.
     * @param level Nivel en construccion.
     * @throws DOPOsHardestGameException si el tipo de elemento no existe.
     */
    private void addSpecialElement(String[] tokens, Level level) throws DOPOsHardestGameException {
        String elementType;
        int x;
        int y;
        if (isInteger(tokens[1])) {
            x = Integer.parseInt(tokens[1]) * TILE_SIZE;
            y = Integer.parseInt(tokens[2]) * TILE_SIZE;
            elementType = tokens[3];
        } else {
            elementType = tokens[1];
            x = Integer.parseInt(tokens[2]) * TILE_SIZE;
            y = Integer.parseInt(tokens[3]) * TILE_SIZE;
        }
        level.getSpecialElements().add(SpecialElementFactory.createElement(x, y, elementType));
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
