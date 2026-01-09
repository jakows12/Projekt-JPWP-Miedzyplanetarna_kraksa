package com.interplanetarycrash.tasks;

/**
 * Types of tasks available in the game
 */
public enum TaskType {
    MULTIPLE_CHOICE("ABCD", "Multiple choice question"),
    FREQUENCY_SPECTRUM("FREQ", "Match frequency spectrum"),
    LOGIC_GATES("LOGIC", "Logic gates puzzle");
    
    private final String code;
    private final String displayName;
    
    TaskType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Get TaskType from code string
     */
    public static TaskType fromCode(String code) {
        for (TaskType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown task type code: " + code);
    }
}