package domain;

public class BlinkyState implements PlayerState {
    @Override
    public double getSpeedMultiplier() { return 1.0; }

    @Override
    public double getSizeMultiplier() { return 1.0; }

    @Override
    public String getSpriteType() { return "Blinky"; }

    @Override
    public void handleDamage(Player player) {
        player.die(); // Blinky muere al primer toque
    }
}