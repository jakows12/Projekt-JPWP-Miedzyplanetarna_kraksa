package com.interplanetarycrash.tasks;

import com.interplanetarycrash.input.InputHandler;
import com.interplanetarycrash.rendering.GameRenderer;

/**
 * Base class for all tasks (puzzles that repair modules)
 * Tasks are loaded from text files in assets/tasks/
 */
public abstract class Task {
    
    protected TaskType type;
    protected String question;
    protected int difficulty;
    protected boolean completed;
    protected boolean correct; // Whether answer was correct
    
    public Task(TaskType type, String question, int difficulty) {
        this.type = type;
        this.question = question;
        this.difficulty = difficulty;
        this.completed = false;
        this.correct = false;
    }
    
    /**
     * Update task logic
     * @param deltaTime Time since last frame
     * @param input Input handler for reading keys
     */
    public abstract void update(double deltaTime, InputHandler input);
    
    /**
     * Render task UI
     */
    public abstract void render(GameRenderer renderer);
    
    /**
     * Check if player's answer is correct
     * Called when player confirms their answer
     */
    protected abstract boolean checkAnswer();
    
    /**
     * Submit answer and complete task
     */
    public void submitAnswer() {
        if (!completed) {
            correct = checkAnswer();
            completed = true;
        }
    }
    
    /**
     * Reset task to initial state
     */
    public void reset() {
        completed = false;
        correct = false;
    }
    
    // Getters
    public TaskType getType() { return type; }
    public String getQuestion() { return question; }
    public int getDifficulty() { return difficulty; }
    public boolean isCompleted() { return completed; }
    public boolean isCorrect() { return correct; }
}
