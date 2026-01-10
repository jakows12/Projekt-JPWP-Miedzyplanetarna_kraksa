package com.interplanetarycrash.level;

import java.util.ArrayList;
import java.util.List;

import com.interplanetarycrash.GameApplication;
import com.interplanetarycrash.assets.AssetManager;
import com.interplanetarycrash.player.Player;
import com.interplanetarycrash.tasks.Task;
import com.interplanetarycrash.tasks.TaskLoader;

import javafx.scene.image.Image;

/**
 * Represents a single game level
 */
public class Level {
    
    private int levelNumber;
    private int difficulty;

    private Player player;
    private Ship ship;
    private List<Module> modules;
    
    private Image background;
    
    // Life support system
    public float lifeSupport; // 0-100
    private float maxLifeSupport = 100f;
    private float lifeDrainRate; // How fast life support drains per second
    
    // Time tracking
    private float elapsedTime;
    
    // Level state
    private boolean completed;
    
    public Level(int levelNumber) {
        this.levelNumber = levelNumber;
        this.difficulty = levelNumber;
        this.elapsedTime = 0;
        this.completed = false;
        this.lifeSupport = maxLifeSupport;
        
        // Load background
        loadBackground();
        
        // Initialize level layout
        initializeLevel();

    }
    
    /**
     * Calculate difficulty parameters based on level number
     */
    private void calculateDifficulty(ModuleType[] types) {
        // More modules as levels progress
        if (levelNumber == 1) {
            modules.get(types.length-1).repair(); // Last module is done in level 1
            modules.get(types.length-2).repair();
        } else if (levelNumber <= 3) {
            modules.get(types.length-1).repair();
        }
        
        // Life drain increases with level
        // Level 1: 0.5/s, Level 10: 2.0/s
        lifeDrainRate = 0.5f + (levelNumber - 1) * 0.15f;
    }
    
    /**
     * Load level background
     */
    private void loadBackground() {
        AssetManager assets = AssetManager.getInstance();
        background = assets.getSprite("background_level" + levelNumber);
    }
    
    /**
     * Initialize level layout (player, ship, modules)
     */
    private void initializeLevel() {
        // Create player at start position
        player = new Player(GameApplication.LOGICAL_WIDTH/2.0+100, GameApplication.LOGICAL_WIDTH/2.0-100); // Bottom center of screen
        
        // Create ship at center-top
        modules = new ArrayList<>();
        ship = new Ship(GameApplication.LOGICAL_WIDTH*0.6, GameApplication.LOGICAL_WIDTH*0.2, modules);
        
        createModulesAroundShip();
    }
    
    /**
     * Create modules positioned around the ship
     */
    private void createModulesAroundShip() {
        double shipX = ship.getX();
        double shipY = ship.getY();
        
        ModuleType[] types = ModuleType.values();

        double moduleXs[] = {GameApplication.LOGICAL_WIDTH*0.15, GameApplication.LOGICAL_WIDTH*0.9, shipX + 100, shipX + 370};
        double moduleYs[] = {GameApplication.LOGICAL_HEIGHT*0.5, GameApplication.LOGICAL_HEIGHT*0.67, shipY + 200, shipY + 180};
        
        for (int i = 0; i < types.length; i++) {
            
            ModuleType type = types[i];
            
            // TODO: Load actual task from file
            String taskFilename = TaskLoader.getTaskFilename(levelNumber, i);
            Task task = TaskLoader.loadTask(taskFilename);
            
            if (task == null) {
                System.err.println("Failed to load task for level " + levelNumber + 
                                 " module " + i + ", using fallback");
            }
            
            Module module = new Module(type, moduleXs[i], moduleYs[i], task);
            modules.add(module);
        }

        calculateDifficulty(types);
    }
    
    /**
     * Update level
     */
    public void update(double deltaTime) {
        if (completed) return;
        
        // Update time
        elapsedTime += deltaTime;
        
        // Drain life support
        lifeSupport -= lifeDrainRate * deltaTime;
        if (lifeSupport < 0) {
            lifeSupport = 0;
        }
        
        ship.update(deltaTime);
        for (Module module : modules) {
            module.update(deltaTime);
        }
    }
    
    /**
     * Check if all modules are repaired
     */
    public boolean areAllModulesRepaired() {
        for (Module module : modules) {
            if (!module.isRepaired()) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get module that player can currently interact with
     * Returns null if no module in range
     */
    public Module getInteractableModule() {
        double px = player.getX();
        double py = player.getY();
        
        for (Module module : modules) {
            if (!module.isRepaired() && module.isPlayerInRange(px, py)) {
                return module;
            }
        }
        return null;
    }
    
    /**
     * Check if player can escape (all modules repaired + near ship)
     */
    public boolean canPlayerEscape() {
        return areAllModulesRepaired() && 
               ship.isPlayerInEscapeRange(player.getX(), player.getY());
    }
    
    /**
     * Check if game over (life support depleted)
     */
    public boolean isGameOver() {
        return lifeSupport <= 0;
    }
    
    /**
     * Mark level as completed
     */
    public void complete() {
        this.completed = true;
    }
    
    // Getters
    public int getLevelNumber() { return levelNumber; }
    public int getDifficulty() { return difficulty; }
    public Player getPlayer() { return player; }
    public Ship getShip() { return ship; }
    public List<Module> getModules() { return modules; }
    public Image getBackground() { return background; }
    public float getLifeSupport() { return lifeSupport; }
    public float getMaxLifeSupport() { return maxLifeSupport; }
    public float getLifeSupportPercentage() { return (lifeSupport / maxLifeSupport) * 100; }
    public float getElapsedTime() { return elapsedTime; }
    public boolean isCompleted() { return completed; }
    public int getRequiredModules() { 
        if (levelNumber == 1) {
            return ModuleType.values().length - 2;
        } else if (levelNumber <= 3) {
            return ModuleType.values().length - 1;
        } else {
            return ModuleType.values().length;
        }
    }
    public int getRepairedModulesCount() {
        int count = 0;
        for (Module m : modules) {
            if (m.isRepaired()) count++;
        }
        if (levelNumber == 1) {
            return count - 2;
        } else if (levelNumber <= 3) {
            return count - 1;
        } else {
            return count;
        }
    }
}