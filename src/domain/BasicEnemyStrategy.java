package domain;

/**Estrategia de movimiento básico en línea recta para el enemigo.*/
public class BasicEnemyStrategy implements EnemyStrategy {
    @Override
    public void updatePosition(Enemy enemy, Player target) {
        enemy.x += enemy.dx * enemy.speed;
        enemy.y += enemy.dy * enemy.speed;
    }

    @Override
    public String getSpriteType() { return "BasicEnemy"; }

    @Override
    public double getSizeMultiplier() { return 1.0; }
}