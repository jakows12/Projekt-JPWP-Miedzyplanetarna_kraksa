package com.interplanetarycrash.states;

import java.text.DecimalFormat;

import com.interplanetarycrash.GameApplication;
import com.interplanetarycrash.core.Game;
import com.interplanetarycrash.rendering.GameRenderer;
import com.interplanetarycrash.save.SaveManager;
import com.interplanetarycrash.level.Level;
import com.interplanetarycrash.states.LevelSelectionState;


public class EscapeSequenceState extends State {

    private Level level;
    private float totalTime = 0;

    public EscapeSequenceState(Game game, Level level) {
        super(game);
        this.level = level;
    }

    @Override
    public void enter() {
        System.out.println("Entering escape sequence for level " + level.getLevelNumber());
        GameApplication.TotalTime[level.getLevelNumber() - 1] = level.getElapsedTime();
        SaveManager saveManager = SaveManager.getInstance();

        saveManager.setLevelBestTime(level.getLevelNumber(), level.getElapsedTime());

        if (level.getLevelNumber() >= LevelSelectionState.TOTAL_LEVELS) {
            for (int i = 0; i < LevelSelectionState.TOTAL_LEVELS; i++) {
                totalTime += GameApplication.TotalTime[i];
            }
            saveManager.setBestTotalTime(totalTime);
        } else {
            saveManager.unlockNextLevel();
        }
    }

    @Override
    public void exit() {
        System.out.println("Exiting escape sequence for level " + level.getLevelNumber());
    }

    @Override
    public void update(double deltaTime) {
        // Check for input to return to main menu or proceed to next level
        if (game.getInputHandler().isPausing()) {
            System.out.println("Returning to main menu from escape sequence");
            game.getStateManager().changeState(new MainMenuState(game));
        } else if (game.getInputHandler().isInteracting() && level.getLevelNumber() < LevelSelectionState.TOTAL_LEVELS) {
            System.out.println("Proceeding to next level from escape sequence");
            game.getStateManager().changeState(new LevelPlayingState(game, level.getLevelNumber() + 1));
        }
    }

    @Override
    public void render(GameRenderer renderer) {
        // Render background
        if (level.getBackground() != null) {
            renderer.drawImage(level.getBackground(), 0, 0, 
                             GameApplication.LOGICAL_WIDTH, 
                             GameApplication.LOGICAL_HEIGHT);
        }
        
        // Render level info
        renderInfo(renderer);
    }

    private void renderInfo(GameRenderer renderer) {
        // Level number and modules repaired

        String totalinfo = "Game completed in " + String.format("%.2fs", totalTime)+ " seconds!";
        String info = "Level completed in " + String.format("%.2fs", level.getElapsedTime()) + " seconds!";

        String toMainMenu = "Press SPACE to return to main menu";
        String toNextLevel = "Press E to go to the next level";

        if (level.getLevelNumber() >= LevelSelectionState.TOTAL_LEVELS) {
            toNextLevel = "No more levels available";
            renderer.drawCenteredText(
            totalinfo,
            GameApplication.LOGICAL_WIDTH / 2 ,
            GameApplication.LOGICAL_HEIGHT / 2.0 - 160,
            game.getAssetManager().getFont("retro_large"),
            GameRenderer.RETRO_GREEN
        );
        }
        renderer.drawCenteredText(
            info,
            GameApplication.LOGICAL_WIDTH / 2 ,
            GameApplication.LOGICAL_HEIGHT / 2.0 - 80,
            game.getAssetManager().getFont("retro_large"),
            GameRenderer.RETRO_GREEN
        );

        renderer.drawCenteredText(
            toMainMenu,
            GameApplication.LOGICAL_WIDTH / 2 ,
            GameApplication.LOGICAL_HEIGHT / 2.0,
            game.getAssetManager().getFont("retro_large"),
            GameRenderer.RETRO_GREEN
        );

        renderer.drawCenteredText(
            toNextLevel,
            GameApplication.LOGICAL_WIDTH / 2 ,
            GameApplication.LOGICAL_HEIGHT / 2.0 + 80,
            game.getAssetManager().getFont("retro_large"),
            GameRenderer.RETRO_GREEN
        );
        
    }
}
