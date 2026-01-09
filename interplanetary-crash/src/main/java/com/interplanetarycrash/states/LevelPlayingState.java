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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Main gameplay state - player moving around, repairing modules
 * PAUSE MENU IS IN THE BOTTOM BAR!
 */
public class LevelPlayingState extends State {
    
    private Level level;
    private LifeSupportBar lifeSupportBar;
    private Timer timer;
    private InteractionPrompt interactionPrompt;

    private Direction lastDirection = Direction.DOWN;
    
    // Pause state - focuses on bottom bar
    private boolean paused;
    private int selectedIcon; // 0=Resume, 1=Restart, 2=Menu, 3=Exit
    private static final int ICON_RESUME = 0;
    private static final int ICON_RESTART = 1;
    private static final int ICON_MENU = 2;
    private static final int ICON_EXIT = 3;
    
    // Bottom bar
    private static final double BOTTOM_BAR_HEIGHT = 80; // Larger for interactive menu
    private static final String SYMBOL_RESUME = "▶";
    private static final String SYMBOL_RESTART = "↻";
    private static final String SYMBOL_MENU = "⌂";
    private static final String SYMBOL_EXIT = "✕";
    
    public LevelPlayingState(Game game, int levelNumber) {
        super(game);
        this.level = new Level(levelNumber);
        this.paused = false;
        this.selectedIcon = ICON_RESUME;
    }
    
    @Override
    public void enter() {
        System.out.println("Entering Level " + level.getLevelNumber());
        
        // Initialize UI
        lifeSupportBar = new LifeSupportBar(30, 30, 350, 30);
        timer = new Timer(GameApplication.LOGICAL_WIDTH - 170, 40);
        interactionPrompt = new InteractionPrompt();
        
        paused = false;
        selectedIcon = ICON_RESUME;
    }
    
    @Override
    public void exit() {
        System.out.println("Exiting Level " + level.getLevelNumber());
    }
    
