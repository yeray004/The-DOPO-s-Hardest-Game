package domain;

public class ClydeState implements PlayerState {
    private boolean isDamaged = false;

    @Override
    public double getSpeedMultiplier() { 
        return isDamaged ? 0.7 : 1.0; 
    }

    @Override
    public double getSizeMultiplier() { 
        return 1.0; 
    }

    @Override
    public String getSpriteType() { 
        return isDamaged ? "ClydeDamaged" : "Clyde"; 
    }

    @Override
    public void handleDamage(Player player) {
        if (!isDamaged) {
            isDamaged = true; // Absorbe el primer golpe
        } else {
            player.die(); // Muere al segundo golpe
        }
    }
}