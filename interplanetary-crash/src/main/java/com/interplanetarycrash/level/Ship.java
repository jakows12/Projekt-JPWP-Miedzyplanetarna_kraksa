package com.interplanetarycrash.level;

import com.interplanetarycrash.animation.AnimatedSprite;
import com.interplanetarycrash.animation.Animation;
import com.interplanetarycrash.assets.AssetManager;
import com.interplanetarycrash.rendering.GameRenderer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Crashed spaceship that player needs to repair and escape with
 */
public class Ship {
    
    private double x, y; // Center position
    private List<Module> modules;
    
    private Image sprite;
    private AnimatedSprite animatedSprite;
    
    private static final double SHIP_RADIUS = 512;
    private static final double ESCAPE_RADIUS = 300; // Distance to press E to escape
    public Ship(double x, double y, List<Module> modules) {
        this.x = x;
        this.y = y;
        this.modules = modules;
        
        loadAssets();
    }
    
    /**
     * Load ship sprite
     */
    private void loadAssets() {
        animatedSprite = new AnimatedSprite();
        AssetManager assets = AssetManager.getInstance();

        Animation starshipDestroyed = new Animation(
            assets.getAnimationFrames("Starship-Destroyed", 6), 
            0.15, 
            true);
        animatedSprite.addAnimation("starship_destroyed", starshipDestroyed);

        Animation starshipBurning = new Animation(
            assets.getAnimationFrames("Starship-Burning", 6), 
            0.15, 
            true);
        animatedSprite.addAnimation("starship_burning", starshipBurning);

        Animation starshipRepaired = new Animation(
            assets.getAnimationFrames("Sprite-Starship-Repaired", 1), 
            0.15, 
            true);
        animatedSprite.addAnimation("starship_repaired", starshipRepaired);

        Animation wingDamaged = new Animation(
            assets.getAnimationFrames("Sprite-Wing-Damaged", 1), 
            0.15, 
            true);
        animatedSprite.addAnimation("wing_damaged", wingDamaged);

        animatedSprite.setAnimation("starship_destroyed");
    }
    
    /**
     * Render ship
     */
    public void render(GameRenderer renderer) {
        Image currentFrame = animatedSprite.getCurrentFrame();
        if (currentFrame != null) {
            // Center sprite on position
            double renderX = x - SHIP_RADIUS / 2;
            double renderY = y - SHIP_RADIUS / 2;
            renderer.drawImage(currentFrame, renderX, renderY, SHIP_RADIUS, SHIP_RADIUS);
        }
    }

    public void update(double deltaTime) {
        // Update ship animation based on module states
        if (modules.get(2).isRepaired() && modules.get(3).isRepaired()) {
            animatedSprite.setAnimation("starship_repaired");
        } else {
            if(modules.get(2).isRepaired() && !modules.get(3).isRepaired()) { // Assuming module index 2 is the engine
                animatedSprite.setAnimation("starship_burning");
            } else if (!modules.get(2).isRepaired() && modules.get(3).isRepaired()) { // Assuming module index 3 is the wing
                animatedSprite.setAnimation("wing_damaged");
            } else {
                animatedSprite.setAnimation("starship_destroyed");
            }
        }
        
        animatedSprite.update(deltaTime);
    }
    
    /**
     * Check if all modules are repaired
     */
    public boolean canEscape() {
        for (Module module : modules) {
            if (!module.isRepaired()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if player is in range to escape
     */
    public boolean isPlayerInEscapeRange(double playerX, double playerY) {
        double dx = x - playerX;
        double dy = y - playerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= ESCAPE_RADIUS;
    }
    
    /**
     * Get collision bounds
     */
    public Rectangle2D getBounds() {
        // Collision box slightly smaller than visual sprite
        double collisionSize = SHIP_RADIUS * 0.5;
        return new Rectangle2D(
            x - collisionSize / 2,
            y - collisionSize / 2,
            collisionSize,
            collisionSize
        );
    }
    
    // Getters
    public double getX() { return x - SHIP_RADIUS / 2; }
    public double getY() { return y - SHIP_RADIUS / 2; }
    public double getEscapeRadius() { return ESCAPE_RADIUS; }
}
