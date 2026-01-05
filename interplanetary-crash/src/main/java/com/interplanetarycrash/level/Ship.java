package com.interplanetarycrash.level;

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
    
    private static final double SHIP_WIDTH = 256;
    private static final double SHIP_HEIGHT = 256;
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
        AssetManager assets = AssetManager.getInstance();
        sprite = assets.getSprite("ship");
    }
    
    /**
     * Render ship
     */
    public void render(GameRenderer renderer) {
        double renderX = x - SHIP_WIDTH / 2;
        double renderY = y - SHIP_HEIGHT / 2;
        renderer.drawImage(sprite, renderX, renderY, SHIP_WIDTH, SHIP_HEIGHT);
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
        double collisionSize = 180;
        return new Rectangle2D(
            x - collisionSize / 2,
            y - collisionSize / 2,
            collisionSize,
            collisionSize
        );
    }
    
    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getEscapeRadius() { return ESCAPE_RADIUS; }
}
