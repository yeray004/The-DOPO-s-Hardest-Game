package domain;

/**Estrategia de movimiento lineal constante para un elemento movil.*/
public class LinearEnemyStrategy implements EnemyStrategy {
    @Override
    public void updatePosition(MovableElement element, Player target) {
        element.setX(element.getX() + element.getDx() * element.getSpeed());
        element.setY(element.getY() + element.getDy() * element.getSpeed());
    }

    @Override
    public String getSpriteType() { return "LinearEnemy"; }

    @Override
    public double getSizeMultiplier() { return 1.0; }
}
