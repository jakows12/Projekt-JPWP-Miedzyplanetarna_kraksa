package com.interplanetarycrash.states;

import com.interplanetarycrash.GameApplication;
import com.interplanetarycrash.core.Game;
import com.interplanetarycrash.level.Level;
import com.interplanetarycrash.level.Module;
import com.interplanetarycrash.player.Direction;
import com.interplanetarycrash.player.Player;
import com.interplanetarycrash.rendering.GameRenderer;
import com.interplanetarycrash.ui.InteractionPrompt;
import com.interplanetarycrash.ui.LifeSupportBar;
import com.interplanetarycrash.ui.Timer;
import com.interplanetarycrash.utils.CollisionDetector;
import javafx.geometry.Rectangle2D;

/**
 * Main gameplay state - player moving around, repairing modules
 */
public class LevelPlayingState extends State {
    
    private Level level;
    private LifeSupportBar lifeSupportBar;
    private Timer timer;
    private InteractionPrompt interactionPrompt;
    
    public LevelPlayingState(Game game, int levelNumber) {
        super(game);
        this.level = new Level(levelNumber);
    }
    
    @Override
    public void enter() {
        System.out.println("Entering Level " + level.getLevelNumber());
        
        // Initialize UI
        lifeSupportBar = new LifeSupportBar( 30, 30, 350, 30);
        timer = new Timer(GameApplication.LOGICAL_WIDTH - 170, 40);
        interactionPrompt = new InteractionPrompt();
    }
    
    @Override
    public void exit() {
        System.out.println("Exiting Level " + level.getLevelNumber());
    }
    
    @Override
    public void update(double deltaTime) {
        // Check for pause
        if (game.getInputHandler().isPausing()) {
            //game.getStateManager().changeState(new PauseMenuState(game, level, this));
            return;
        }
        
        // Check game over
        if (level.isGameOver()) {
            System.out.println("Game Over - Life support depleted!");
            // TODO: Go to game over screen
            return;
        }
        
        // Update level (time, life support drain, module animations)
        level.update(deltaTime);
        
        // Handle player movement
        updatePlayerMovement(deltaTime);
        
        // Check interactions
        updateInteractions();
        
        // Update UI
        interactionPrompt.update(deltaTime);
    }
    
    /**
     * Handle player movement with WASD
     */
    private void updatePlayerMovement(double deltaTime) {
        Player player = level.getPlayer();
        
        // Determine movement direction
        Direction moveDirection = Direction.NONE;
        
        if (game.getInputHandler().isMovingUp()) {
            moveDirection = Direction.UP;
        } else if (game.getInputHandler().isMovingDown()) {
            moveDirection = Direction.DOWN;
        } else if (game.getInputHandler().isMovingLeft()) {
            moveDirection = Direction.LEFT;
        } else if (game.getInputHandler().isMovingRight()) {
            moveDirection = Direction.RIGHT;
        }
        
        // Save old position
        double oldX = player.getX();
        double oldY = player.getY();
        
        // Move player
        player.update(deltaTime, moveDirection);
        
        // Check collisions and resolve
        if (checkCollisions()) {
            // Collision detected, revert movement
            player.setPosition(oldX, oldY);
        }
    }
    
    /**
     * Check if player collides with anything
     */
    private boolean checkCollisions() {
        Player player = level.getPlayer();
        Rectangle2D playerBounds = player.getBounds();
        
        // Check collision with ship
        if (CollisionDetector.checkCollision(playerBounds, level.getShip().getBounds())) {
            return true;
        }
        
        // Check collision with modules
        for (Module module : level.getModules()) {
            if (CollisionDetector.checkCollision(playerBounds, module.getBounds())) {
                return true;
            }
        }
        
        // Check map bounds
        if (player.getX() < 50 || player.getX() > GameApplication.LOGICAL_WIDTH - 50 ||
            player.getY() < 50 || player.getY() > GameApplication.LOGICAL_HEIGHT - 50) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check for possible interactions (modules, ship escape)
     */
    private void updateInteractions() {
        Player player = level.getPlayer();
        
        // Check if can escape
        if (level.canPlayerEscape()) {
            interactionPrompt.show(
                "Naciśnij E aby uciec! / Press E to escape!",
                player.getX(),
                player.getY() - 80
            );
            
            // Handle escape
            if (game.getInputHandler().isInteracting()) {
                initiateEscape();
            }
            return;
        }
        
        // Check for nearby module to repair
        Module nearbyModule = level.getInteractableModule();
        if (nearbyModule != null) {
            interactionPrompt.show(
                "Naciśnij E aby naprawić / Press E to repair",
                player.getX(),
                player.getY() - 80
            );
            
            // Handle interaction
            if (game.getInputHandler().isInteracting()) {
                startModuleRepair(nearbyModule);
            }
        } else {
            interactionPrompt.hide();
        }
    }
    
    /**
     * Start repairing a module (enter task state)
     */
    private void startModuleRepair(Module module) {
        System.out.println("Starting repair of " + module.getType().getDisplayName());
        
        // Check if module has a task
        if (module.getTask() == null) {
            System.err.println("ERROR: Module has no task!");
            return;
        }
        // Change to TaskActiveState
        game.getStateManager().changeState(new TaskActiveState(game, level, module, this, timer, lifeSupportBar));
    }
    
    /**
     * Initiate escape sequence
     */
    private void initiateEscape() {
        System.out.println("Initiating escape sequence!");
        level.complete();
        
        game.getStateManager().changeState(new EscapeSequenceState(game, level));
    }
    
    @Override
    public void render(GameRenderer renderer) {
        // Render background
        if (level.getBackground() != null) {
            renderer.drawImage(level.getBackground(), 0, 0, 
                             GameApplication.LOGICAL_WIDTH, 
                             GameApplication.LOGICAL_HEIGHT);
        }
        
        // Render ship
        level.getShip().render(renderer);
        
        // Render modules
        for (Module module : level.getModules()) {
            module.render(renderer);
        }
        
        // Render player
        level.getPlayer().render(renderer);
        
        // Render UI
        lifeSupportBar.render(renderer, level.getLifeSupportPercentage());
        timer.render(renderer, level.getElapsedTime());
        interactionPrompt.render(renderer);
        
        // Render level info
        renderLevelInfo(renderer);
        
        // Render controls hint
        renderControlsHint(renderer);
    }
    
    /**
     * Render level information
     */
    private void renderLevelInfo(GameRenderer renderer) {
        // Level number and modules repaired
        String info = String.format("Level %d | Modules: %d/%d", 
            level.getLevelNumber(),
            level.getRepairedModulesCount(),
            level.getRequiredModules()
        );
        
        renderer.drawText(
            info,
            GameApplication.LOGICAL_WIDTH / 2 - 100,
            60,
            game.getAssetManager().getFont("retro"),
            GameRenderer.RETRO_GREEN
        );
    }
    
    /**
     * Render controls hint
     */
    private void renderControlsHint(GameRenderer renderer) {
        String hint = "↑↓ or WASD: Move  |  E: Interact  |  SPACE: Pause";
        renderer.drawCenteredText(
            hint,
            GameApplication.LOGICAL_WIDTH / 2,
            GameApplication.LOGICAL_HEIGHT - 30,
            game.getAssetManager().getFont("retro_small"),
            GameRenderer.RETRO_GREEN_DARKER
        );
    }
}