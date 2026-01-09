package com.interplanetarycrash.ui;

import com.interplanetarycrash.assets.AssetManager;
import com.interplanetarycrash.rendering.GameRenderer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Shows "Press E to interact" prompt when near interactable objects
 */
public class InteractionPrompt {
    
    private String text;
    private double x, y;
    private boolean visible;
    private Font font;
    
    
    public InteractionPrompt() {
        AssetManager asset = AssetManager.getInstance();
        this.font = asset.getFont("retro");
        this.visible = false;
    }
    
    /**
     * Show prompt at position with text
     */
    public void show(String text, double x, double y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.visible = true;
    }
    
    /**
     * Hide prompt
     */
    public void hide() {
        this.visible = false;
    }
    
    /**
     * Update (for blink animation)
     */
    public void update(double deltaTime) {

    }
    
    /**
     * Render prompt
     */
    public void render(GameRenderer renderer) {
        if (!visible) return;
        
        
        // Background
        double padding = 10;
        double textWidth = text.length() * 12; // Approximate
        double textHeight = 30;
        
        renderer.fillRect(
            x - textWidth / 2 - padding,
            y - textHeight / 2 - padding,
            textWidth + padding * 2,
            textHeight + padding * 2,
            Color.rgb(0, 0, 0, 0.8)
        );
        
        // Border
        renderer.drawRect(
            x - textWidth / 2 - padding,
            y - textHeight / 2 - padding,
            textWidth + padding * 2,
            textHeight + padding * 2,
            GameRenderer.RETRO_GREEN
        );
        
        // Text
        renderer.drawCenteredText(text, x, y + 7, font, GameRenderer.RETRO_GREEN);
    }
    
    public boolean isVisible() { return visible; }
}
