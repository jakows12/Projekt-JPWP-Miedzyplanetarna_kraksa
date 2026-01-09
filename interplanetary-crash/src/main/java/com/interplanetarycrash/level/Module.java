package com.interplanetarycrash.level;

import com.interplanetarycrash.animation.AnimatedSprite;
import com.interplanetarycrash.animation.Animation;
import com.interplanetarycrash.assets.AssetManager;
import com.interplanetarycrash.rendering.GameRenderer;
import com.interplanetarycrash.level.ModuleType.*;
import com.interplanetarycrash.tasks.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

/**
 * Ship module that needs to be repaired
 * Has fire animation when broken
 */
public class Module {
    
    private ModuleType type;
    private double x, y; // Position
    private boolean repaired;
    
    private Task task; // Task to repair this module
    
    private Image brokenSprite;
    private Image repairedSprite;
    private AnimatedSprite animatedSprite;
    
    // Interaction and collision
    private static final double MODULE_SIZE = 192;
    private static final double INTERACTION_RADIUS = 210; // Distance to press E
    
    
    public Module(ModuleType type, double x, double y, Task task) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.task = task;
        this.repaired = false;
        
        loadAssets();
    }
    
    /**
     * Load sprites and animations
     */
    private void loadAssets() {
        animatedSprite = new AnimatedSprite();
        AssetManager assets = AssetManager.getInstance();
        
        Animation commsDestroyed = new Animation(
            assets.getAnimationFrames("Comms-Destroyed", 4), 
            0.15, 
            true);
        animatedSprite.addAnimation("comms_destroyed", commsDestroyed);

        Animation commsRepaired = new Animation(
            assets.getAnimationFrames("Comms-Repaired", 8), 
            0.15, 
            true);
        animatedSprite.addAnimation("comms_repaired", commsRepaired);

        Animation serversDestroyed = new Animation(
            assets.getAnimationFrames("Servers-Destroyed", 5), 
            0.15, 
            true);
        animatedSprite.addAnimation("servers_destroyed", serversDestroyed);

        Animation serversRepaired = new Animation(
            assets.getAnimationFrames("Servers-Repaired", 7), 
            0.15, 
            true);
        animatedSprite.addAnimation("servers_repaired", serversRepaired);

        if (type != ModuleType.ENGINE && type != ModuleType.WING) {
                animatedSprite.setAnimation(type.getProperName() + "_destroyed");
        }
    }
    
    /**
     * Update module (mainly fire animation)
     */
    public void update(double deltaTime) {
        animatedSprite.update(deltaTime);
    }
    
    /**
     * Render module
     */
    public void render(GameRenderer renderer) {
        // Render appropriate sprite
        if (type != ModuleType.ENGINE && type != ModuleType.WING) {
            if (repaired) {
                animatedSprite.setAnimation(type.getProperName() + "_repaired");
            } else {
                animatedSprite.setAnimation(type.getProperName() + "_destroyed");
            }
            Image currentFrame = animatedSprite.getCurrentFrame();
            double renderX = x - MODULE_SIZE / 2;
            double renderY = y - MODULE_SIZE / 2;
            renderer.drawImage(currentFrame, renderX, renderY, MODULE_SIZE, MODULE_SIZE);
        }
    }
    
    /**
     * Check if player is in range to interact
     */
    public boolean isPlayerInRange(double playerX, double playerY) {
        double dx = x - playerX;
        double dy = y - playerY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= INTERACTION_RADIUS;
    }
    
    /**
     * Get collision bounds
     */
    public Rectangle2D getBounds() {
        return new Rectangle2D(
            x - MODULE_SIZE / 2,
            y - MODULE_SIZE / 2,
            MODULE_SIZE,
            MODULE_SIZE
        );
    }
    
    /**
     * Repair this module
     */
    public void repair() {
        this.repaired = true;
    }
    
    // Getters and setters
    public ModuleType getType() { return type; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isRepaired() { return repaired; }
    public Task getTask() { return task; }
    public double getInteractionRadius() { return INTERACTION_RADIUS; }
}
