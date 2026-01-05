package com.interplanetarycrash.states;

import com.interplanetarycrash.core.Game;
import com.interplanetarycrash.level.Level;
import com.interplanetarycrash.level.Module;
import com.interplanetarycrash.rendering.GameRenderer;
import com.interplanetarycrash.tasks.Task;
import com.interplanetarycrash.ui.*;

/**
 * State when player is actively solving a task
 * Time continues to pass and life support drains!
 */
public class TaskActiveState extends State {
    
    private Level level;
    private Module module;
    private Task task;
    private Timer timer;
    private LifeSupportBar lifeSupportBar;
    private LevelPlayingState levelPlayingState;
    
    private boolean waitingForConfirm; // After completing, wait for ENTER
    
    public TaskActiveState(Game game, Level level, Module module, LevelPlayingState levelPlayingState, Timer timer, LifeSupportBar lifeSupportBar) {
        super(game);
        this.level = level;
        this.module = module;
        this.task = module.getTask();
        this.levelPlayingState = levelPlayingState;
        this.waitingForConfirm = false;
        this.timer = timer;
        this.lifeSupportBar = lifeSupportBar;
    }
    
    @Override
    public void enter() {
        System.out.println("Starting task: " + task.getType());
        
        if (task == null) {
            System.err.println("ERROR: Task is null!");
            // Return to playing state
            game.getStateManager().changeState(levelPlayingState);
        }
    }
    
    @Override
    public void exit() {
        System.out.println("Exiting task");
    }
    
    @Override
    public void update(double deltaTime) {
        // IMPORTANT: Level continues to update (time passes, life drains!)
        level.update(deltaTime);
        
        // Check if game over
        if (level.isGameOver()) {
            System.out.println("Game Over during task!");
            // TODO: Go to game over screen
            game.getStateManager().changeState(levelPlayingState);
            return;
        }
        
        // If task completed, wait for confirmation
        if (task.isCompleted()) {
            if (!waitingForConfirm) {
                waitingForConfirm = true;
                
                // If correct, repair the module
                if (task.isCorrect()) {
                    module.repair();
                    System.out.println("Module repaired!");
                    if(level.lifeSupport < 90.0) {
                        level.lifeSupport += 10.0; // Reward life support
                    } else level.lifeSupport = 100;
                } else {
                    System.out.println("Incorrect answer, module not repaired");
                    level.lifeSupport -= 10.0;
                }
            }
            
            // Wait for ENTER to continue
            if (game.getInputHandler().isConfirming()) {
                if (task.isCorrect()) {
                    game.getStateManager().changeState(levelPlayingState);
                } else {
                    task.reset();
                    waitingForConfirm = false;
                }
            }
            return;
        }
        
        // Update task
        task.update(deltaTime, game.getInputHandler());
    }
    
    @Override
    public void render(GameRenderer renderer) {
        // Render the task UI
        task.render(renderer);
        
        // Overlay life support and time (always visible)
        lifeSupportBar.render(renderer, level.getLifeSupportPercentage());
        timer.render(renderer, level.getElapsedTime());
    }
}