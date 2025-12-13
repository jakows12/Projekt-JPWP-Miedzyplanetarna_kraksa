package com.interplanetarycrash.rendering;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Handles all rendering operations
 * Coordinates are in logical space (1920x1080) and automatically scaled
 */
public class GameRenderer {
    
    private final Canvas canvas;
    private final GraphicsContext gc;
    
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    
    // Retro green color scheme
    public static final Color RETRO_GREEN = Color.rgb(0, 255, 0);
    public static final Color RETRO_GREEN_DARK = Color.rgb(0, 180, 0);
    public static final Color RETRO_GREEN_DARKER = Color.rgb(0, 100, 0);
    public static final Color RETRO_BACKGROUND = Color.rgb(0, 20, 0);
    
    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        
        // Setup default rendering settings
        gc.setImageSmoothing(false); // Pixel-perfect rendering for retro look
    }
    
    /**
     * Clear screen with background color
     */
    public void clear() {
        gc.save();
        gc.setFill(RETRO_BACKGROUND);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.restore();
        
        // DEBUG: Uncomment to verify clear is being called
        // System.out.println("Screen cleared");
    }
    
    /**
     * Set scale factors for responsive rendering
     */
    public void setScale(double scaleX, double scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    /**
     * Convert logical X coordinate to screen coordinate
     */
    private double toScreenX(double logicalX) {
        return logicalX * scaleX;
    }
    
    /**
     * Convert logical Y coordinate to screen coordinate
     */
    private double toScreenY(double logicalY) {
        return logicalY * scaleY;
    }
    
    /**
     * Convert logical width to screen width
     */
    private double toScreenWidth(double logicalWidth) {
        return logicalWidth * scaleX;
    }
    
    /**
     * Convert logical height to screen height
     */
    private double toScreenHeight(double logicalHeight) {
        return logicalHeight * scaleY;
    }
    
    // ===== DRAWING METHODS =====
    
    /**
     * Draw image at logical coordinates
     */
    public void drawImage(Image image, double x, double y) {
        if (image == null) return;
        gc.drawImage(image, toScreenX(x), toScreenY(y), 
                     toScreenWidth(image.getWidth()), toScreenHeight(image.getHeight()));
    }
    
    /**
     * Draw image with specified size
     */
    public void drawImage(Image image, double x, double y, double width, double height) {
        if (image == null) return;
        gc.drawImage(image, toScreenX(x), toScreenY(y), 
                     toScreenWidth(width), toScreenHeight(height));
    }
    
    /**
     * Draw rectangle
     */
    public void drawRect(double x, double y, double width, double height, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(2 * Math.min(scaleX, scaleY));
        gc.strokeRect(toScreenX(x), toScreenY(y), 
                      toScreenWidth(width), toScreenHeight(height));
    }
    
    /**
     * Fill rectangle
     */
    public void fillRect(double x, double y, double width, double height, Color color) {
        gc.setFill(color);
        gc.fillRect(toScreenX(x), toScreenY(y), 
                    toScreenWidth(width), toScreenHeight(height));
    }
    
    /**
     * Draw circle
     */
    public void drawCircle(double centerX, double centerY, double radius, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(2 * Math.min(scaleX, scaleY));
        double diameter = radius * 2;
        gc.strokeOval(toScreenX(centerX - radius), toScreenY(centerY - radius),
                      toScreenWidth(diameter), toScreenHeight(diameter));
    }
    
    /**
     * Fill circle
     */
    public void fillCircle(double centerX, double centerY, double radius, Color color) {
        gc.setFill(color);
        double diameter = radius * 2;
        gc.fillOval(toScreenX(centerX - radius), toScreenY(centerY - radius),
                    toScreenWidth(diameter), toScreenHeight(diameter));
    }
    
    /**
     * Draw text with automatic scaling
     */
    public void drawText(String text, double x, double y, Font font, Color color) {
        gc.save();
        gc.setFill(color);
        
        // Scale font
        double scaledSize = font.getSize() * Math.min(scaleX, scaleY);
        gc.setFont(Font.font(font.getFamily(), scaledSize));
        
        gc.fillText(text, toScreenX(x), toScreenY(y));
        gc.restore();
    }
    
    /**
     * Draw centered text
     */
    public void drawCenteredText(String text, double centerX, double y, Font font, Color color) {
        gc.save();
        gc.setFill(color);
        gc.setTextAlign(TextAlignment.CENTER);
        
        // Scale font
        double scaledSize = font.getSize() * Math.min(scaleX, scaleY);
        gc.setFont(Font.font(font.getFamily(), scaledSize));
        
        gc.fillText(text, toScreenX(centerX), toScreenY(y));
        gc.restore();
    }
    
    /**
     * Draw line
     */
    public void drawLine(double x1, double y1, double x2, double y2, Color color, double width) {
        gc.setStroke(color);
        gc.setLineWidth(width * Math.min(scaleX, scaleY));
        gc.strokeLine(toScreenX(x1), toScreenY(y1), toScreenX(x2), toScreenY(y2));
    }
    
    /**
     * Get graphics context for custom drawing
     */
    public GraphicsContext getGraphicsContext() {
        return gc;
    }
    
    /**
     * Save graphics context state
     */
    public void save() {
        gc.save();
    }
    
    /**
     * Restore graphics context state
     */
    public void restore() {
        gc.restore();
    }
}