package com.interplanetarycrash.level;

/**
 * Types of ship modules that can be repaired
 */
public enum ModuleType {
    SERVERS("servers"),
    COMMUNICATION("comms"),
    ENGINE("engine"),
    WING("wing");
    
    private final String displayName;
    
    ModuleType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getProperName() {
        return displayName;
    }
}
