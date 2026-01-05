package com.interplanetarycrash.tasks;

import com.interplanetarycrash.rendering.GameRenderer;

/**
 * Base class for all tasks (puzzles that repair modules)
 * TODO: Implement task system in next phase
 */
public abstract class Task {
    
    protected String question;
    protected int difficulty;
    protected boolean completed;
    
    public Task(String question, int difficulty) {
        this.question = question;
        this.difficulty = difficulty;
        this.completed = false;
    }
    
    /**
     * Update task logic
     */
    public abstract void update(double deltaTime);
    
    /**
     * Render task UI
     */
    public abstract void render(GameRenderer renderer);
    
    /**
     * Check if task is completed
     */
    public boolean isCompleted() {
        return completed;
    }
    
    /**
     * Reset task
     */
    public void reset() {
        completed = false;
    }
    
    public String getQuestion() { return question; }
    public int getDifficulty() { return difficulty; }
}
