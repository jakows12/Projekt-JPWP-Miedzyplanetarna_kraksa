package com.interplanetarycrash.tasks;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.interplanetarycrash.tasks.FrequencySpectrumTask.WaveformType;
import com.interplanetarycrash.tasks.LogicGatesTask.GateType;

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
            java.io.FileInputStream stream = new java.io.FileInputStream(System.getProperty("user.dir") + "/interplanetary-crash/src/main/assets/tasks/" + filename);
            
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
                                       
                default:
                    System.err.println("Unknown task type: " + type);
                    return createFallbackTask();
            }
            
        } catch (Exception e) {
            System.err.println("Error loading task from " + filename + ": " + e.getMessage());
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
     * 
     * Format (OLD - backward compatible):
     * FREQ
     * Adjust the function parameters to match the frequency spectrum
     * 5.0,50.0,0.0 (target: amplitude,frequency,phase)
     * 0.0,10.0 (amplitude range: min,max)
     * 10.0,100.0 (frequency range: min,max)
     * 0.0,6.28 (phase range: min,max)
     * 0.5 (tolerance)
     * 2 (difficulty)
     * 
     * Format (NEW - with waveform):
     * FREQ
     * Match the signal waveform and parameters
     * 5.0,50.0,0.0,SQUARE (target: amplitude,frequency,phase,waveform)
     * 0.0,10.0 (amplitude range: min,max)
     * 10.0,100.0 (frequency range: min,max)
     * 0.0,6.28 (phase range: min,max)
     * 0.5 (tolerance)
     * 2 (difficulty)
     */
    private static Task loadFrequencySpectrumTask(List<String> lines) {
        if (lines.size() < 8) {
            throw new IllegalArgumentException("FrequencySpectrum task needs at least 8 lines");
        }
        
        String instruction = lines.get(1).trim();
        
        // Parse target values (check if waveform is specified)
        String[] targets = lines.get(2).split(",");
        double targetAmplitude = Double.parseDouble(targets[0].trim());
        double targetFrequency = Double.parseDouble(targets[1].trim());
        double targetPhase = Double.parseDouble(targets[2].trim());
        
        // Check if waveform type is specified (NEW FORMAT)
        WaveformType targetWaveform = WaveformType.SINE; // Default
        if (targets.length >= 4) {
            try {
                targetWaveform = WaveformType.valueOf(targets[3].trim().toUpperCase());
                System.out.println("  Loaded waveform type: " + targetWaveform);
            } catch (IllegalArgumentException e) {
                System.err.println("  Unknown waveform type: " + targets[3] + ", using SINE");
            }
        }
        
        // Parse ranges
        String[] ampRange = lines.get(3).split(",");
        double ampMin = Double.parseDouble(ampRange[0].trim());
        double ampMax = Double.parseDouble(ampRange[1].trim());
        
        String[] freqRange = lines.get(4).split(",");
        double freqMin = Double.parseDouble(freqRange[0].trim());
        double freqMax = Double.parseDouble(freqRange[1].trim());
        
        String[] phaseRange = lines.get(5).split(",");
        double phaseMin = Double.parseDouble(phaseRange[0].trim());
        double phaseMax = Double.parseDouble(phaseRange[1].trim());
        
        double tolerance = Double.parseDouble(lines.get(6).trim());
        int difficulty = Integer.parseInt(lines.get(7).trim());
        
        // Create task with waveform parameter (uses new constructor)
        return new FrequencySpectrumTask(
            instruction,
            targetAmplitude, targetFrequency, targetPhase, targetWaveform,
            ampMin, ampMax,
            freqMin, freqMax,
            phaseMin, phaseMax,
            tolerance,
            difficulty
        );
    }
    
 /**
     * Load Logic Gates task
     * Format:
     * LOGIC
     * Build circuit for: F(A,B) = A NAND B
     * 1,1,0  (truth table row: A, B, F)
     * 1,0,1
     * 0,1,1
     * 0,0,1
     * NAND,NOT  (available gates)
     * 2  (difficulty)
     */
    private static Task loadLogicGatesTask(List<String> lines) {
        if (lines.size() < 4) {
            throw new IllegalArgumentException("LogicGates task needs at least 4 lines");
        }
        
        String instruction = lines.get(1).trim();
        
        // Parse truth table (all lines until we hit gate list)
        List<boolean[]> truthTable = new ArrayList<>();
        int lineIndex = 2;
        
        while (lineIndex < lines.size()) {
            String line = lines.get(lineIndex).trim();
            
            // Check if this is the gate list (contains letters)
            if (line.matches(".*[A-Z]+.*") && line.contains(",") && 
                line.toUpperCase().contains("AND") || line.toUpperCase().contains("OR") || 
                line.toUpperCase().contains("NOT") || line.toUpperCase().contains("XOR")) {
                break; // This is the gate list
            }
            
            // Parse as truth table row
            String[] parts = line.split(",");
            boolean[] row = new boolean[parts.length];
            for (int i = 0; i < parts.length; i++) {
                row[i] = parts[i].trim().equals("1");
            }
            truthTable.add(row);
            lineIndex++;
        }
        
        // Parse available gates
        if (lineIndex >= lines.size()) {
            throw new IllegalArgumentException("Missing available gates line");
        }
        
        String[] gateStrs = lines.get(lineIndex).split(",");
        Set<GateType> availableGates = new HashSet<>();
        for (String gateStr : gateStrs) {
            try {
                availableGates.add(GateType.valueOf(gateStr.trim().toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("Unknown gate type: " + gateStr);
            }
        }
        
        lineIndex++;
        
        // Parse difficulty
        int difficulty = 2;
        if (lineIndex < lines.size()) {
            difficulty = Integer.parseInt(lines.get(lineIndex).trim());
        }
        
        return new LogicGatesTask(instruction, truthTable, availableGates, difficulty);
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
