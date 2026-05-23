package domain;

/**Estrategia de emboscada: se mueve rapido solo cuando el jugador esta cerca.
 * @author Yeray Guacheta
 * @version 1.1*/
public class AmbushEnemyStrategy implements EnemyStrategy {
    private final int AGGRO_RANGE = 200; // Rango de visión en píxeles

    @Override
    public void updatePosition(MovableElement element, Player target) {
        if (target == null) return;
        
        double distance = Math.hypot(element.getX() - target.getX(), element.getY() - target.getY());
        if (distance < AGGRO_RANGE) {
            // Acelera hacia el jugador si entra en su rango
            int boostedSpeed = element.getSpeed() + 1;
            if (element.getX() < target.getX()) element.setX(element.getX() + boostedSpeed);
            else if (element.getX() > target.getX()) element.setX(element.getX() - boostedSpeed);
            
            if (element.getY() < target.getY()) element.setY(element.getY() + boostedSpeed);
            else if (element.getY() > target.getY()) element.setY(element.getY() - boostedSpeed);
        }
    }

    @Override public String getSpriteType() { return "AmbushEnemy"; }
    @Override public double getSizeMultiplier() { return 1.0; }
}
