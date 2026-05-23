package test;

import static org.junit.Assert.*;

import domain.*;

import java.util.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
//import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Pruebas unitarias para la fachada DOPOsHardestGame.
 * Cada caso valida un comportamiento esperado y un caso que no deberia alterar el estado.
 * @author Yeray Guacheta
 * @version 1.0
 */
public class DOPOsHardestGameTest {
	
	@BeforeClass
	public static void muteExpectedLogs() {
	    Logger.getLogger(persistence.LevelLoader.class.getName()).setLevel(java.util.logging.Level.OFF);
	}

    /**Crea un archivo temporal de nivel con una matriz simple y entidades opcionales.*/
    private String createLevelFile(String name, int timeLimit, String... lines) throws IOException {
        Path file = Files.createTempFile(name, ".txt");
        StringBuilder content = new StringBuilder();
        content.append("5 3\n");
        content.append("0 0 0 0 0\n");
        content.append("3 1 1 1 4\n");
        content.append("0 0 0 0 0\n");
        content.append("TIME ").append(timeLimit).append("\n");
        for (String line : lines) {
            content.append(line).append("\n");
        }
        Files.write(file, content.toString().getBytes(StandardCharsets.UTF_8));
        return file.toAbsolutePath().toString();
    }

    /**Crea una fachada con un nivel basico y prepara el modo individual.*/
    private DOPOsHardestGame createSingleGame(String... levelLines) throws IOException {
        DOPOsHardestGame game = new DOPOsHardestGame(new String[] {createLevelFile("level", 30, levelLines)});
        game.setGameMode("Single");
        return game;
    }

    /**Marca el nivel actual como completado colocando al jugador en la meta.*/
    private void completeCurrentLevel(DOPOsHardestGame game) {
        Level level = game.getCurrentLevel();
        Player player = level.getPlayers().get(0);
        for (Coin coin : level.getCoins()) {
            coin.collect(player);
        }
        player.setX(4 * 40);
        player.setY(1 * 40);
        game.movePlayer(0, 0);
    }

    /**Debe cargar niveles validos y no debe crear nivel cuando la ruta no existe.*/
    @Test
    public void shouldLoadValidLevelsAndShouldNotLoadMissingLevels() throws IOException {
        String levelPath = createLevelFile("valid", 45, "COIN 2 1 Yellow");
        DOPOsHardestGame game = new DOPOsHardestGame(new String[] {levelPath, "missing-file.txt"});

        assertNotNull(game.getCurrentLevel());
        assertEquals(1, game.getLevelNames().length);
        assertEquals(45, game.getTimeLeft());

        DOPOsHardestGame emptyGame = new DOPOsHardestGame(new String[] {"missing-file.txt"});
        assertNull(emptyGame.getCurrentLevel());
        assertEquals(0, emptyGame.getLevelNames().length);
    }

    /**Debe configurar el modo PvP y no debe dejar dos jugadores cuando se vuelve a modo individual.*/
    @Test
    public void shouldSetGameModeAndShouldNotKeepExtraPlayersInSingleMode() throws IOException {
        DOPOsHardestGame game = createSingleGame();

        game.setGameMode("PvP");
        assertEquals(2, game.getCurrentLevel().getPlayers().size());
        assertTrue(game.getCurrentLevel().getPlayers().get(1).usesInvertedGoal());

        game.setGameMode("Single");
        assertEquals(1, game.getCurrentLevel().getPlayers().size());
        assertFalse(game.getCurrentLevel().getPlayers().get(0).usesInvertedGoal());
    }

    /**Debe preparar jugadores para el modo actual y no debe fallar si no hay niveles cargados.*/
    @Test
    public void shouldSetupPlayersForModeAndShouldNotFailWithoutLoadedLevels() throws IOException {
        DOPOsHardestGame game = createSingleGame();
        game.setGameMode("PvP");
        game.setupPlayersForMode();

        assertEquals(2, game.getCurrentLevel().getPlayers().size());

        DOPOsHardestGame emptyGame = new DOPOsHardestGame(new String[] {"missing-file.txt"});
        emptyGame.setupPlayersForMode();
        assertNull(emptyGame.getCurrentLevel());
    }

    /**Debe seleccionar una configuracion valida y no debe cambiar con un indice invalido.*/
    @Test
    public void shouldSelectLevelAndShouldNotSelectInvalidIndex() throws IOException {
        String first = createLevelFile("first", 30);
        String second = createLevelFile("second", 55);
        DOPOsHardestGame game = new DOPOsHardestGame(new String[] {first, second});
        game.setTotalDeaths(4);

        game.selectLevel(1);
        assertEquals(1, game.getCurrentLevelIndex());
        assertEquals(0, game.getTotalDeaths());
        assertEquals(55, game.getTimeLeft());

        game.selectLevel(9);
        assertEquals(1, game.getCurrentLevelIndex());
    }

