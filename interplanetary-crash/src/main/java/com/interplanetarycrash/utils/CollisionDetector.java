package com.interplanetarycrash.utils;

import javafx.geometry.Rectangle2D;

/**
 * Utility class for collision detection
 */
public class CollisionDetector {
    
    /**
     * Check if two rectangles intersect
     */
    public static boolean checkCollision(Rectangle2D a, Rectangle2D b) {
        return a.intersects(b);
    }

}
