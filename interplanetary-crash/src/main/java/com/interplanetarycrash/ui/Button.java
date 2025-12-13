package com.interplanetarycrash.ui;

import com.interplanetarycrash.rendering.GameRenderer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Interactive button UI element - KEYBOARD NAVIGATION
 * Use arrow keys to navigate, ENTER/SPACE to activate
 */
public class Button {
    
    private String text;
    private double x, y, width, height;
    private boolean selected; // Selected with keyboard navigation
    private boolean enabled;
    private Runnable onClick;
    private final Font font;

    
    private static final Color NORMAL_COLOR = GameRenderer.RETRO_GREEN_DARK;
    private static final Color SELECTED_COLOR = GameRenderer.RETRO_GREEN;
    private static final Color DISABLED_COLOR = Color.rgb(50, 50, 50);
    private static final Color TEXT_COLOR = GameRenderer.RETRO_GREEN;
    private static final Color TEXT_SELECTED_COLOR = Color.BLACK;
    
    public Button(String text, double x, double y, double width, double height, Font font) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.enabled = true;
        this.selected = false;
        this.font = font;
    }
    
    /**
     * Render button
     */
    public void render(GameRenderer renderer) {
        Color bgColor = enabled ? (selected ? SELECTED_COLOR : NORMAL_COLOR) : DISABLED_COLOR;
        Color textColor = enabled ? (selected ? TEXT_SELECTED_COLOR : TEXT_COLOR) : Color.GRAY;
        
        // Draw background
        renderer.fillRect(x, y, width, height, bgColor);
        
        // Draw border (thicker if selected)
        if (selected && enabled) {
            renderer.drawRect(x - 5, y - 5, width + 10, height + 10, TEXT_COLOR);
        }
        renderer.drawRect(x, y, width, height, TEXT_COLOR);
        
        // Draw text centered
        renderer.drawCenteredText(text, x + width / 2, y + height / 2 + 8, font, textColor);
        
        // Draw arrow indicator if selected
        if (selected && enabled) {
            renderer.drawText(">", x - 40, y + height / 2 + 10, font, TEXT_COLOR);
            renderer.drawText("<", x + width + 15, y + height / 2 + 10, font, TEXT_COLOR);
        }
    }
    
    /**
     * Execute onClick action
     */
    public void activate() {
        if (enabled && onClick != null) {
            onClick.run();
        }
    }
    
    // Getters and setters
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setOnClick(Runnable onClick) { this.onClick = onClick; }
    public void setText(String text) { this.text = text; }
    public String getText() { return text; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}
