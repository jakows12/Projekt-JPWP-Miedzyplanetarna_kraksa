package com.interplanetarycrash.states;

import com.interplanetarycrash.core.Game;
import com.interplanetarycrash.rendering.GameRenderer;

/**
 * Base interface for all game states
 * Each state represents a different screen/mode of the game
 */
public abstract class State {
    
    protected final Game game;
    
    public State(Game game) {
        this.game = game;
    }
    
    /**
     * Called when entering this state
     */
    public abstract void enter();
    
    /**
     * Called when exiting this state
     */
    public abstract void exit();
    
    /**
    Update state logic
    @param deltaTime Time since last frame in seconds
    */
    public abstract void update(double deltaTime);
    
    /**
     * Render state
     * @param renderer The game renderer
     */
    public abstract void render(GameRenderer renderer);
}
