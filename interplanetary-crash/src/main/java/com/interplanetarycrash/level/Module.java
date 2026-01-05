package com.interplanetarycrash.level;

import com.interplanetarycrash.animation.Animation;
import com.interplanetarycrash.assets.AssetManager;
import com.interplanetarycrash.rendering.GameRenderer;
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
    private Animation fireAnimation;
    
    // Interaction and collision
    private static final double MODULE_SIZE = 96;
    private static final double INTERACTION_RADIUS = 80; // Distance to press E
    
    // Fire animation offset (render above module)
    private static final double FIRE_OFFSET_X = 16;
    private static final double FIRE_OFFSET_Y = -20;
    
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
        AssetManager assets = AssetManager.getInstance();
        
        // Load module sprites based on type
        String typeName = type.name().toLowerCase();
        brokenSprite = assets.getSprite(typeName + "_broken");
        repairedSprite = assets.getSprite(typeName + "_repaired");
        
        // Load fire animation (4 frames, 0.1s each)
        Image[] fireFrames = assets.getAnimationFrames("fire", 4);
        fireAnimation = new Animation(fireFrames, 0.1, true);
    }
    
    /**
     * Update module (mainly fire animation)
     */
    public void update(double deltaTime) {
        if (!repaired) {
            fireAnimation.update(deltaTime);
        }
    }
    
    /**
     * Render module
     */
    public void render(GameRenderer renderer) {
        // Render appropriate sprite
        Image sprite = repaired ? repairedSprite : brokenSprite;
        double renderX = x - MODULE_SIZE / 2;
        double renderY = y - MODULE_SIZE / 2;
        renderer.drawImage(sprite, renderX, renderY, MODULE_SIZE, MODULE_SIZE);
        
        // Render fire animation if not repaired
        if (!repaired) {
            Image fireFrame = fireAnimation.getCurrentFrame();
            if (fireFrame != null) {
                renderer.drawImage(
                    fireFrame,
                    x - 32 + FIRE_OFFSET_X,
                    y - 32 + FIRE_OFFSET_Y,
                    64,
                    64
                );
            }
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
