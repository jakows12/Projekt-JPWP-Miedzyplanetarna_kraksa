package com.interplanetarycrash.states;

import java.util.ArrayList;
import java.util.List;

import com.interplanetarycrash.GameApplication;
import com.interplanetarycrash.core.Game;
import com.interplanetarycrash.rendering.GameRenderer;
import com.interplanetarycrash.save.SaveManager;
import com.interplanetarycrash.ui.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Level selection screen - KEYBOARD NAVIGATION
 * Arrow keys or WASD to navigate, ENTER/SPACE to select, ESC to go back
 */
public class LevelSelectionState extends State {
    
    private static final int LEVELS_PER_ROW = 5;
    private static final double LEVEL_BUTTON_SIZE = 150;
    private static final double LEVEL_BUTTON_SPACING = 40;

    private static final Color TEXT_COLOR = GameRenderer.RETRO_GREEN;
    private static final Color TEXT_SELECTED_COLOR = Color.BLACK;

    public static int TOTAL_LEVELS = 10;
    
    private int selectedLevel; 
    private int unlockedLevels;
    
    private Font titleFont;
    private Font textFont;
    
    private double startX;
    private double startY;

    private float bestTotalTime;

    private List<Button> buttons;

    
    public LevelSelectionState(Game game) {
        super(game);
    }
    
    @Override
    public void enter() {
        System.out.println("Entering Level Selection");
        
        titleFont = game.getAssetManager().getFont("retro_large");
        textFont = game.getAssetManager().getFont("retro");
        
        // Load save data
        SaveManager saveManager = SaveManager.getInstance();
        unlockedLevels = saveManager.getUnlockedLevels();
        bestTotalTime = saveManager.getBestTotalTime();
        // Start with first unlocked level selected
        selectedLevel = 1;
        
        // Calculate grid start position
        startX = (GameApplication.LOGICAL_WIDTH - 
                 (LEVELS_PER_ROW * LEVEL_BUTTON_SIZE + 
                  (LEVELS_PER_ROW - 1) * LEVEL_BUTTON_SPACING)) / 2.0;
        startY = 200;
        buttons = new ArrayList<>();
    }
    
    @Override
    public void exit() {
        System.out.println("Exiting Level Selection");
    }
    
    @Override
    public void update(double deltaTime) {
        // Handle navigation
        if (game.getInputHandler().isNavigatingLeft()) {
            navigateLeft();
        }
        
        if (game.getInputHandler().isNavigatingRight()) {
            navigateRight();
        }
        
        if (game.getInputHandler().isNavigatingUp()) {
            navigateUp();
        }
        
        if (game.getInputHandler().isNavigatingDown()) {
            navigateDown();
        }
        
        // Handle confirmation (ENTER or SPACE)
        if (game.getInputHandler().isConfirming() || game.getInputHandler().isInteracting()) { 
            buttons.get(selectedLevel).activate();
        }
        

    }
    
    private void navigateLeft() {
        if (selectedLevel == 0) return; // Already on back button
        
        int row = (selectedLevel - 1) / LEVELS_PER_ROW;
        int col = (selectedLevel - 1) % LEVELS_PER_ROW;
        
        if (col > 0) {
            selectedLevel--;
        } else {
            // Wrap to end of row
            selectedLevel = Math.min(TOTAL_LEVELS, (row + 1) * LEVELS_PER_ROW);
        }
    }
    
    private void navigateRight() {
        if (selectedLevel == 0) return; // On back button
        
        int row = (selectedLevel - 1) / LEVELS_PER_ROW;
        int col = (selectedLevel - 1) % LEVELS_PER_ROW;
        
        if (col < LEVELS_PER_ROW - 1 && selectedLevel < TOTAL_LEVELS) {
            selectedLevel++;
        } else {
            // Wrap to start of row
            selectedLevel = row * LEVELS_PER_ROW + 1;
        }
    }
    
    private void navigateUp() {
        if (selectedLevel == 0) {
            // From back button, go to bottom row
            selectedLevel = TOTAL_LEVELS - LEVELS_PER_ROW + 1;
        } else if (selectedLevel <= LEVELS_PER_ROW) {
            // Top row, go to back button
            selectedLevel = 0;
        } else {
            // Go up one row
            selectedLevel -= LEVELS_PER_ROW;
        }
    }
    
    private void navigateDown() {
        if (selectedLevel == 0) {
            // From back button, go to first level
            selectedLevel = 1;
        } else if (selectedLevel + LEVELS_PER_ROW <= TOTAL_LEVELS) {
            // Go down one row
            selectedLevel += LEVELS_PER_ROW;
        } else {
            // Bottom row, go to back button
            selectedLevel = 0;
        }
    }
    
