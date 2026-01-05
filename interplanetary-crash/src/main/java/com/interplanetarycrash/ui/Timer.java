package com.interplanetarycrash.ui;

import com.interplanetarycrash.rendering.GameRenderer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Timer UI element - displays elapsed time
 */
public class Timer {
    
    private double x, y;
    private Font font;
    
    public Timer(double x, double y) {
        this.x = x;
        this.y = y;
        this.font = Font.font("Monospaced", 24);
    }
    
    /**
     * Render timer
     * @param elapsedTime Time in seconds
     */
    public void render(GameRenderer renderer, float elapsedTime) {
        // Format time as MM:SS.ms
        int minutes = (int)(elapsedTime / 60);
        int seconds = (int)(elapsedTime % 60);
        int milliseconds = (int)((elapsedTime % 1) * 100);
        
        String timeString = String.format("%02d:%02d.%02d", minutes, seconds, milliseconds);
        
        // Render with background for readability
        double textWidth = 150;
        double textHeight = 35;
        
        renderer.fillRect(x - 5, y - 25, textWidth, textHeight, Color.rgb(0, 0, 0, 0.7));
        renderer.drawRect(x - 5, y - 25, textWidth, textHeight, GameRenderer.RETRO_GREEN);
        
        renderer.drawText(timeString, x, y, font, GameRenderer.RETRO_GREEN);
    }
}
