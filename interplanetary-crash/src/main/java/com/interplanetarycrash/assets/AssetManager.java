package com.interplanetarycrash.assets;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class for managing game assets (sprites, fonts, sounds)
 * Creates placeholder graphics when actual assets are not available
 */
public class AssetManager {
    
    private static AssetManager instance;
    
    private final Map<String, Image> sprites = new HashMap<>();
    private final Map<String, Font> fonts = new HashMap<>();
    
    private AssetManager() {}
    
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
        loadUISprites();
        
        System.out.println("Assets loaded successfully!");
    }
    
    /**
     * Load fonts
     */
    private void loadFonts() {
        // Try to load custom font from file (JavaFX)
        try (java.io.FileInputStream fis = new java.io.FileInputStream("C:\\Users\\jakow\\Desktop\\PG\\3_rok\\JPWP\\kod\\Projekt-JPWP-Miedzyplanetarna_kraksa\\interplanetary-crash\\src\\main\\assets\\fonts\\BoldPixels.ttf")) {
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
    
    /**
     * Load background sprites
     */
    private void loadBackgrounds() {
        // TODO: Load from assets/sprites/backgrounds/
        
        // Create placeholder backgrounds for each level
        for (int i = 1; i <= 10; i++) {
            sprites.put("background_level" + i, 
                       createPlaceholder(1920, 1080, Color.rgb(0, 20 + i * 5, 0), ""));
        }
    }
    
    /**
     * Load UI sprites
     */
    private void loadUISprites() {
        // TODO: Load UI elements
        sprites.put("button", createPlaceholder(200, 60, Color.rgb(0, 100, 0), ""));
        sprites.put("button_hover", createPlaceholder(200, 60, Color.rgb(0, 150, 0), ""));
        sprites.put("button_locked", createPlaceholder(200, 60, Color.rgb(50, 50, 50), ""));
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
    
}