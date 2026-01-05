package com.interplanetarycrash.animation;

import javafx.scene.image.Image;

/**
 * Represents an animation with multiple frames
 */
public class Animation {
    
    private final Image[] frames;
    private final double frameDuration; // Duration of each frame in seconds
    private final boolean loop;
    
    private int currentFrame;
    private double elapsedTime;
    private boolean finished;
    
    /**
     * Create a new animation
     * @param frames Array of images for animation frames
     * @param frameDuration How long each frame lasts in seconds
     * @param loop Whether animation should loop
     */
    public Animation(Image[] frames, double frameDuration, boolean loop) {
        this.frames = frames;
        this.frameDuration = frameDuration;
        this.loop = loop;
        this.currentFrame = 0;
        this.elapsedTime = 0;
        this.finished = false;
    }
    
    /**
     * Update animation
     */
    public void update(double deltaTime) {
        if (finished && !loop) return;
        
        elapsedTime += deltaTime;
        
        // Check if we should advance to next frame
        while (elapsedTime >= frameDuration) {
            elapsedTime -= frameDuration;
            currentFrame++;
            
            // Handle loop or finish
            if (currentFrame >= frames.length) {
                if (loop) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.length - 1;
                    finished = true;
                    break;
                }
            }
        }
    }
    
    /**
     * Get current frame image
     */
    public Image getCurrentFrame() {
        return frames[currentFrame];
    }
    
    /**
     * Reset animation to first frame
     */
    public void reset() {
        currentFrame = 0;
        elapsedTime = 0;
        finished = false;
    }
    
    /**
     * Check if animation is finished (only meaningful for non-looping animations)
     */
    public boolean isFinished() {
        return finished;
    }
    
    /**
     * Get total duration of animation
     */
    public double getTotalDuration() {
        return frames.length * frameDuration;
    }
    
    /**
     * Get frame count
     */
    public int getFrameCount() {
        return frames.length;
    }
}
