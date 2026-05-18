package domain;
import java.awt.Point;

/**Estrategia de patrullaje: El enemigo sigue una lista de coordenadas.
 * @author Yeray Guacheta
 * @version 1.0*/
public class OrangeEnemyStrategy implements EnemyStrategy {
    private int targetIndex = 0;

    @Override
    public void updatePosition(Enemy enemy, Player target) {
        if (enemy.movement == null || enemy.movement.isEmpty()) return;
        Point dest = enemy.movement.get(targetIndex);
        
        if (enemy.x < dest.x) enemy.x += enemy.speed;
        else if (enemy.x > dest.x) enemy.x -= enemy.speed;
        
        if (enemy.y < dest.y) enemy.y += enemy.speed;
        else if (enemy.y > dest.y) enemy.y -= enemy.speed;

        // Si llega al punto, avanza al siguiente en la lista
        if (Math.abs(enemy.x - dest.x) <= enemy.speed && Math.abs(enemy.y - dest.y) <= enemy.speed) {
            targetIndex = (targetIndex + 1) % enemy.movement.size();
        }
    }

    @Override public String getSpriteType() { return "Orange"; }
    @Override public double getSizeMultiplier() { return 1.0; }
}