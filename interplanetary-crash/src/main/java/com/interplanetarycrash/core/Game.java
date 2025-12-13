package com.interplanetarycrash.core;

import com.interplanetarycrash.GameApplication;
import com.interplanetarycrash.assets.AssetManager;
import com.interplanetarycrash.input.InputHandler;
import com.interplanetarycrash.rendering.GameRenderer;
import com.interplanetarycrash.states.*;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;

/**
 * Main game class - manages game loop, states, and core systems
 */
public class Game {
    
    private final Canvas canvas;
    private final GameRenderer renderer;
    private final InputHandler inputHandler;
    private final StateManager stateManager;
    private final AssetManager assetManager;
    
    private AnimationTimer gameLoop;
    private long lastFrameTime;
    
    // Scaling factors for responsive window
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    
    private boolean running = false;
    
    public Game(Canvas canvas) {
        this.canvas = canvas;
        
        // Initialize core systems
        this.assetManager = AssetManager.getInstance();
        this.renderer = new GameRenderer(canvas);
        this.inputHandler = new InputHandler();
        this.stateManager = new StateManager(this);
        
        // Load assets
        assetManager.loadAssets();
        
        // Initialize with main menu state
        stateManager.changeState(new MainMenuState(this));
        
        // Create game loop
        createGameLoop();
    }
    
    /**
     * Create the main game loop using JavaFX AnimationTimer
     */
    private void createGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long currentTime) {
                if (!running) return;
                
                // Calculate delta time in seconds
                double deltaTime = 0.0;
                if (lastFrameTime > 0) {
                    deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0;
                }
                lastFrameTime = currentTime;
                
                // Cap delta time to prevent large jumps
                if (deltaTime > 0.1) {
                    deltaTime = 0.1;
                }
                
                // Update and render
                update(deltaTime);
                render();
            }
        };
    }
    
    /**
     * Start the game loop
     */
    public void start() {
        if (!running) {
            running = true;
            lastFrameTime = 0;
            gameLoop.start();
            System.out.println("AnimationTimer started - rendering should begin");
        }
    }
    
    /**
     * Stop the game loop
     */
    public void stop() {
        running = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }
    
    /**
     * Update game state
     */
    private void update(double deltaTime) {
        // DEBUG: 
        // System.out.println("Game.update() - deltaTime: " + deltaTime);
        stateManager.update(deltaTime);
        inputHandler.update();
    }
    
    /**
     * Render game
     */
    private void render() {
        // DEBUG:
        // System.out.println("Rendering frame...");
        
        renderer.clear();
        stateManager.render(renderer);
    }
    
    /**
     * Handle window resize
     */
    public void handleResize(double width, double height) {
        scaleX = width / GameApplication.LOGICAL_WIDTH;
        scaleY = height / GameApplication.LOGICAL_HEIGHT;
        renderer.setScale(scaleX, scaleY);
    }
    
    // Getters
    public Canvas getCanvas() { return canvas; }
    public GameRenderer getRenderer() { return renderer; }
    public InputHandler getInputHandler() { return inputHandler; }
    public StateManager getStateManager() { return stateManager; }
    public AssetManager getAssetManager() { return assetManager; }
    public double getScaleX() { return scaleX; }
    public double getScaleY() { return scaleY; }
}