    /**Debe entregar nombres de niveles y no debe exponer la lista interna a cambios externos.*/
    @Test
    public void shouldReturnLevelNamesAndShouldNotExposeInternalNames() throws IOException {
        DOPOsHardestGame game = new DOPOsHardestGame(new String[] {createLevelFile("names", 30)});
        String[] names = game.getLevelNames();
        names[0] = "changed";

        assertEquals(1, game.getLevelNames().length);
        assertNotEquals("changed", game.getLevelNames()[0]);
    }

    /**Debe asignar skins iniciales y no debe perder la skin si se envian valores nulos.*/
    @Test
    public void shouldSetInitialSkinsAndShouldNotReplaceThemWithNull() throws IOException {
        DOPOsHardestGame game = createSingleGame();
        Player player = game.getCurrentLevel().getPlayers().get(0);

        game.setInitialSkins("Inky", null);
        assertEquals("Inky", player.getSpriteType());

        game.setInitialSkins(null, null);
        assertEquals("Inky", player.getSpriteType());
    }

    /**Debe asignar bordes de jugador y no debe reemplazarlos con valores nulos.*/
    @Test
    public void shouldSetPlayerBordersAndShouldNotReplaceThemWithNull() throws IOException {
        DOPOsHardestGame game = createSingleGame();
        Player player = game.getCurrentLevel().getPlayers().get(0);

        game.setPlayerBorders("Cyan", null);
        assertEquals("Cyan", player.getBorderColorName());

        game.setPlayerBorders(null, null);
        assertEquals("Cyan", player.getBorderColorName());
    }

    /**Debe actualizar elementos y no debe actualizar cuando hay victoria pendiente.*/
    @Test
    public void shouldUpdateElementsAndShouldNotUpdateWhenVictoryIsPending() throws IOException {
        DOPOsHardestGame game = createSingleGame("COIN 2 1 Yellow", "ENEMY LINEAR 2 1 1 1 0");
        Enemy enemy = game.getCurrentLevel().getEnemies().get(0);
        int initialX = enemy.getX();

        game.update();
        assertTrue(enemy.getX() > initialX);

        completeCurrentLevel(game);
        int xAfterVictory = enemy.getX();
        game.update();
        assertEquals(xAfterVictory, enemy.getX());
    }

    /**Debe registrar una muerte cuando un enemigo toca al jugador.*/
    @Test
    public void shouldRegisterDeathWhenEnemyTouchesPlayer() throws Exception {
        DOPOsHardestGame game = createSingleGame();
        Level level = game.getCurrentLevel();
        Player player = level.getPlayers().get(0);
        player.setX(40);
        player.setY(40);
        level.getEnemies().add(new Enemy(40, 40, 0, null, 0, 0, new LinearEnemyStrategy()));

        game.update();

        assertEquals(1, game.getTotalDeaths());
    }

    /**No debe registrar muerte cuando el enemigo esta lejos de los jugadores.*/
    @Test
    public void shouldNotRegisterDeathWhenEnemyIsFar() throws IOException {
        DOPOsHardestGame game = createSingleGame();
        try {
            game.getCurrentLevel().getEnemies().add(new Enemy(120, 40, 0, null, 0, 0, new LinearEnemyStrategy()));
        } catch (DOPOsHardestGameException e) {
            fail("No deberia fallar la creacion de un enemigo con estrategia valida.");
        }

        game.update();

        assertEquals(0, game.getTotalDeaths());
    }

    /**Debe hacer que el enemigo de busqueda persiga al jugador mas cercano en PvP.*/
    @Test
    public void shouldSearchEnemyFollowNearestPlayerInPvp() throws IOException {
        DOPOsHardestGame game = new DOPOsHardestGame(new String[] {createLevelFile("nearestTarget", 30,
                "ENEMY SEARCH 3 1 2")});
        game.setGameMode("PvP");
        Level level = game.getCurrentLevel();
        Enemy enemy = level.getEnemies().get(0);
        Player playerTwo = level.getPlayers().get(1);
        int initialX = enemy.getX();

        assertTrue(playerTwo.getX() > enemy.getX());
        game.update();

        assertTrue("El enemigo deberia acercarse al jugador dos, que esta mas cerca.", enemy.getX() > initialX);
        assertEquals(0, game.getTotalDeaths());
    }

