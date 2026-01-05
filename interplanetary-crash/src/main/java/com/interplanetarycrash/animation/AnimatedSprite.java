package com.interplanetarycrash.animation;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages multiple animations for a single sprite
 * Allows switching between different animation states
 */
public class AnimatedSprite {
    
    private final Map<String, Animation> animations;
    private String currentAnimationName;
    private Animation currentAnimation;
    
    public AnimatedSprite() {
        this.animations = new HashMap<>();
    }
    
    /**
     * Add an animation to this sprite
     */
    public void addAnimation(String name, Animation animation) {
        animations.put(name, animation);
        
        // Set as current if this is the first animation
        if (currentAnimation == null) {
            setAnimation(name);
        }
    }
    
    /**
     * Switch to a different animation
     */
    public void setAnimation(String name) {
        if (name.equals(currentAnimationName)) {
            return; // Already playing this animation
        }
        
        Animation newAnimation = animations.get(name);
        if (newAnimation == null) {
            System.err.println("Animation not found: " + name);
            return;
        }
        
        currentAnimationName = name;
        currentAnimation = newAnimation;
        currentAnimation.reset();
    }
    
    /**
     * Update current animation
     */
    public void update(double deltaTime) {
        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);
        }
    }
    
    /**
     * Get current frame to render
     */
    public Image getCurrentFrame() {
        if (currentAnimation != null) {
            return currentAnimation.getCurrentFrame();
        }
        return null;
    }
    
    /**
     * Get current animation name
     */
    public String getCurrentAnimationName() {
        return currentAnimationName;
    }
    
    /**
     * Check if current animation is finished
     */
    public boolean isCurrentAnimationFinished() {
        return currentAnimation != null && currentAnimation.isFinished();
    }
    
    /**
     * Reset current animation
     */
    public void resetCurrentAnimation() {
        if (currentAnimation != null) {
            currentAnimation.reset();
        }
    }
}