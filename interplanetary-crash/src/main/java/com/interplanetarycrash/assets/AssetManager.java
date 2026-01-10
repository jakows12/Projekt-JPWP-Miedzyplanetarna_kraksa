package com.interplanetarycrash.assets;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Singleton class for managing game assets (sprites, fonts, sounds)
 * Creates placeholder graphics when actual assets are not available
 */
public class AssetManager {
    
    private static AssetManager instance;

    private static final int PLAYER_SCALE = 2;      
    private static final int MODULE_SCALE = 2;      
    private static final int SHIP_SCALE = 4;        
    private static final int BACKGROUND_SCALE = 1;  
    
    private final Map<String, Image> sprites = new HashMap<>();
    private final Map<String, Font> fonts = new HashMap<>();
    
    private AssetManager() {}

    private static final String assetsPath = System.getProperty("user.dir") + "/interplanetary-crash/src/main/assets";

    public static AssetManager getInstance() {
        if (instance == null) {
            instance = new AssetManager();
        }
        return instance;
    }
    
    /**
     * Load all game assets
     */
    public void loadAssets() {
        System.out.println("Loading assets...");
        
        loadFonts();
        loadBackgrounds();
        loadAllSprites();
        loadAllAnimations();

        System.out.println("Assets loaded successfully!");
    }
    
    /**
     * Load fonts
     */
    private void loadFonts() {
        // Try to load custom font from file (JavaFX)
        try (java.io.FileInputStream fis = new java.io.FileInputStream(assetsPath + "/fonts/BoldPixels.ttf")) {
            javafx.scene.text.Font fxBase = javafx.scene.text.Font.loadFont(fis, 12);
            if (fxBase != null) {
                System.out.println("Custom font loaded successfully: " + fxBase.getName());
                fonts.put("retro", javafx.scene.text.Font.font(fxBase.getFamily(), 24));
                fonts.put("retro_large", javafx.scene.text.Font.font(fxBase.getFamily(), 48));
                fonts.put("retro_small", javafx.scene.text.Font.font(fxBase.getFamily(), 16));
            } else {
                System.err.println("Failed to load custom font - using fallback");
                loadFallbackFonts();
            }
        } catch (Exception e) {
            System.err.println("Error loading custom font: " + e.getMessage());
            e.printStackTrace();
            loadFallbackFonts();
        }
    }
    
    /**
     * Load fallback fonts (system monospaced)
     */
    private void loadFallbackFonts() {
        System.out.println("Loading fallback monospaced fonts");
        fonts.put("retro", Font.font("Monospaced", 24));
        fonts.put("retro_large", Font.font("Monospaced", 48));
        fonts.put("retro_small", Font.font("Monospaced", 16));
    }

    private void loadSingleAnimation(String name, int frameCount, int scale) {
        for (int i = 1; i <= frameCount; i++) {
            String path = name + "/Sprite-" + name + i + ".png";
            sprites.put(name + i, loadImage(path, scale));
        }
    }
    
    private void loadAllAnimations() {
        loadSingleAnimation("Astronaut-Death-Left", 13, PLAYER_SCALE);
        loadSingleAnimation("Astronaut-Death-Right", 13, PLAYER_SCALE);
        loadSingleAnimation("Astronaut-Walking-Left", 2, PLAYER_SCALE);
        loadSingleAnimation("Astronaut-Walking-Right", 2, PLAYER_SCALE);
        loadSingleAnimation("Astronaut-Idle-Right", 2, PLAYER_SCALE);
        loadSingleAnimation("Astronaut-Idle-Left", 2, PLAYER_SCALE);
        loadSingleAnimation("Comms-Destroyed", 4, MODULE_SCALE);
        loadSingleAnimation("Comms-Repaired", 8, MODULE_SCALE);
        loadSingleAnimation("Servers-Destroyed", 5, MODULE_SCALE);
        loadSingleAnimation("Servers-Repaired", 7, MODULE_SCALE);
        loadSingleAnimation("Starship-Burning", 6, SHIP_SCALE);
        loadSingleAnimation("Starship-Destroyed", 6, SHIP_SCALE);
    }

    private void loadAllSprites() {
        //Load individual sprites
        String[] spriteNames = {
            "Sprite-Wing-Damaged1",
            "Sprite-Starship-Repaired1"
        };

        for (String name : spriteNames) {
            String path = name + ".png";
            sprites.put(name, loadImage(path, SHIP_SCALE));
        }
    }
    /**
     * Load single image file
     */
    private Image loadImage(String relativePath, int scale) {
        try {
            String fullPath = assetsPath + "/sprites/" + relativePath;
            File file = new File(fullPath);
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                // Load original size first
                Image original = new Image(fis, 0, 0, true, false);
                fis.close();
                
                // Calculate scaled dimensions
                int scaledWidth = (int)(original.getWidth() * scale);
                int scaledHeight = (int)(original.getHeight() * scale);
                
                // Reload with specific size and NO SMOOTHING
                fis = new FileInputStream(file);
                Image scaled = new Image(fis, scaledWidth, scaledHeight, true, false);
                fis.close();
                
                System.out.println("  Loaded (scaled " + scale + "x): " + relativePath);
                return scaled;
            } else {
                System.err.println("  File not found: " + fullPath);
            }
        } catch (Exception e) {
            System.err.println("  Failed to load: " + relativePath + " - " + e.getMessage());
        }
        return null;
    }

    /**
     * Load background sprites
     */
    private void loadBackgrounds() {
        // TODO: Load from assets/sprites/backgrounds/
        
        // Create placeholder backgrounds for each level
        for (int i = 1; i <= 10; i++) {
            sprites.put("background_level" + i, 
                       createPlaceholder(1280, 720, Color.rgb(0, 20, 0), ""));
        }
    }
    
    
    /**
     * Create a placeholder image with color and optional text
     */
    private Image createPlaceholder(int width, int height, Color color, String text) {
        WritableImage img = new WritableImage(width, height);
        PixelWriter pw = img.getPixelWriter();
        
        // Fill with color
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Create border
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    pw.setColor(x, y, Color.WHITE);
                } else {
                    pw.setColor(x, y, color);
                }
            }
        }
        
        // TODO: Draw text on placeholder (would need Canvas for proper text rendering)
        // For now, the colored rectangles with borders are sufficient
        
        return img;
    }
    
    // ===== GETTER METHODS =====
    
    public Font getFont(String name) {
        Font font = fonts.get(name);
        if (font == null) {
            System.err.println("Font not found: " + name);
            return Font.getDefault();
        }
        return font;
    }

    public Image getSprite(String name) {
        Image sprite = sprites.get(name);
        if (sprite == null) {
            System.err.println("Sprite not found: " + name);
            return createPlaceholder(64, 64, Color.MAGENTA, "?");
        }
        return sprite;
    }

    public Image[] getAnimationFrames(String baseName, int frameCount) {
        Image[] frames = new Image[frameCount];
        for (int i = 1; i < frameCount+1; i++) {
            frames[i-1] = getSprite(baseName + i);
        }
        return frames;
    }


    
}