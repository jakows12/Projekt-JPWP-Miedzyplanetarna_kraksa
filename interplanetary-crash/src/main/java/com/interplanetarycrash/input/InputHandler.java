package com.interplanetarycrash.input;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Handles all keyboard input - KEYBOARD ONLY, NO MOUSE
 * Tracks key states and provides query methods
 */
public class InputHandler {
    
    // Key state tracking
    private final Set<KeyCode> keysPressed = new HashSet<>();
    private final Set<KeyCode> keysJustPressed = new HashSet<>();
    private final Set<KeyCode> keysJustReleased = new HashSet<>();
    
    /**
     * Update input state - called once per frame
     * Clears "just pressed/released" states
     */
    public void update() {
        keysJustPressed.clear();
        keysJustReleased.clear();
    }
    
    // ===== KEYBOARD HANDLERS =====
    
    public void handleKeyPressed(KeyEvent e) {
        KeyCode code = e.getCode();
        
        // DEBUG: 
        // System.out.println("Key pressed: " + code);
        
        if (!keysPressed.contains(code)) {
            keysJustPressed.add(code);
        }
        keysPressed.add(code);
    }
    
    public void handleKeyReleased(KeyEvent e) {
        KeyCode code = e.getCode();
        keysPressed.remove(code);
        keysJustReleased.add(code);
    }
    
    // ===== QUERY METHODS =====
    
    /**
     * Check if key is currently held down
     */
    public boolean isKeyPressed(KeyCode key) {
        return keysPressed.contains(key);
    }
    
    /**
     * Check if key was just pressed this frame
     */
    public boolean isKeyJustPressed(KeyCode key) {
        return keysJustPressed.contains(key);
    }
    
    /**
     * Check if key was just released this frame
     */
    public boolean isKeyJustReleased(KeyCode key) {
        return keysJustReleased.contains(key);
    }
    
    // ===== CONVENIENCE METHODS FOR GAME CONTROLS =====
    
    // WASD Movement
    public boolean isMovingUp() {
        return isKeyPressed(KeyCode.UP) || isKeyPressed(KeyCode.W);
    }
    
    public boolean isMovingDown() {
        return isKeyPressed(KeyCode.DOWN) || isKeyPressed(KeyCode.S);
    }
    
    public boolean isMovingLeft() {
        return isKeyPressed(KeyCode.LEFT) || isKeyPressed(KeyCode.A);
    }
    
    public boolean isMovingRight() {
        return isKeyPressed(KeyCode.RIGHT) || isKeyPressed(KeyCode.D);
    }
    
    // Interaction
    public boolean isInteracting() {
        return isKeyJustPressed(KeyCode.E);
    }
    
    // Menu navigation
    public boolean isPausing() {
        return isKeyJustPressed(KeyCode.SPACE);
    }
    
    public boolean isConfirming() {
        return isKeyJustPressed(KeyCode.ENTER);
    } 

    public boolean isExiting() {
        return isKeyJustPressed(KeyCode.ESCAPE);
    }
    
    // Arrow keys for menu navigation
    public boolean isNavigatingUp() {
        return isKeyJustPressed(KeyCode.UP) || isKeyJustPressed(KeyCode.W);
    }
    
    public boolean isNavigatingDown() {
        return isKeyJustPressed(KeyCode.DOWN) || isKeyJustPressed(KeyCode.S);
    }
    
    public boolean isNavigatingLeft() {
        return isKeyJustPressed(KeyCode.LEFT) || isKeyJustPressed(KeyCode.A);
    }
    
    public boolean isNavigatingRight() {
        return isKeyJustPressed(KeyCode.RIGHT) || isKeyJustPressed(KeyCode.D);
    }

    public boolean isSelectingRight() {
        return isKeyJustPressed(KeyCode.E);
    }

    public boolean isSelectingLeft() {
        return isKeyJustPressed(KeyCode.Q);
    }
}