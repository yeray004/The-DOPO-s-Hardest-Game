package test;

import domain.DOPOsHardestGame;
import presentation.GameFrame;
import org.junit.jupiter.api.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;

/**
 * Prueba de aceptación visual.
 * Demuestra la victoria cruzando el nivel de referencia, tomando la moneda y evadiendo al enemigo.
 * @author Yeray Guacheta
 * @support Assisted by Gemini (Google AI) - May 2026
 * @version 4.1
 */
public class GameAcceptanceTestEI {

    private final String TEST_FILE = "testLevelVisual.txt";
    private GameFrame frame;

    @BeforeEach
    public void setUp() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TEST_FILE))) {
            writer.println("22 9");
            writer.println("0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0");
            writer.println("0 0 0 0 0 1 2 1 2 1 2 1 2 1 2 1 2 0 0 0 0 0");
            writer.println("0 0 0 0 0 2 1 2 1 2 1 2 1 2 1 2 1 0 0 0 0 0");
            writer.println("0 3 3 3 2 1 2 1 2 1 2 1 2 1 2 1 2 1 4 4 4 0");
            writer.println("0 3 3 3 1 2 1 2 1 2 1 2 1 2 1 2 1 2 4 4 4 0");
            writer.println("0 0 0 0 0 1 2 1 2 1 2 1 2 1 2 1 2 0 0 0 0 0");
            writer.println("0 0 0 0 0 2 1 2 1 2 1 2 1 2 1 2 1 0 0 0 0 0");
            writer.println("0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0");
            writer.println("0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0");
            writer.println("PLAYER 2 4");
            writer.println("COIN 10 4 Yellow");
            writer.println("ENEMY BASIC BasicEnemy 6 3 2 1 0");
        }
    }

    @Test
    public void testVisualGameplay() throws Exception {
        DOPOsHardestGame game = new DOPOsHardestGame(new String[]{TEST_FILE});
        frame = new GameFrame(game);
        frame.setVisible(true);

        Robot robot = new Robot();
        robot.delay(3000);

        // Navegación automática en la interfaz
        clickButtonByText(frame, "MODO INDIVIDUAL");
        robot.delay(1500);

        clickButtonByText(frame, "INICIAR JUEGO");
        robot.delay(2000);

        // Simula la pulsación física para avanzar hacia la meta
        robot.keyPress(KeyEvent.VK_RIGHT);
        robot.delay(4000); 
        robot.keyRelease(KeyEvent.VK_RIGHT);

        robot.delay(2000); 
    }

    @AfterEach
    public void tearDown() {
        if (frame != null) frame.dispose();
        new File(TEST_FILE).delete();
    }

    private void clickButtonByText(Container container, String text) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                if (text.equals(btn.getText())) {
                    btn.doClick();
                    return;
                }
            } else if (comp instanceof Container) {
                clickButtonByText((Container) comp, text);
            }
        }
    }
}