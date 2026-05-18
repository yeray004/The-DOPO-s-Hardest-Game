package domain;

/**Estrategia de búsqueda: El enemigo persigue directamente al jugador.
 * @author Yeray Guacheta
 * @version 1.0*/
public class PinkEnemyStrategy implements EnemyStrategy {
    @Override
    public void updatePosition(Enemy enemy, Player target) {
        if (target == null) return;
        // Vector de persecución simple
        if (enemy.x < target.getX()) enemy.x += enemy.speed;
        else if (enemy.x > target.getX()) enemy.x -= enemy.speed;
        
        if (enemy.y < target.getY()) enemy.y += enemy.speed;
        else if (enemy.y > target.getY()) enemy.y -= enemy.speed;
    }

    @Override public String getSpriteType() { return "Pink"; }
    @Override public double getSizeMultiplier() { return 1.0; }
}