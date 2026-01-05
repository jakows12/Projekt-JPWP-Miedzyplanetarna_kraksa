package com.interplanetarycrash;

import com.interplanetarycrash.core.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * JavaFX Application wrapper for the game
 * Handles window creation and JavaFX lifecycle
 */
public class GameApplication extends Application {
    
    // Logical resolution (all game coordinates use this)
    public static final int LOGICAL_WIDTH = 1280;
    public static final int LOGICAL_HEIGHT = 720;
    
    // Initial window size
    private static final int INITIAL_WIDTH = 1280;
    private static final int INITIAL_HEIGHT = 720;
    
    private Game game;
    private Canvas canvas;
    
    @Override
    public void start(Stage primaryStage) {
        //System.out.println("=== GameApplication.start() called ===");
        
        // Create canvas for rendering
        canvas = new Canvas(INITIAL_WIDTH, INITIAL_HEIGHT);
        
        // IMPORTANT: Canvas needs to be focusable to receive keyboard events
        canvas.setFocusTraversable(true);
        //System.out.println("Canvas created and set focusable");
        
        // Create game instance
        game = new Game(canvas);
        //System.out.println("Game instance created");
        
        // Setup scene
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, INITIAL_WIDTH, INITIAL_HEIGHT);
        
        // Make sure canvas keeps focus when clicked
        canvas.setOnMouseClicked(e -> {
        //    System.out.println("Canvas clicked - requesting focus");
            canvas.requestFocus();
        });
        
        // Handle window resize
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setWidth(newVal.doubleValue());
            game.handleResize(newVal.doubleValue(), canvas.getHeight());
        });
        
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            canvas.setHeight(newVal.doubleValue());
            game.handleResize(canvas.getWidth(), newVal.doubleValue());
        });
        
        // Setup input handlers
        setupInputHandlers(scene);
        //System.out.println("Input handlers setup complete");
        
        // Configure stage
        primaryStage.setTitle("Międzyplanetarna Kraksa - Interplanetary Crash");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        
        // Show window
        primaryStage.show();
        //System.out.println("Window shown");
        
        // CRITICAL: Request focus for canvas so it receives keyboard events
        canvas.requestFocus();
        //System.out.println("Focus requested for canvas. Focus owner: " + scene.getFocusOwner());
        
        // Start game loop
        game.start();
        //System.out.println("Game loop started");
        
        // Handle window close
        primaryStage.setOnCloseRequest(e -> {
            game.stop();
        });
        
        //System.out.println("=== GameApplication initialization complete ===");
    }
    
    /**
     * Setup keyboard input handlers - KEYBOARD ONLY
     */
    private void setupInputHandlers(Scene scene) {
        System.out.println("Setting up input handlers...");
        
        // Handle keyboard input on SCENE level
        scene.setOnKeyPressed(e -> {
        //    System.out.println("!!! SCENE received KEY PRESSED: " + e.getCode() + " !!!");
            game.getInputHandler().handleKeyPressed(e);
            e.consume(); // Prevent event propagation
        });
        
        scene.setOnKeyReleased(e -> {
        //    System.out.println("!!! SCENE received KEY RELEASED: " + e.getCode() + " !!!");
            game.getInputHandler().handleKeyReleased(e);
            e.consume();
        });
        
        //System.out.println("Input handlers registered on Scene");
    }
}