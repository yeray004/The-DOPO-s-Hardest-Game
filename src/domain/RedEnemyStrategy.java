package domain;

/**Estrategia de emboscada: Se mueve rápido solo cuando el jugador está cerca.
 * @author Yeray Guacheta
 * @version 1.0*/
public class RedEnemyStrategy implements EnemyStrategy {
    private final int AGGRO_RANGE = 200; // Rango de visión en píxeles

    @Override
    public void updatePosition(Enemy enemy, Player target) {
        if (target == null) return;
        
        double distance = Math.hypot(enemy.x - target.getX(), enemy.y - target.getY());
        if (distance < AGGRO_RANGE) {
            // Acelera hacia el jugador si entra en su rango
            if (enemy.x < target.getX()) enemy.x += enemy.speed + 1;
            else if (enemy.x > target.getX()) enemy.x -= (enemy.speed + 1);
            
            if (enemy.y < target.getY()) enemy.y += enemy.speed + 1;
            else if (enemy.y > target.getY()) enemy.y -= (enemy.speed + 1);
        }
    }

    @Override public String getSpriteType() { return "Red"; }
    @Override public double getSizeMultiplier() { return 1.0; }
}