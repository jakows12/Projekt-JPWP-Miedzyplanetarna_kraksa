package com.interplanetarycrash.states;

import com.interplanetarycrash.GameApplication;
import com.interplanetarycrash.core.Game;
import com.interplanetarycrash.rendering.GameRenderer;
import com.interplanetarycrash.ui.Button;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * Main menu state - first screen when game starts
 * Navigation: UP/DOWN arrows or W/S to select, ENTER/SPACE to confirm
 */
public class MainMenuState extends State {
    
    private List<Button> buttons;
    private int selectedButtonIndex;
    
    private Font titleFont;
    private Font subtitleFont;
    
    private static final double BUTTON_WIDTH = 200;
    private static final double BUTTON_HEIGHT = 40;
    private static final double BUTTON_SPACING = 10;
    
    public MainMenuState(Game game) {
        super(game);
    }
    
    @Override
    public void enter() {
        System.out.println("Entering Main Menu");
        
        // Get fonts
        titleFont = game.getAssetManager().getFont("retro_large");
        subtitleFont = game.getAssetManager().getFont("retro");
        
        // Create buttons
        buttons = new ArrayList<>();
        
        double centerX = GameApplication.LOGICAL_WIDTH / 2.0;
        double startY = GameApplication.LOGICAL_HEIGHT / 2.0;
        
        // Start button - begins from level 1
        Button startButton = new Button(
            "START",
            centerX - BUTTON_WIDTH / 2,
            startY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            subtitleFont
        );
        startButton.setOnClick(() -> {
            System.out.println("Starting game from level 1");
            game.getStateManager().changeState(new LevelPlayingState(game, 1));
        });
        buttons.add(startButton);
        
        // Levels button - opens level selection
        Button levelsButton = new Button(
            "LEVELS",
            centerX - BUTTON_WIDTH / 2,
            startY + (BUTTON_HEIGHT + BUTTON_SPACING),
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            subtitleFont
        );
        levelsButton.setOnClick(() -> {
            System.out.println("Opening level selection");
            game.getStateManager().changeState(new LevelSelectionState(game));
        });
        buttons.add(levelsButton);
        
        // Settings button
        Button settingsButton = new Button(
            "SETTINGS",
            centerX - BUTTON_WIDTH / 2,
            startY + 2 * (BUTTON_HEIGHT + BUTTON_SPACING),
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            subtitleFont
        );
        settingsButton.setOnClick(() -> {
            System.out.println("Opening settings");
            // TODO: Create SettingsState
        });
        buttons.add(settingsButton);
        
        // Tutorial button
        Button creditsButton = new Button(
            "TUTORIAL",
            centerX - BUTTON_WIDTH / 2,
            startY + 3 * (BUTTON_HEIGHT + BUTTON_SPACING),
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            subtitleFont
        );
        creditsButton.setOnClick(() -> {
            System.out.println("Opening Tutorial");
            // TODO: Create TutorialState
        });
        buttons.add(creditsButton);
        
        // Exit button
        Button exitButton = new Button(
            "EXIT",
            centerX - BUTTON_WIDTH / 2,
            startY + 4 * (BUTTON_HEIGHT + BUTTON_SPACING),
            BUTTON_WIDTH,
            BUTTON_HEIGHT,
            subtitleFont
        );
        exitButton.setOnClick(() -> {
            System.out.println("Exiting game");
            System.exit(0);
        });
        buttons.add(exitButton);
        
        // Select first button
        selectedButtonIndex = 0;
        updateButtonSelection();
    }
    
    @Override
    public void exit() {
        System.out.println("Exiting Main Menu");
    }
    
    @Override
    public void update(double deltaTime) {
        
        // Handle navigation
        if (game.getInputHandler().isNavigatingDown()) {
        //    System.out.println("Navigating DOWN"); // DEBUG
            selectedButtonIndex = (selectedButtonIndex + 1) % buttons.size();
            updateButtonSelection();
        }
        
        if (game.getInputHandler().isNavigatingUp()) {
        //    System.out.println("Navigating UP"); // DEBUG
            selectedButtonIndex--;
            if (selectedButtonIndex < 0) {
                selectedButtonIndex = buttons.size() - 1;
            }
            updateButtonSelection();
        }
        
        // Handle confirmation
        if (game.getInputHandler().isConfirming()) {
        //    System.out.println("Confirming selection: " + selectedButtonIndex); // DEBUG
            buttons.get(selectedButtonIndex).activate();
        }
    }
    
    /**
     * Update which button is selected
     */
    private void updateButtonSelection() {
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setSelected(i == selectedButtonIndex);
        }
    }
    
    @Override
    public void render(GameRenderer renderer) {
        // DEBUG
        // System.out.println("MainMenuState.render() - selected button: " + selectedButtonIndex);
        
        // Draw background
        renderer.fillRect(0, 0, GameApplication.LOGICAL_WIDTH, 
                         GameApplication.LOGICAL_HEIGHT, 
                         GameRenderer.RETRO_BACKGROUND);
        
        // Draw title
        renderer.drawCenteredText(
            "MIĘDZYPLANETARNA KRAKSA",
            GameApplication.LOGICAL_WIDTH / 2.0,
            200,
            titleFont,
            GameRenderer.RETRO_GREEN
        );
        
        // Draw subtitle
        renderer.drawCenteredText(
            "INTERPLANETARY CRASH",
            GameApplication.LOGICAL_WIDTH / 2.0,
            260,
            subtitleFont,
            GameRenderer.RETRO_GREEN_DARK
        );
        
        // Draw retro spaceship ASCII art or placeholder
        drawSpaceshipArt(renderer);
        
        // Draw buttons
        for (Button button : buttons) {
            button.render(renderer);
        }
        
        // Draw controls hint
        Font hintFont = game.getAssetManager().getFont("retro_small");
        renderer.drawCenteredText(
            "↑↓ or W/S: Navigate  |  ENTER or SPACE: Select",
            GameApplication.LOGICAL_WIDTH / 2.0,
            GameApplication.LOGICAL_HEIGHT - 50,
            hintFont,
            GameRenderer.RETRO_GREEN_DARKER
        );
        
        // Draw version info
        renderer.drawText(
            "v0.1.1 - Still a Prototype",
            20,
            GameApplication.LOGICAL_HEIGHT - 30,
            game.getAssetManager().getFont("retro_small"),
            GameRenderer.RETRO_GREEN_DARKER
        );
        
    }
    
    /**
     * Draw decorative spaceship art
     */
    private void drawSpaceshipArt(GameRenderer renderer) {
        // TODO: Draw actual spaceship sprite or ASCII art
    }
}