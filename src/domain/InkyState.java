package domain;

public class InkyState implements PlayerState {
    @Override
    public double getSpeedMultiplier() { return 1.5; }

    @Override
    public double getSizeMultiplier() { return 1.5; }

    @Override
    public String getSpriteType() { return "Inky"; }

    @Override
    public void handleDamage(Player player) {
        player.die();
    }
}