package com.interplanetarycrash.core;

import com.interplanetarycrash.rendering.GameRenderer;
import com.interplanetarycrash.states.State;

/**
 * Manages game states and transitions between them
 */
public class StateManager {
    
    private final Game game;
    private State currentState;
    private State nextState;
    
    public StateManager(Game game) {
        this.game = game;
    }
    
    /**
     * Change to a new state
     */
    public void changeState(State newState) {
        this.nextState = newState;
    }
    
    /**
     * Update current state
     */
    public void update(double deltaTime) {
        // Handle state transition
        if (nextState != null) {
            if (currentState != null) {
                currentState.exit();
            }
            currentState = nextState;
            currentState.enter();
            nextState = null;
        }
        
        // Update current state
        if (currentState != null) {
            currentState.update(deltaTime);
        }
    }
    
    /**
     * Render current state
     */
    public void render(GameRenderer renderer) {
        if (currentState != null) {
            currentState.render(renderer);
        }
    }
    
    /**
     * Get current state
     */
    public State getCurrentState() {
        return currentState;
    }
    
    /**
     * Get game instance
     */
    public Game getGame() {
        return game;
    }
}