    @Override
    public void render(GameRenderer renderer) {
        // Background
        renderer.fillRect(0, 0, GameApplication.LOGICAL_WIDTH, 
                         GameApplication.LOGICAL_HEIGHT, 
                         GameRenderer.RETRO_BACKGROUND);
        
        // Title
        renderer.drawCenteredText(
            "SELECT LEVEL",
            GameApplication.LOGICAL_WIDTH / 2.0,
            120,
            titleFont,
            GameRenderer.RETRO_GREEN
        );

        renderer.drawCenteredText(
            "BEST TOTAL TIME: " + String.format("%.2fs", bestTotalTime),
            GameApplication.LOGICAL_WIDTH / 2.0,
            150,
            textFont,
            GameRenderer.RETRO_GREEN
        );
        
        // Info text
        renderer.drawCenteredText(
            "Unlocked: " + unlockedLevels + " / " + TOTAL_LEVELS,
            GameApplication.LOGICAL_WIDTH / 2.0,
            180,
            textFont,
            GameRenderer.RETRO_GREEN_DARK
        );
        
        // Render back button
        double backX = 50;
        double backY = GameApplication.LOGICAL_HEIGHT - 120;
        renderBackButton(renderer, backX, backY, selectedLevel == 0);

        // Render level buttons
        SaveManager saveManager = SaveManager.getInstance();
        for (int i = 0; i < TOTAL_LEVELS; i++) {
            int level = i + 1;
            int row = i / LEVELS_PER_ROW;
            int col = i % LEVELS_PER_ROW;
            
            double x = startX + col * (LEVEL_BUTTON_SIZE + LEVEL_BUTTON_SPACING);
            double y = startY + row * (LEVEL_BUTTON_SIZE + LEVEL_BUTTON_SPACING);
            
            boolean unlocked = level <= unlockedLevels;
            boolean selected = level == selectedLevel;
            float bestTime = saveManager.getLevelBestTime(level);
            
            renderLevelButton(renderer, level, x, y, unlocked, selected, bestTime);
        }
        

        
        // Controls hint
        Font hintFont = game.getAssetManager().getFont("retro_small");
        renderer.drawCenteredText(
            "↑↓ or WASD: Navigate  |  ENTER / E: Select ",
            GameApplication.LOGICAL_WIDTH / 2.0,
            GameApplication.LOGICAL_HEIGHT - 50,
            hintFont,
            GameRenderer.RETRO_GREEN_DARKER
        );
    }
    
    private void renderLevelButton(GameRenderer renderer, int level, double x, double y, 
                                   boolean unlocked, boolean selected, float bestTime) {
        Color textColor = selected ? TEXT_SELECTED_COLOR : TEXT_COLOR;
        
        Button levelButton = new Button(
            unlocked ? String.valueOf(level) : "🔒",
            x,
            y,
            LEVEL_BUTTON_SIZE,
            LEVEL_BUTTON_SIZE,
            titleFont
        );         

        if (!unlocked) {
            levelButton.setEnabled(false);
        } else {
            levelButton.setEnabled(true);
            levelButton.setOnClick(() -> {
            game.getStateManager().changeState(new LevelPlayingState(game, selectedLevel));
        });
        } 

        if (selected) {
            levelButton.setSelected(true);
        } else {
            levelButton.setSelected(false);
        }

        levelButton.setArrowsVisible(false);

        buttons.add(level, levelButton);

        levelButton.render(renderer);
            
        
        // Best time
        if (unlocked && bestTime > 0) {
            Font timeFont = game.getAssetManager().getFont("retro_small");
            String timeStr = String.format("%.2fs", bestTime);
            renderer.drawCenteredText(
                timeStr,
                x + LEVEL_BUTTON_SIZE / 2,
                y + LEVEL_BUTTON_SIZE - 20,
                timeFont,
                textColor
            );
        }
    }
    
    private void renderBackButton(GameRenderer renderer, double x, double y, boolean selected) {
        
        double width = 200;
        double height = 60;
        
        Button backButton = new Button(
            "Back ⌂",
            x,
            y,
            width,
            height,
            titleFont
        );

        backButton.setArrowsVisible(false);
        
        backButton.setOnClick(() -> {
            game.getStateManager().changeState(new MainMenuState(game));
        });

        if (selected) {
            backButton.setSelected(true);
        } else {
            backButton.setSelected(false);
        }

        backButton.render(renderer);
        buttons.add(0, backButton);
    
    }

}