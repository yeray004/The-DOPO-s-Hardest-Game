package persistence;

import domain.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Clase encargada de leer archivos de salvaguarda y restaurar el estado del nivel.
 * @author Yeray Guacheta
 * @version 1.2
 */
public class GameLoader {
    private static final Logger LOGGER = Logger.getLogger(GameLoader.class.getName());

    /**
     * Carga una partida guardada sobre el nivel actual.
     * @param current Nivel activo que sera restaurado.
     * @param game Fachada donde se actualizan las metricas generales.
     * @param filePath Ruta del archivo de guardado.
     * @return true si la carga fue correcta, false si ocurrio un error.
     */
    public boolean load(Level current, DOPOsHardestGame game, String filePath) {
        if (current == null) return false;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int playerIdx = 0;
            int coinIdx = 0;
            int enemyIdx = 0;
            int specialIdx = 0;
            boolean playerScoreStored = false;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(" ");
                String type = tokens[0];

                switch (type) {
                    case "STATE":
                        game.setTimeLeft(Integer.parseInt(tokens[1]));
                        game.setTotalDeaths(Integer.parseInt(tokens[2]));
                        break;

                    case "PLAYER":
                        if (playerIdx < current.getPlayers().size()) {
                            Player p = current.getPlayers().get(playerIdx++);
                            p.setX(Integer.parseInt(tokens[1]));
                            p.setY(Integer.parseInt(tokens[2]));
                            if (tokens.length > 3) {
                                p.setExtraLives(Integer.parseInt(tokens[3]));
                            }
                            if (tokens.length > 4) {
                                p.setBorderColorName(tokens[4]);
                            }
                            if (tokens.length > 5) {
                                p.setCollectedCoins(Integer.parseInt(tokens[5]));
                                playerScoreStored = true;
                            }
                        }
                        break;

                    case "COIN":
                        if (coinIdx < current.getCoins().size()) {
                            Coin c = current.getCoins().get(coinIdx++);
                            c.setX(Integer.parseInt(tokens[1]));
                            c.setY(Integer.parseInt(tokens[2]));
                            c.setCollected(Boolean.parseBoolean(tokens[4]));
                        }
                        break;

                    case "ENEMY":
                        if (enemyIdx < current.getEnemies().size()) {
                            Enemy en = current.getEnemies().get(enemyIdx++);
                            en.setX(Integer.parseInt(tokens[1]));
                            en.setY(Integer.parseInt(tokens[2]));
                        }
                        break;

                    case "SPECIAL":
                        if (specialIdx < current.getSpecialElements().size()) {
                            SpecialElement special = current.getSpecialElements().get(specialIdx++);
                            special.setX(Integer.parseInt(tokens[1]));
                            special.setY(Integer.parseInt(tokens[2]));
                            if (tokens.length > 4) {
                                special.setActive(Boolean.parseBoolean(tokens[4]));
                            }
                        }
                        break;
                }
            }
            if (!playerScoreStored) {
                updateCollectedCoinsForLegacySave(current);
            }
            return true;
        } catch (FileNotFoundException e) {
            return handleLoadError(new DOPOsHardestGameException(DOPOsHardestGameException.FILE_NOT_FOUND_ERROR), e);
        } catch (IOException e) {
            return handleLoadError(new DOPOsHardestGameException(DOPOsHardestGameException.IO_ERROR), e);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return handleLoadError(new DOPOsHardestGameException(DOPOsHardestGameException.GAME_LOAD_ERROR), e);
        }
    }

    /**
     * Sincroniza partidas antiguas que no guardaban puntaje individual por jugador.
     * @param current Nivel restaurado desde archivo.
     */
    private void updateCollectedCoinsForLegacySave(Level current) {
        int collected = 0;
        for (Coin coin : current.getCoins()) {
            if (coin.isCollected()) {
                collected++;
            }
        }
        if (!current.getPlayers().isEmpty()) {
            current.getPlayers().get(0).setCollectedCoins(collected);
        }
    }

    /**
     * Registra un error de carga de partida para programadores.
     * @param gameException Excepcion del juego con mensaje controlado.
     * @param cause Causa original del problema.
     * @return false porque la partida no pudo cargarse.
     */
    private boolean handleLoadError(DOPOsHardestGameException gameException, Exception cause) {
        LOGGER.severe(gameException.getMessage() + " Cause: " + cause.getMessage());
        System.out.println(gameException.getMessage());
        return false;
    }
}
