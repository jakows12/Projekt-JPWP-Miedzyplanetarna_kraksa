package com.interplanetarycrash.save;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages game save data (unlocked levels, best times)
 * Singleton pattern
 */
public class SaveManager {
    
    private static SaveManager instance;
    private static final String SAVE_FILE = "intergalactic_crash_save.dat";
    
    private GameSave currentSave;
    
    private SaveManager() {
        load();
    }
    
    public static SaveManager getInstance() {
        if (instance == null) {
            instance = new SaveManager();
        }
        return instance;
    }
    
    /**
     * Load save data from file
     */
    private void load() {
        File file = new File(SAVE_FILE);
        
        if (!file.exists()) {
            // Create new save with only level 1 unlocked
            currentSave = new GameSave();
            currentSave.unlockedLevels = 1;
            save();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            currentSave = (GameSave) ois.readObject();
            System.out.println("Save loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading save: " + e.getMessage());
            // Create new save on error
            currentSave = new GameSave();
            currentSave.unlockedLevels = 1;
        }
    }
    
    /**
     * Save data to file
     */
    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(currentSave);
            System.out.println("Save successful");
        } catch (Exception e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }
    
    /**
     * Unlock next level
     */
    public void unlockNextLevel() {
        if (currentSave.unlockedLevels < 10) {
            currentSave.unlockedLevels++;
            save();
        }
    }
    
    /**
     * Set best time for a level
     */
    public void setLevelBestTime(int level, float time) {
        Float currentBest = currentSave.levelBestTimes.get(level);
        if (currentBest == null || time < currentBest) {
            currentSave.levelBestTimes.put(level, time);
            save();
        }
    }
    
    /**
     * Set best total game time
     */
    public void setBestTotalTime(float time) {
        if (currentSave.bestTotalTime == 0 || time < currentSave.bestTotalTime) {
            currentSave.bestTotalTime = time;
            save();
        }
    }
    
    /**
     * Get number of unlocked levels
     */
    public int getUnlockedLevels() {
        return currentSave.unlockedLevels;
    }
    
    /**
     * Get best time for a specific level
     * Returns 0 if never completed
     */
    public float getLevelBestTime(int level) {
        return currentSave.levelBestTimes.getOrDefault(level, 0f);
    }
    
    /**
     * Get best total game time
     */
    public float getBestTotalTime() {
        return currentSave.bestTotalTime;
    }
    
    /**
     * Check if a level is unlocked
     */
    public boolean isLevelUnlocked(int level) {
        return level <= currentSave.unlockedLevels;
    }
    
    /**
     * Reset all save data (for debugging or new game+)
     */
    public void resetSave() {
        currentSave = new GameSave();
        currentSave.unlockedLevels = 1;
        save();
    }
    
    /**
     * Inner class representing save data
     */
    private static class GameSave implements Serializable {
        private static final long serialVersionUID = 1L;
        
        int unlockedLevels = 1;
        float bestTotalTime = 0;
        Map<Integer, Float> levelBestTimes = new HashMap<>();
    }
}
