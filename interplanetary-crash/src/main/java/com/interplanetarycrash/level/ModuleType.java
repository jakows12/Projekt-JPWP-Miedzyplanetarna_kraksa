package com.interplanetarycrash.level;

/**
 * Types of ship modules that can be repaired
 */
public enum ModuleType {
    ENGINE("Silnik"),
    NAVIGATION("Nawigacja"),
    LIFE_SUPPORT("Podtrzymywanie Życia"),
    COMMUNICATION("Komunikacja");
    
    private final String displayName;
    
    ModuleType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
