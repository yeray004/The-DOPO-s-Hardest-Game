package domain;

/**
 * Excepcion personalizada para manejar errores propios de The DOPO Hardest Game.
 * Centraliza los mensajes de error usados por dominio y persistencia.
 *
 * @author Yeray Guacheta
 * @version 1.1
 */
public class DOPOsHardestGameException extends Exception {
    public static final String FILE_NOT_FOUND_ERROR = "File not found or access denied.";
    public static final String IO_ERROR = "I/O error while reading/writing the file.";
    public static final String CLASS_NOT_FOUND_ERROR = "Invalid file format or missing class.";
    public static final String INVALID_LINE_ERROR = "Invalid format in line: ";
    public static final String NUMBER_FORMAT_ERROR = "Coordinates must be numeric.";
    public static final String INVALID_STRATEGY_ERROR = "Unsupported enemy strategy: ";
    public static final String EMPTY_STRATEGY_ERROR = "Enemy strategy type cannot be empty.";
    public static final String NULL_ENEMY_STRATEGY = "Enemy strategy cannot be null.";
    public static final String INVALID_COIN_ERROR = "Unsupported coin type: ";
    public static final String INVALID_SPECIAL_ELEMENT_ERROR = "Unsupported special element: ";
    public static final String LEVEL_LOAD_ERROR = "Error loading level configuration.";
    public static final String GAME_SAVE_ERROR = "Error saving the game state.";
    public static final String GAME_LOAD_ERROR = "Error loading the game state.";

    /**
     * Crea una excepcion del juego con un mensaje descriptivo.
     * @param message Descripcion clara del error ocurrido.
     */
    public DOPOsHardestGameException(String message) {
        super(message);
    }
}
