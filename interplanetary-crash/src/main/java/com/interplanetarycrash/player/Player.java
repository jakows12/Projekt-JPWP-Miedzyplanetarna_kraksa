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
    private boolean isDead = false;
    
    private AnimatedSprite animatedSprite;
    
    // Collision box
    private static final double HITBOX_WIDTH = 16;
    private static final double HITBOX_HEIGHT = 16;
    private static final double SPRITE_WIDTH = 64;
    private static final double SPRITE_HEIGHT = 64;
    
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
        
        Animation idle_left = new Animation(
            assets.getAnimationFrames("Astronaut-Idle-Left", 2), 
            0.15, 
            true);

        Animation idle_right = new Animation(
            assets.getAnimationFrames("Astronaut-Idle-Right", 2), 
            0.15, 
            true);

        animatedSprite.addAnimation("idle_left", idle_left);
        animatedSprite.addAnimation("idle_right", idle_right);
        
        Animation walk_left = new Animation(
            assets.getAnimationFrames("Astronaut-Walking-Left", 2),
            0.15,
            true
        );

        Animation walk_right = new Animation(
            assets.getAnimationFrames("Astronaut-Walking-Right", 2),
            0.15,
            true
        );

        animatedSprite.addAnimation("walk_left", walk_left);
        animatedSprite.addAnimation("walk_right", walk_right);

        Animation death_right = new Animation(
            assets.getAnimationFrames("Astronaut-Death-Right", 13),
            0.15,
            false
        );

        Animation death_left = new Animation(
            assets.getAnimationFrames("Astronaut-Death-Left", 13),
            0.15,
            false
        );

        animatedSprite.addAnimation("death_right", death_right);
        animatedSprite.addAnimation("death_left", death_left);    
        
        animatedSprite.setAnimation("idle_right");
    }
    
    /**
     * Update player position and animation
     */
    public void update(double deltaTime, Direction moveDirection) {
        isMoving = moveDirection != Direction.NONE_RIGHT && moveDirection != Direction.NONE_LEFT;
        if(!isDead) {
            // Normal movement
            if (isMoving) {
                currentDirection = moveDirection;
                
                // Update position based on direction
                switch (moveDirection) {
                    case UP:
                        y -= speed * deltaTime;
                        animatedSprite.setAnimation("walk_right");
                        break;
                    case DOWN:
                        y += speed * deltaTime;
                        animatedSprite.setAnimation("walk_left");
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
                switch (moveDirection) {
                    case NONE_LEFT:
                        animatedSprite.setAnimation("idle_left");
                        break;
                    case NONE_RIGHT:
                        animatedSprite.setAnimation("idle_right");
                        break;
                }
            }
        } else {
            // Death animation
            if (currentDirection == Direction.LEFT || currentDirection == Direction.NONE_LEFT || currentDirection == Direction.DOWN) {
                animatedSprite.setAnimation("death_left");
            } else {
                animatedSprite.setAnimation("death_right");
            }
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

    public void setDead() {
        isDead = true;
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