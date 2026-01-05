package com.interplanetarycrash.ui;

import com.interplanetarycrash.rendering.GameRenderer;
import com.interplanetarycrash.assets.AssetManager;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Life support bar UI element
 * Shows remaining oxygen/life support as a horizontal bar
 */
public class LifeSupportBar {
    
    private double x, y;
    private double width, height;
    
    private Font labelFont;
    
    public LifeSupportBar(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        AssetManager asset = AssetManager.getInstance();
        this.labelFont = asset.getFont("retro");
    }
    
    /**
     * Render the life support bar
     * @param percentage 0-100
     */
    public void render(GameRenderer renderer, float percentage) {
        // Clamp percentage
        percentage = Math.max(0, Math.min(100, percentage));
        
        // Background (empty bar)
        renderer.fillRect(x, y, width, height, Color.rgb(20, 20, 20));
        
        // Border
        renderer.drawRect(x, y, width, height, GameRenderer.RETRO_GREEN);
        
        // Fill (life support remaining)
        double fillWidth = (width - 4) * (percentage / 100.0);
        Color fillColor = getColorForPercentage(percentage);
        renderer.fillRect(x + 2, y + 2, fillWidth, height - 4, fillColor);
        
        // Label
        renderer.drawText("LIFE SUPPORT", x, y - 5, labelFont, GameRenderer.RETRO_GREEN);
        
        // Percentage text
        String percentText = String.format("%.0f%%", percentage);
        renderer.drawCenteredText(percentText, x + width / 2, y + height / 2 + 6, 
                                 labelFont, Color.WHITE);
    }
    
    /**
     * Get bar color based on percentage (green -> yellow -> red)
     */
    private Color getColorForPercentage(float percentage) {
        if (percentage > 50) {
            // Green to yellow
            double t = (100 - percentage) / 50.0;
            return Color.rgb(
                (int)(0 + 255 * t),
                (int)(255),
                0
            );
        } else {
            // Yellow to red
            double t = (50 - percentage) / 50.0;
            return Color.rgb(
                255,
                (int)(255 - 255 * t),
                0
            );
        }
    }
}