    /**Debe avanzar al siguiente nivel y no debe avanzar si ya esta en el ultimo.*/
    @Test
    public void shouldGoToNextLevelAndShouldNotPassLastLevel() throws IOException {
        String first = createLevelFile("firstNext", 20);
        String second = createLevelFile("secondNext", 40);
        DOPOsHardestGame game = new DOPOsHardestGame(new String[] {first, second});

        assertTrue(game.hasNextLevel());
        game.nextLevel();
        assertEquals(1, game.getCurrentLevelIndex());
        assertFalse(game.hasNextLevel());

        game.nextLevel();
        assertEquals(1, game.getCurrentLevelIndex());
    }

    /**Debe continuar luego de una victoria y no debe avanzar si no hay mas niveles.*/
    @Test
    public void shouldContinueAfterVictoryAndShouldNotContinueWithoutNextLevel() throws IOException {
        String first = createLevelFile("firstVictory", 20);
        String second = createLevelFile("secondVictory", 40);
        DOPOsHardestGame game = new DOPOsHardestGame(new String[] {first, second});
        game.setGameMode("Single");
        completeCurrentLevel(game);

        assertTrue(game.hasPendingVictory());
        assertTrue(game.continueAfterVictory());
        assertEquals(1, game.getCurrentLevelIndex());

        completeCurrentLevel(game);
        assertFalse(game.continueAfterVictory());
        assertFalse(game.hasPendingVictory());
    }

    /**Debe entregar y limpiar mensajes de victoria.*/
    @Test
    public void shouldExposeVictoryMessageAndShouldClearVictory() throws IOException {
        DOPOsHardestGame game = createSingleGame();
        completeCurrentLevel(game);

        assertTrue(game.hasPendingVictory());
        assertTrue(game.getVictoryMessage().contains("Victoria"));

        game.clearVictory();
        assertFalse(game.hasPendingVictory());
    }

    /**Debe reiniciar el nivel y no debe modificar el estado si no hay nivel actual.*/
    @Test
    public void shouldRestartLevelAndShouldNotRestartWithoutCurrentLevel() throws IOException {
        DOPOsHardestGame game = createSingleGame("COIN 2 1 Yellow");
        game.setTimeLeft(1);
        game.restartLevel();

        assertEquals(30, game.getTimeLeft());
        assertEquals(1, game.getTotalDeaths());

        DOPOsHardestGame emptyGame = new DOPOsHardestGame(new String[] {"missing-file.txt"});
        emptyGame.restartLevel();
        assertEquals(0, emptyGame.getTotalDeaths());
    }

    /**Debe mover al jugador principal y no debe moverlo si hay victoria pendiente.*/
    @Test
    public void shouldMovePlayerAndShouldNotMoveWhenVictoryIsPending() throws IOException {
        DOPOsHardestGame game = createSingleGame();
        Player player = game.getCurrentLevel().getPlayers().get(0);
        int initialX = player.getX();

        game.movePlayer(1, 0);
        assertTrue(player.getX() > initialX);

        completeCurrentLevel(game);
        int xAfterVictory = player.getX();
        game.movePlayer(1, 0);
        assertEquals(xAfterVictory, player.getX());
    }

    /**Debe reducir el tiempo y no debe permitir valores negativos.*/
    @Test
    public void shouldDecreaseTimeAndShouldNotGoBelowZero() throws IOException {
        DOPOsHardestGame game = createSingleGame();
        game.setTimeLeft(1);

        game.decreaseTime();
        game.decreaseTime();

        assertEquals(0, game.getTimeLeft());
    }

    /**Debe mover un jugador por indice y no debe mover si el indice no existe.*/
    @Test
    public void shouldHandlePlayerMovementAndShouldNotMoveInvalidPlayerIndex() throws IOException {
        DOPOsHardestGame game = createSingleGame();
        Player player = game.getCurrentLevel().getPlayers().get(0);
        int initialX = player.getX();

        game.handlePlayerMovement(0, 1, 0);
        assertTrue(player.getX() > initialX);

        int xAfterMove = player.getX();
        game.handlePlayerMovement(5, 1, 0);
        assertEquals(xAfterMove, player.getX());
    }

    /**Debe entregar datos del nivel actual y no debe entregar datos si no hay nivel.*/
    @Test
    public void shouldReturnCurrentLevelDataAndShouldReturnEmptyDataWithoutLevel() throws IOException {
        DOPOsHardestGame game = createSingleGame("COIN 2 1 Yellow");

        assertNotNull(game.getCurrentLevel());
        assertNotNull(game.getMapData());
        assertFalse(game.getEntitiesToDraw().isEmpty());
        assertFalse(game.getRenderables().isEmpty());

        DOPOsHardestGame emptyGame = new DOPOsHardestGame(new String[] {"missing-file.txt"});
        assertNull(emptyGame.getCurrentLevel());
        assertNull(emptyGame.getMapData());
        assertTrue(emptyGame.getEntitiesToDraw().isEmpty());
        assertTrue(emptyGame.getRenderables().isEmpty());
    }

