package com.interplanetarycrash.tasks;

import com.interplanetarycrash.core.Game;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from text files in assets/tasks/
 * 
 * File naming convention:
 * assets/tasks/level1_module1.txt
 * assets/tasks/level1_module2.txt
 * assets/tasks/level2_module1.txt
 * etc.
 */
public class TaskLoader {
    
    /**
     * Load task from file
     * @param filename Filename relative to assets/tasks/ (e.g. "level1_module1.txt")
     * @return Loaded task or null if error
     */

    public static Task loadTask(String filename) {
        try {
            // Try to load from resources
            java.io.FileInputStream stream = new java.io.FileInputStream(System.getProperty("user.dir") + "\\interplanetary-crash\\src\\main\\assets\\tasks\\" + filename);
            
            List<String> lines = readLines(stream);
            
            if (lines.isEmpty()) {
                System.err.println("Task file is empty: " + filename);
                return createFallbackTask();
            }
            
            String typeCode = lines.get(0).trim();
            TaskType type = TaskType.fromCode(typeCode);
            
            System.out.println("Loading task type: " + type + " from " + filename);
            
            // Load based on type
            switch (type) {
                case MULTIPLE_CHOICE:
                    return loadMultipleChoiceTask(lines);
                    
                case FREQUENCY_SPECTRUM:
                    return loadFrequencySpectrumTask(lines);
                    
                case LOGIC_GATES:
                    return loadLogicGatesTask(lines);
                    
                case CIRCUIT:
                    return loadCircuitTask(lines);
                    
                default:
                    System.err.println("Unknown task type: " + type);
                    return createFallbackTask();
            }
            
        } catch (Exception e) {
            System.err.println("Error loading task from " + filename + ": " + e.getMessage());
            e.printStackTrace();
            return createFallbackTask();
        }
    }
    
    /**
     * Load Multiple Choice task
     * Format:
     * ABCD
     * Question text?
     * Answer A
     * Answer B
     * Answer C
     * Answer D
     * B (correct answer)
     * 1 (difficulty)
     */
    private static Task loadMultipleChoiceTask(List<String> lines) {
        if (lines.size() < 8) {
            throw new IllegalArgumentException("MultipleChoice task needs at least 8 lines");
        }
        
        String question = lines.get(1).trim();
        String[] answers = new String[4];
        answers[0] = lines.get(2).trim();
        answers[1] = lines.get(3).trim();
        answers[2] = lines.get(4).trim();
        answers[3] = lines.get(5).trim();
        
        char correctAnswer = lines.get(6).trim().toUpperCase().charAt(0);
        int difficulty = Integer.parseInt(lines.get(7).trim());
        
        return new MultipleChoiceTask(question, answers, correctAnswer, difficulty);
    }
    
    /**
     * Load Frequency Spectrum task
     * Format:
     * FREQ
     * Adjust the function parameters to match the frequency spectrum
     * amplitude,frequency,phase (target values)
     * amplitude_min,amplitude_max
     * frequency_min,frequency_max
     * phase_min,phase_max
     * tolerance
     * 2 (difficulty)
     */
    private static Task loadFrequencySpectrumTask(List<String> lines) {
        // TODO: Implement when FrequencySpectrumTask is created
        System.out.println("FrequencySpectrumTask loading - not yet implemented");
        return createFallbackTask();
    }
    
    /**
     * Load Logic Gates task
     * Format:
     * LOGIC
     * Connect logic gates to produce the correct output
     * input1,input2,input3 (input values)
     * expected_output
     * available_gates (AND,OR,NOT,NAND,NOR,XOR)
     * 3 (difficulty)
     */
    private static Task loadLogicGatesTask(List<String> lines) {
        // TODO: Implement when LogicGatesTask is created
        System.out.println("LogicGatesTask loading - not yet implemented");
        return createFallbackTask();
    }
    
    /**
     * Load Circuit task
     * Format:
     * CIRCUIT
     * Calculate the total resistance of the circuit
     * resistor_values (R1,R2,R3,...)
     * circuit_configuration (series/parallel/mixed)
     * correct_answer
     * tolerance
     * 2 (difficulty)
     */
    private static Task loadCircuitTask(List<String> lines) {
        // TODO: Implement when CircuitTask is created
        System.out.println("CircuitTask loading - not yet implemented");
        return createFallbackTask();
    }
    
    /**
     * Read all lines from input stream
     */
    private static List<String> readLines(InputStream stream) throws Exception {
        List<String> lines = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines and comments
                if (!line.trim().isEmpty() && !line.trim().startsWith("#")) {
                    lines.add(line);
                }
            }
        }
        
        return lines;
    }
    
    /**
     * Create a simple fallback task when loading fails
     */
    private static Task createFallbackTask() {
        System.out.println("Creating fallback task");
        
        return new MultipleChoiceTask(
            "Fallback question: What is 2 + 2?",
            new String[] {
                "3",
                "4",
                "5",
                "6"
            },
            'B',
            1
        );
    }
    
    /**
     * Get task filename for level and module
     */
    public static String getTaskFilename(int level, int moduleIndex) {
        return String.format("level%d_module%d.txt", level, moduleIndex + 1);
    }
}