    @Override
    public void update(double deltaTime) {
        // Check for pause toggle
        if (game.getInputHandler().isPausing()) {
            togglePause();
            return;
        }
        
        // If paused, handle bottom bar navigation
        if (paused) {
            updatePauseNavigation();
            return;
        }
        
        // Normal gameplay updates
        // Check game over
        if (level.isGameOver()) {
            level.getPlayer().setDead();
            if (game.getInputHandler().isConfirming() || game.getInputHandler().isInteracting()) {
                game.getStateManager().changeState(new LevelPlayingState(game, level.getLevelNumber()));
            } else if (game.getInputHandler().isPausing()) {
                game.getStateManager().changeState(new MainMenuState(game));
            }
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
     * Toggle pause state
     */
    private void togglePause() {
        paused = !paused;
        if (paused) {
            selectedIcon = ICON_RESUME; // Start at resume
        } 
    }
    
    /**
     * Update pause navigation (bottom bar icons)
     */
    private void updatePauseNavigation() {
        // Navigate with LEFT/RIGHT or A/D
        if (game.getInputHandler().isNavigatingRight()) {
            selectedIcon = (selectedIcon + 1) % 4;
        }
        
        if (game.getInputHandler().isNavigatingLeft()) {
            selectedIcon--;
            if (selectedIcon < 0) {
                selectedIcon = 3;
            }
        }
        
        // Confirm with ENTER or E
        if (game.getInputHandler().isConfirming() || game.getInputHandler().isInteracting()) {
            executeIconAction();
        }
    }
    
    /**
     * Execute selected icon action
     */
    private void executeIconAction() {
        switch (selectedIcon) {
            case ICON_RESUME:
                paused = false;
                break;
                
            case ICON_RESTART:
                game.getStateManager().changeState(new LevelPlayingState(game, level.getLevelNumber()));
                break;
                
            case ICON_MENU:
                game.getStateManager().changeState(new MainMenuState(game));
                break;
                
            case ICON_EXIT:
                System.out.println("Exiting game");
                System.exit(0);
                break;
        }
    }
    
    /**
     * Handle player movement with WASD
     */
    private void updatePlayerMovement(double deltaTime) {
        Player player = level.getPlayer();
        
        // Determine movement direction
        Direction moveDirection = Direction.NONE; // Default to no movement;

        if (game.getInputHandler().isMovingUp()) {
            lastDirection = moveDirection = Direction.UP;
        } else if (game.getInputHandler().isMovingDown()) {
            lastDirection = moveDirection = Direction.DOWN;
        } else if (game.getInputHandler().isMovingLeft()) {
            lastDirection = moveDirection = Direction.LEFT;
        } else if (game.getInputHandler().isMovingRight()) {
            lastDirection = moveDirection = Direction.RIGHT;
        } else {
            if (lastDirection == Direction.UP || lastDirection == Direction.RIGHT ) {
                moveDirection = Direction.NONE_RIGHT;
            } else if (lastDirection == Direction.DOWN || lastDirection == Direction.LEFT ) {
                moveDirection = Direction.NONE_LEFT;
            }
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
        
        // Check map bounds (account for bottom bar)
        if (player.getX() < 50 || player.getX() > GameApplication.LOGICAL_WIDTH - 50 ||
            player.getY() < 50 || player.getY() > GameApplication.LOGICAL_HEIGHT - BOTTOM_BAR_HEIGHT - 50) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check for possible interactions (modules, ship escape)
     */
    private void updateInteractions() {
        Player player = level.getPlayer();
        
        if (!level.isGameOver()) {
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
        } else {
            interactionPrompt.hide();
        }
    }
    
    /**
     * Start repairing a module (enter task state)
     */
    private void startModuleRepair(Module module) {
        
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
        // Render main game content
        renderGameplay(renderer);
        
        // Render bottom bar (interactive when paused)
        renderBottomBar(renderer);
        
        // If paused, dim everything except bottom bar
        if (paused) {
            renderPause(renderer);
        }
        if (level.isGameOver()) {
            renderDeathMessage(renderer);
        }
    }
    
    /**
     * Render normal gameplay
     */
    private void renderGameplay(GameRenderer renderer) {
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
        
        // Render UI overlays
        lifeSupportBar.render(renderer, level.getLifeSupportPercentage());
        timer.render(renderer, level.getElapsedTime());
        interactionPrompt.render(renderer);
        
        // Render level info
        renderLevelInfo(renderer);
    }
    
    /**
     * Render dimmer overlay when paused (doesn't dim bottom bar)
     */
    private void renderPause(GameRenderer renderer) {
        
        Font pauseFont = game.getAssetManager().getFont("retro_large");
        renderer.drawCenteredText(
            " PAUSED ",
            GameApplication.LOGICAL_WIDTH / 2.0,
            200,
            pauseFont,
            GameRenderer.RETRO_GREEN
        );     
    }
    
    /**
     * Render level information
     */
    private void renderLevelInfo(GameRenderer renderer) {
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
     * Render bottom icon bar (INTERACTIVE MENU when paused!)
     */
    private void renderBottomBar(GameRenderer renderer) {
        double barY = GameApplication.LOGICAL_HEIGHT - BOTTOM_BAR_HEIGHT;
        
        // Background bar - brighter when paused
        Color barBg = paused ? Color.rgb(0, 30, 0, 0.95) : Color.rgb(0, 15, 0, 0.9);
        renderer.fillRect(0, barY, GameApplication.LOGICAL_WIDTH, BOTTOM_BAR_HEIGHT, barBg);
        
        // Top border - brighter when paused
        Color borderColor = paused ? GameRenderer.RETRO_GREEN : GameRenderer.RETRO_GREEN_DARKER;
        renderer.drawLine(0, barY, GameApplication.LOGICAL_WIDTH, barY, borderColor, 3);
        
        // Calculate icon positions
        double iconSpacing = GameApplication.LOGICAL_WIDTH / 5.0;
        double startX = iconSpacing;
        double iconY = barY + 40;
        double labelY = barY + 70;
        
        Font iconFont = game.getAssetManager().getFont("retro_large");
        Font labelFont = game.getAssetManager().getFont("retro_small");
        
        // Render each icon
        renderIcon(renderer, SYMBOL_RESUME, "Resume", startX, iconY, labelY, 
                  ICON_RESUME, iconFont, labelFont);
        renderIcon(renderer, SYMBOL_RESTART, "Restart", startX + iconSpacing, iconY, labelY, 
                  ICON_RESTART, iconFont, labelFont);
        renderIcon(renderer, SYMBOL_MENU, "Menu", startX + 2 * iconSpacing, iconY, labelY, 
                  ICON_MENU, iconFont, labelFont);
        renderIcon(renderer, SYMBOL_EXIT, "Exit", startX + 3 * iconSpacing, iconY, labelY, 
                  ICON_EXIT, iconFont, labelFont);
        
        // Central hint text
        String hintText;
        if (paused) {
            hintText = "←→ or A/D: Navigate  |  ENTER or E: Select  |  SPACE: Resume";
        } else {
            hintText = "  ↑↓ or WASD: Move   |      E: Interact     |  SPACE: Pause ";
        }

        Font hintFont = game.getAssetManager().getFont("retro_small");
        renderer.drawCenteredText(
            hintText,
            GameApplication.LOGICAL_WIDTH / 2.0,
            barY - 15,
            hintFont,
            paused ? GameRenderer.RETRO_GREEN : GameRenderer.RETRO_GREEN_DARK
        );
    }
    
    /**
     * Render a single icon with selection highlighting
     */
    private void renderIcon(GameRenderer renderer, String symbol, String label,
                           double x, double y, double labelY, int iconIndex,
                           Font iconFont, Font labelFont) {
        boolean selected = (paused && selectedIcon == iconIndex);
        
        // Selection background circle
        if (selected) {
            renderer.fillRect(x-35, y-40, 70, 80, Color.rgb(0, 100, 0, 0.7));
            renderer.drawRect(x-35, y-40, 70, 80, GameRenderer.RETRO_GREEN);
            
            renderer.drawText("◄", x - 62, y + 5, game.getAssetManager().getFont("retro"),
                            GameRenderer.RETRO_GREEN);
            renderer.drawText("►", x + 45, y + 5, game.getAssetManager().getFont("retro"), 
                            GameRenderer.RETRO_GREEN);
        }
        
        // Icon color
        Color iconColor;
        if (selected) {
            iconColor = Color.WHITE;
        } else if (paused) {
            iconColor = GameRenderer.RETRO_GREEN_DARK;
        } else {
            iconColor = GameRenderer.RETRO_GREEN_DARKER;
        }
        
        // Draw icon
        renderer.drawCenteredText(symbol, x, y, iconFont, iconColor);
        
        // Draw label
        Color labelColor = selected ? GameRenderer.RETRO_GREEN : GameRenderer.RETRO_GREEN_DARKER;
        renderer.drawCenteredText(label, x, labelY, labelFont, labelColor);
    }

    private void renderDeathMessage(GameRenderer renderer) {
        renderer.fillRect(0, GameApplication.LOGICAL_HEIGHT / 2.0 - 120, GameApplication.LOGICAL_WIDTH, 120, 
        Color.rgb(100, 0, 0, 0.8));
        renderer.drawCenteredText(
            "YOU DIED",
            GameApplication.LOGICAL_WIDTH / 2.0,
            GameApplication.LOGICAL_HEIGHT / 2.0 - 70,
            game.getAssetManager().getFont("retro_large"),
            Color.RED
        );
        renderer.drawCenteredText(
            "Press ENTER or E to try again || Press SPACE to go to menu",
            GameApplication.LOGICAL_WIDTH / 2.0,
            GameApplication.LOGICAL_HEIGHT / 2.0 - 20,
            game.getAssetManager().getFont("retro"),
            Color.RED
        );
    }
}