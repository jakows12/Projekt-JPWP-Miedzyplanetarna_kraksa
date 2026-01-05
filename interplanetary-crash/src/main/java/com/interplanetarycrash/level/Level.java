package com.interplanetarycrash.level;

import com.interplanetarycrash.assets.AssetManager;
import com.interplanetarycrash.core.Game;
import com.interplanetarycrash.player.Player;
import com.interplanetarycrash.tasks.Task;
import com.interplanetarycrash.GameApplication;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single game level
 */
public class Level {
    
    private int levelNumber;
    private int difficulty;
    private int requiredModules; // How many modules to repair (2-4)
    
    private Player player;
    private Ship ship;
    private List<Module> modules;
    
    private Image background;
    
    // Life support system
    private float lifeSupport; // 0-100
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
        
        // Calculate difficulty parameters
        calculateDifficulty();
        
        // Load background
        loadBackground();
        
        // Initialize level layout
        initializeLevel();
    }
    
    /**
     * Calculate difficulty parameters based on level number
     */
    private void calculateDifficulty() {
        // More modules as levels progress
        if (levelNumber == 1) {
            requiredModules = 2;
        } else if (levelNumber <= 3) {
            requiredModules = 3;
        } else {
            requiredModules = 4;
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
        player = new Player(GameApplication.LOGICAL_WIDTH/2.0, GameApplication.LOGICAL_WIDTH/2.0-100); // Bottom center of screen
        
        // Create ship at center-top
        modules = new ArrayList<>();
        ship = new Ship(GameApplication.LOGICAL_WIDTH/2.0, GameApplication.LOGICAL_WIDTH/2.0-300, modules);
        
        // Create modules around the ship
        // TODO: Load actual tasks from files
        createModulesAroundShip();
    }
    
    /**
     * Create modules positioned around the ship
     */
    private void createModulesAroundShip() {
        double shipX = ship.getX();
        double shipY = ship.getY();
        double radius = 256;
        
        ModuleType[] types = ModuleType.values();
        
        for (int i = 0; i < requiredModules; i++) {
            // Position modules in a circle around ship
            double angle = (Math.PI * 2 / requiredModules) * i - Math.PI / 4.0;
            double moduleX = shipX + Math.cos(angle) * radius;
            double moduleY = shipY + Math.sin(angle) * radius;
            
            ModuleType type = types[i % types.length];
            
            // TODO: Load actual task from file
            Task task = null; // Placeholder
            
            Module module = new Module(type, moduleX, moduleY, task);
            modules.add(module);
        }
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
        
        // Update modules (fire animations)
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
    public int getRequiredModules() { return requiredModules; }
    public int getRepairedModulesCount() {
        int count = 0;
        for (Module m : modules) {
            if (m.isRepaired()) count++;
        }
        return count;
    }
}