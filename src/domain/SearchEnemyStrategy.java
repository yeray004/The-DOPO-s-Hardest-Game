package domain;

/**Estrategia de busqueda: el elemento persigue directamente al jugador.
 * @author Yeray Guacheta
 * @version 1.1*/
public class SearchEnemyStrategy implements EnemyStrategy {
    @Override
    public void updatePosition(MovableElement element, Player target) {
        if (target == null) return;
        // Vector de persecución simple
        if (element.getX() < target.getX()) element.setX(element.getX() + element.getSpeed());
        else if (element.getX() > target.getX()) element.setX(element.getX() - element.getSpeed());
        
        if (element.getY() < target.getY()) element.setY(element.getY() + element.getSpeed());
        else if (element.getY() > target.getY()) element.setY(element.getY() - element.getSpeed());
    }

    @Override public String getSpriteType() { return "SearchEnemy"; }
    @Override public double getSizeMultiplier() { return 1.0; }
}