    /**Debe enviar el borde alineado en el DTO del jugador.*/
    @Test
    public void shouldReturnVisualPlayerBoundsAndShouldNotUseReducedHitboxForRendering() throws IOException {
        DOPOsHardestGame game = createSingleGame();
        RenderData playerData = null;
        for (RenderData data : game.getEntitiesToDraw()) {
            if ("Blinky".equals(data.type)) {
                playerData = data;
                break;
            }
        }

        assertNotNull(playerData);
        assertEquals(25, playerData.width);
        assertEquals(25, playerData.height);
        assertEquals("Black", playerData.borderType);
    }

    /**Debe permitir ajustar metricas desde persistencia y no debe bloquear valores validos.*/
    @Test
    public void shouldSetTimeAndDeaths() throws IOException {
        DOPOsHardestGame game = createSingleGame();

        game.setTimeLeft(12);
        game.setTotalDeaths(3);

        assertEquals(12, game.getTimeLeft());
        assertEquals(3, game.getTotalDeaths());
    }

    /**Debe guardar y cargar partida, y no debe hacerlo si no hay nivel o archivo valido.*/
    @Test
    public void shouldSaveAndLoadCurrentGameAndShouldNotWithInvalidState() throws IOException {
        DOPOsHardestGame game = createSingleGame("COIN 2 1 Yellow");
        Player player = game.getCurrentLevel().getPlayers().get(0);
        player.setX(60);
        player.collectCoin();
        game.setTimeLeft(14);
        game.setTotalDeaths(2);
        Path saveFile = Files.createTempFile("dopo-save", ".txt");

        assertTrue(game.saveCurrentGame(saveFile.toString()));
        player.setX(0);
        player.setCollectedCoins(0);
        game.setTimeLeft(1);
        game.setTotalDeaths(0);

        assertTrue(game.loadCurrentGame(saveFile.toString()));
        assertEquals(60, player.getX());
        assertEquals(1, player.getCollectedCoins());
        assertEquals(14, game.getTimeLeft());
        assertEquals(2, game.getTotalDeaths());

        DOPOsHardestGame emptyGame = new DOPOsHardestGame(new String[] {"missing-file.txt"});
        assertFalse(emptyGame.saveCurrentGame(saveFile.toString()));
        assertFalse(emptyGame.loadCurrentGame("missing-save.txt"));
    }

    /**Debe contar monedas solo para quien las recoge y no debe sumar al otro jugador.*/
    @Test
    public void shouldCountCoinForCollectorAndShouldNotCountItForBothPlayersInPvp() throws IOException {
        DOPOsHardestGame game = new DOPOsHardestGame(new String[] {createLevelFile("pvpCoins", 30, "COIN 1 1 Yellow")});
        game.setGameMode("PvP");
        Level level = game.getCurrentLevel();
        Player playerOne = level.getPlayers().get(0);
        Player playerTwo = level.getPlayers().get(1);
        Coin coin = level.getCoins().get(0);

        playerOne.setX(coin.getX());
        playerOne.setY(coin.getY());
        game.handlePlayerMovement(0, 0, 0);

        assertTrue(coin.isCollected());
        assertEquals(1, playerOne.getCollectedCoins());
        assertEquals(0, playerTwo.getCollectedCoins());
        assertEquals(1, level.getCollectedCoinCount());
    }

    /**Debe completar con monedas globales recogidas y no debe exigir el puntaje individual del jugador.*/
    @Test
    public void shouldCompleteWithBoardCoinsAndShouldNotRequireWinnerCollectedEveryCoin() throws IOException {
        DOPOsHardestGame game = new DOPOsHardestGame(new String[] {createLevelFile("pvpGoal", 30, "COIN 1 1 Yellow")});
        game.setGameMode("PvP");
        Level level = game.getCurrentLevel();
        Player playerOne = level.getPlayers().get(0);
        Player playerTwo = level.getPlayers().get(1);
        Coin coin = level.getCoins().get(0);

        playerOne.setX(coin.getX());
        playerOne.setY(coin.getY());
        game.handlePlayerMovement(0, 0, 0);
        playerTwo.setX(0);
        playerTwo.setY(40);
        game.handlePlayerMovement(1, 0, 0);

        assertEquals(0, playerTwo.getCollectedCoins());
        assertTrue(game.hasPendingVictory());
    }
}
