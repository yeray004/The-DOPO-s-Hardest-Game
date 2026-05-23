package domain;
import java.awt.Point;
import java.util.List;

/**Estrategia de patrullaje: el elemento sigue una lista de coordenadas.
 * @author Yeray Guacheta
 * @version 1.1*/
public class PatrolEnemyStrategy implements EnemyStrategy {
    private int targetIndex = 0;

    @Override
    public void updatePosition(MovableElement element, Player target) {
        List<Point> movement = element.getMovementRoute();
        if (movement == null || movement.isEmpty()) return;
        Point dest = movement.get(targetIndex);
        
        if (element.getX() < dest.x) element.setX(element.getX() + element.getSpeed());
        else if (element.getX() > dest.x) element.setX(element.getX() - element.getSpeed());
        
        if (element.getY() < dest.y) element.setY(element.getY() + element.getSpeed());
        else if (element.getY() > dest.y) element.setY(element.getY() - element.getSpeed());

        // Si llega al punto, avanza al siguiente en la lista
        if (Math.abs(element.getX() - dest.x) <= element.getSpeed()
                && Math.abs(element.getY() - dest.y) <= element.getSpeed()) {
            targetIndex = (targetIndex + 1) % movement.size();
        }
    }

    @Override public String getSpriteType() { return "PatrolEnemy"; }
    @Override public double getSizeMultiplier() { return 1.0; }
}
