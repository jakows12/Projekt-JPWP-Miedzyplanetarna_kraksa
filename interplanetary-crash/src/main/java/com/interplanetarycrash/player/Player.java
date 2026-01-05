package com.interplanetarycrash.player;

import com.interplanetarycrash.animation.AnimatedSprite;
import com.interplanetarycrash.animation.Animation;
import com.interplanetarycrash.assets.AssetManager;
import com.interplanetarycrash.rendering.GameRenderer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

/**
 * Player character with WASD movement and walking animation
 */
public class Player {
    
    private double x, y; // Position
    private double speed = 200.0; // Pixels per second
    
    private Direction currentDirection;
    private boolean isMoving;
    
    private AnimatedSprite animatedSprite;
    
    // Collision box
    private static final double HITBOX_WIDTH = 16;
    private static final double HITBOX_HEIGHT = 16;
    private static final double SPRITE_WIDTH = 32;
    private static final double SPRITE_HEIGHT = 32;
    
    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.currentDirection = Direction.DOWN;
        this.isMoving = false;
        
        initializeAnimations();
    }
    
    /**
     * Initialize all player animations
     */
    private void initializeAnimations() {
        animatedSprite = new AnimatedSprite();
        AssetManager assets = AssetManager.getInstance();
        
        // Idle animation (just one frame, but in Animation format)
        Image[] idleFrames = { assets.getSprite("player_idle") };
        Animation idleAnim = new Animation(idleFrames, 1.0, true);
        animatedSprite.addAnimation("idle", idleAnim);
        
        // Walking animations (4 frames each, 0.15s per frame = ~7fps for retro look)
        Animation walk = new Animation(
            assets.getAnimationFrames("player_walk", 2),
            0.15,
            true
        );
        animatedSprite.addAnimation("walk", walk);
        
        // Start with idle
        animatedSprite.setAnimation("idle");
    }
    
    /**
     * Update player position and animation
     */
    public void update(double deltaTime, Direction moveDirection) {
        isMoving = moveDirection != Direction.NONE;
        
        if (isMoving) {
            currentDirection = moveDirection;
            
            // Update position based on direction
            switch (moveDirection) {
                case UP:
                    y -= speed * deltaTime;
                    animatedSprite.setAnimation("walk_up");
                    break;
                case DOWN:
                    y += speed * deltaTime;
                    animatedSprite.setAnimation("walk_down");
                    break;
                case LEFT:
                    x -= speed * deltaTime;
                    animatedSprite.setAnimation("walk_left");
                    break;
                case RIGHT:
                    x += speed * deltaTime;
                    animatedSprite.setAnimation("walk_right");
                    break;
            }
        } else {
            animatedSprite.setAnimation("idle");
        }
        
        // Update animation
        animatedSprite.update(deltaTime);
    }
    
    /**
     * Render player
     */
    public void render(GameRenderer renderer) {
        Image currentFrame = animatedSprite.getCurrentFrame();
        if (currentFrame != null) {
            // Center sprite on position
            double renderX = x - SPRITE_WIDTH / 2;
            double renderY = y - SPRITE_HEIGHT / 2;
            renderer.drawImage(currentFrame, renderX, renderY, SPRITE_WIDTH, SPRITE_HEIGHT);
        }
    }
    
    /**
     * Get collision bounds for the player
     */
    public Rectangle2D getBounds() {
        // Hitbox is smaller than sprite for better gameplay feel
        double hitboxX = x - HITBOX_WIDTH / 2;
        double hitboxY = y - HITBOX_HEIGHT / 2;
        return new Rectangle2D(hitboxX, hitboxY, HITBOX_WIDTH, HITBOX_HEIGHT);
    }
    
    /**
     * Set position (used for collision resolution)
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Check if player can interact with something at given position
     */
    public boolean canInteractWith(double targetX, double targetY, double radius) {
        double dx = x - targetX;
        double dy = y - targetY;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= radius;
    }
    
    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public Direction getCurrentDirection() { return currentDirection; }
    public boolean isMoving() { return isMoving; }
}