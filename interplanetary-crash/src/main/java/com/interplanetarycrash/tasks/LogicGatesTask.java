package com.interplanetarycrash.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.interplanetarycrash.GameApplication;
import com.interplanetarycrash.assets.AssetManager;
import com.interplanetarycrash.input.InputHandler;
import com.interplanetarycrash.rendering.GameRenderer;

import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Logic Gates Task - Build a circuit using logic gates and wires
 * NOW WITH WORKING SIMULATION!
 */
public class LogicGatesTask extends Task {
    
    private List<boolean[]> truthTable;
    private int numInputs;
    private Set<GateType> availableGates;
    
    // Grid system
    private static final int GRID_COLS = 25;
    private static final int GRID_ROWS = 11;
    private static final double CELL_SIZE = 35;
    private static final double GRID_START_X = 80;
    private static final double GRID_START_Y = 180;
    
    private GridCell[][] grid;
    private List<Gate> placedGates;
    
    private int cursorX, cursorY;
    private GateType selectedGateType;
    
    private boolean drawingWire;
    private int wireStartX, wireStartY;
    
    private Font titleFont;
    private Font labelFont;
    private Font smallFont;
    
    public LogicGatesTask(String instruction, List<boolean[]> truthTable,
                         Set<GateType> availableGates, int difficulty) {
        super(TaskType.LOGIC_GATES, instruction, difficulty);
        
        this.truthTable = truthTable;
        this.numInputs = truthTable.get(0).length - 1;
        this.availableGates = availableGates;
        this.placedGates = new ArrayList<>();
        
        initializeGrid();
        
        cursorX = GRID_COLS / 2;
        cursorY = GRID_ROWS / 2;
        
        if (!availableGates.isEmpty()) {
            selectedGateType = availableGates.iterator().next();
        }
        
        drawingWire = false;
    }
    
    private void initializeGrid() {
        grid = new GridCell[GRID_ROWS][GRID_COLS];
        for (int y = 0; y < GRID_ROWS; y++) {
            for (int x = 0; x < GRID_COLS; x++) {
                grid[y][x] = new GridCell();
            }
        }
        
        // Place inputs on left (column 1)
        for (int i = 0; i < numInputs; i++) {
            int y = 2 + i * 3;
            if (y < GRID_ROWS) {
                grid[y][1].type = ComponentType.INPUT;
                grid[y][1].inputIndex = i;
            }
        }
        
        // Place output on right (column GRID_COLS-2)
        int outputY = GRID_ROWS / 2;
        grid[outputY][GRID_COLS - 2].type = ComponentType.OUTPUT;
    }
    
    @Override
    public void update(double deltaTime, InputHandler input) {
        if (completed) return;
        
        // Move cursor
        if (input.isKeyJustPressed(KeyCode.UP) || input.isKeyJustPressed(KeyCode.W)) {
            cursorY = Math.max(0, cursorY - 1);
        }
        if (input.isKeyJustPressed(KeyCode.DOWN) || input.isKeyJustPressed(KeyCode.S)) {
            cursorY = Math.min(GRID_ROWS - 1, cursorY + 1);
        }
        if (input.isKeyJustPressed(KeyCode.LEFT) || input.isKeyJustPressed(KeyCode.A)) {
            cursorX = Math.max(0, cursorX - 1);
        }
        if (input.isKeyJustPressed(KeyCode.RIGHT) || input.isKeyJustPressed(KeyCode.D)) {
            cursorX = Math.min(GRID_COLS - 1, cursorX + 1);
        }
        
        // Cycle gates
        if (input.isKeyJustPressed(KeyCode.Q)) {
            cyclePreviousGate();
        }
        if (input.isKeyJustPressed(KeyCode.E)) {
            cycleNextGate();
        }
        
        // Place gate
        if (input.isKeyJustPressed(KeyCode.SPACE)) {
            placeGate();
        }
        
        // Wire drawing
        if (input.isKeyJustPressed(KeyCode.F)) {
            toggleWireDrawing();
        }
        
        // Delete
        if (input.isKeyJustPressed(KeyCode.BACK_SPACE)) {
            deleteComponent();
        }
        
        // Test circuit
        if (input.isConfirming()) {
            submitAnswer();
        }
    }
    
    private void cycleNextGate() {
        List<GateType> gates = new ArrayList<>(availableGates);
        int index = gates.indexOf(selectedGateType);
        selectedGateType = gates.get((index + 1) % gates.size());
    }
    
    private void cyclePreviousGate() {
        List<GateType> gates = new ArrayList<>(availableGates);
        int index = gates.indexOf(selectedGateType);
        selectedGateType = gates.get((index - 1 + gates.size()) % gates.size());
    }
    
    private void placeGate() {
        // Check bounds
        if (cursorX < 1 || cursorX >= GRID_COLS - 1 ||
            cursorY < 1 || cursorY >= GRID_ROWS - 1) {
            return;
        }
        
        // Check if 3x3 area is clear
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                GridCell cell = grid[cursorY + dy][cursorX + dx];
                if (cell.type != ComponentType.EMPTY && cell.type != ComponentType.WIRE) {
                    return;
                }
            }
        }
        
        // Create gate
        Gate gate = new Gate(selectedGateType, cursorX, cursorY);
        placedGates.add(gate);
        
        // Mark grid
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                GridCell cell = grid[cursorY + dy][cursorX + dx];
                cell.type = ComponentType.GATE;
                cell.gate = gate;
                System.out.println("Marking cell at (" + (cursorX + dx) + "," + (cursorY + dy) + ") as part of gate");
            }
        }
        
        System.out.println("Placed " + selectedGateType + " gate at (" + cursorX + "," + cursorY + ")");
    }
    
    private void toggleWireDrawing() {
        if (!drawingWire) {
            wireStartX = cursorX;
            wireStartY = cursorY;
            drawingWire = true;
        } else {
            drawWireLine(wireStartX, wireStartY, cursorX, cursorY);
            drawingWire = false;
        }
    }
    
    private void drawWireLine(int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            // Vertical
            int startY = Math.min(y1, y2);
            int endY = Math.max(y1, y2);
            for (int y = startY; y <= endY; y++) {
                if (grid[y][x1].type == ComponentType.EMPTY) {
                    grid[y][x1].type = ComponentType.WIRE;
                }
            }
        } else if (y1 == y2) {
            // Horizontal
            int startX = Math.min(x1, x2);
            int endX = Math.max(x1, x2);
            for (int x = startX; x <= endX; x++) {
                if (grid[y1][x].type == ComponentType.EMPTY) {
                    grid[y1][x].type = ComponentType.WIRE;
                }
            }
        } else {
            // L-shaped
            for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
                if (grid[y1][x].type == ComponentType.EMPTY) {
                    grid[y1][x].type = ComponentType.WIRE;
                }
            }
            for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
                if (grid[y][x2].type == ComponentType.EMPTY) {
                    grid[y][x2].type = ComponentType.WIRE;
                }
            }
        }
    }
    
    private void deleteComponent() {
        GridCell cell = grid[cursorY][cursorX];
        
        if (cell.type == ComponentType.GATE && cell.gate != null) {
            Gate gateToRemove = cell.gate;
            placedGates.remove(gateToRemove);
            
            // Clear 3x3 area
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    int gy = gateToRemove.centerY + dy;
                    int gx = gateToRemove.centerX + dx;
                    if (gy >= 0 && gy < GRID_ROWS && gx >= 0 && gx < GRID_COLS) {
                        grid[gy][gx].type = ComponentType.EMPTY;
                        grid[gy][gx].gate = null;
                    }
                }
            }
            System.out.println("Deleted gate at (" + gateToRemove.centerX + "," + gateToRemove.centerY + ")");
        } else if (cell.type == ComponentType.WIRE) {
            cell.type = ComponentType.EMPTY;
        }
    }
    
    @Override
    public void render(GameRenderer renderer) {
        AssetManager asset = AssetManager.getInstance();
        titleFont = asset.getFont("retro_large");
        labelFont = asset.getFont("retro");
        smallFont = asset.getFont("retro_small");
        
        renderer.fillRect(0, 0, GameApplication.LOGICAL_WIDTH,
                         GameApplication.LOGICAL_HEIGHT,
                         Color.rgb(0, 10, 0));
        
        renderer.drawCenteredText(
            "REPAIR MODULE - BUILD LOGIC CIRCUIT",
            GameApplication.LOGICAL_WIDTH / 2.0,
            110,
            titleFont,
            GameRenderer.RETRO_GREEN
        );
        
        renderer.drawCenteredText(
            question,
            GameApplication.LOGICAL_WIDTH / 2.0,
            150,
            smallFont,
            GameRenderer.RETRO_GREEN_DARK
        );
        
        drawGrid(renderer);
        drawPalette(renderer);
        drawTruthTable(renderer);
        
        if (completed) {
            renderResult(renderer);
        } else {
            String hint = drawingWire 
                ? "Move to end, press F to finish "
                : "↑↓ ←→ or WSAD: Move | Q/E: Gate | SPACE: Place | F: Wire | BKSP: Delete | ENTER: Test | ESC: Exit";
            
            renderer.drawCenteredText(
                hint,
                GameApplication.LOGICAL_WIDTH / 2.0,
                GameApplication.LOGICAL_HEIGHT - 25,
                smallFont,
                GameRenderer.RETRO_GREEN_DARKER
            );
        }
    }
    
    private void drawGrid(GameRenderer renderer) {
        for (int y = 0; y < GRID_ROWS; y++) {
            for (int x = 0; x < GRID_COLS; x++) {
                double cellX = GRID_START_X + x * CELL_SIZE;
                double cellY = GRID_START_Y + y * CELL_SIZE;
                
                GridCell cell = grid[y][x];
                Gate gate = cell.gate;
                if (gate == null) {
                    Color bgColor = Color.rgb(0, 20, 0);
                    if (x == cursorX && y == cursorY) {
                        bgColor = Color.rgb(0, 50, 0);
                    }
                    renderer.fillRect(cellX, cellY, CELL_SIZE - 2, CELL_SIZE - 2, bgColor);
                    renderer.drawRect(cellX, cellY, CELL_SIZE - 2, CELL_SIZE - 2, 
                                    GameRenderer.RETRO_GREEN_DARKER);
                }
                
                drawCell(renderer, cell, cellX, cellY, x, y);
            }
        }
        
        if (drawingWire) {
            double startCellX = GRID_START_X + wireStartX * CELL_SIZE;
            double startCellY = GRID_START_Y + wireStartY * CELL_SIZE;
            renderer.drawRect(startCellX - 2, startCellY - 2, CELL_SIZE + 2, CELL_SIZE + 2,
                            Color.YELLOW);
        }
    }
    
    private void drawCell(GameRenderer renderer, GridCell cell, double x, double y, int gridX, int gridY) {
        Color color = GameRenderer.RETRO_GREEN;
        
        switch (cell.type) {
            case INPUT:
                char inputName = (char)('A' + cell.inputIndex);
                renderer.fillRect(x+2 , y+2, CELL_SIZE-6, CELL_SIZE-6, 
                                GameRenderer.RETRO_GREEN_DARK);
                renderer.drawRect(x+2, y+2, CELL_SIZE-6, CELL_SIZE-6, color);
                renderer.drawCenteredText(String.valueOf(inputName), 
                                         x + CELL_SIZE / 2, y + CELL_SIZE / 2 + 4,
                                         labelFont, Color.BLACK);
                break;
                
            case OUTPUT:
                renderer.fillRect(x+2 , y+2, CELL_SIZE-6, CELL_SIZE-6, 
                                Color.rgb(0, 30, 0));
                renderer.drawRect(x+2, y+2, CELL_SIZE-6, CELL_SIZE-6, color);
                renderer.drawCenteredText("OUT", x + CELL_SIZE / 2, y + CELL_SIZE / 2 + 3,
                                         smallFont, color);
                break;
                
            case GATE:
                Gate gate = cell.gate;
                if (gate != null && gridX == gate.centerX && gridY == gate.centerY) {
                    drawGate(renderer, gate, x, y);
                }
                break;

                
            case WIRE:
                renderer.drawLine(x + CELL_SIZE / 2, y + 4, 
                                x + CELL_SIZE / 2, y + CELL_SIZE - 4,
                                color, 2);
                renderer.drawLine(x + 4, y + CELL_SIZE / 2, 
                                x + CELL_SIZE - 4, y + CELL_SIZE / 2,
                                color, 2);
                break;
                
            case EMPTY:
                break;
        }
    }
    
    private void drawGate(GameRenderer renderer, Gate gate, double centerX, double centerY) {
        double gateSize = CELL_SIZE * 3 - 3;
        double x = centerX - CELL_SIZE;
        double y = centerY - CELL_SIZE;
        
        renderer.fillRect(x, y, gateSize, gateSize, Color.rgb(0, 40, 0));
        renderer.drawRect(x, y, gateSize, gateSize, GameRenderer.RETRO_GREEN);
        
        renderer.drawCenteredText(gate.type.toString(), 
                                 centerX + CELL_SIZE / 2, 
                                 centerY + CELL_SIZE / 2 + 4,
                                 labelFont, GameRenderer.RETRO_GREEN);
        
        // Inputs
        double inputY1 = centerY - CELL_SIZE / 2;
        double inputY2 = centerY + 3 * CELL_SIZE / 2;
        
        if (!gate.type.isSingleInput()) {
            renderer.fillRect(x + 2.5, inputY1-2.5, 5, 5, GameRenderer.RETRO_GREEN);
            renderer.fillRect(x + 2.5, inputY2-2.5, 5, 5, GameRenderer.RETRO_GREEN);
            renderer.drawText("A", x + 15, inputY1 + 4, smallFont, 
                            GameRenderer.RETRO_GREEN_DARK);
            renderer.drawText("B", x + 15, inputY2 + 4, smallFont, 
                            GameRenderer.RETRO_GREEN_DARK);
        } else {
            renderer.fillRect(x + 2.5, centerY + CELL_SIZE / 2 - 2.5, 5, 5, GameRenderer.RETRO_GREEN);
            renderer.drawText("A", x + 15, centerY + CELL_SIZE / 2 + 4, 
                            smallFont, GameRenderer.RETRO_GREEN_DARK);
        }
        
        // Output
        renderer.fillRect(x + gateSize - 7.5, centerY + CELL_SIZE / 2 - 2.5, 5, 5, GameRenderer.RETRO_GREEN);
        renderer.drawText("Y", x + gateSize - 20, centerY + CELL_SIZE / 2 + 4, 
                        smallFont, GameRenderer.RETRO_GREEN_DARK);
    }
    
    private void drawPalette(GameRenderer renderer) {
        double startX = 80;
        double startY = 620;
        
        renderer.drawText("Gates:", startX, startY, labelFont, GameRenderer.RETRO_GREEN);
        
        int i = 0;
        for (GateType gate : availableGates) {
            boolean selected = (selectedGateType == gate);
            Color gateColor = selected ? Color.YELLOW : GameRenderer.RETRO_GREEN_DARK;
            
            double x = startX + i * 80;
            renderer.fillRect(x, startY + 10, 70, 40, Color.rgb(0, 20, 0));
            renderer.drawRect(x, startY + 10, 70, 40, gateColor);
            renderer.drawCenteredText(gate.toString(), x + 35, startY + 35, smallFont, gateColor);
            
            i++;
        }
    }
    
    private void drawTruthTable(GameRenderer renderer) {
        double startX = 1000;
        double startY = 190;
        
        renderer.drawText("Truth Table:", startX, startY, labelFont, GameRenderer.RETRO_GREEN);
        
        StringBuilder header = new StringBuilder();
        for (int i = 0; i < numInputs; i++) {
            header.append((char)('A' + i)).append(" ");
        }
        header.append("| Y");
        renderer.drawText(header.toString(), startX, startY + 20, smallFont, 
                         GameRenderer.RETRO_GREEN_DARK);
        
        renderer.drawText("-------", startX, startY + 35, smallFont,
                         GameRenderer.RETRO_GREEN_DARKER);
        
        int maxRows = Math.min(12, truthTable.size());
        for (int r = 0; r < maxRows; r++) {
            boolean[] row = truthTable.get(r);
            StringBuilder rowText = new StringBuilder();
            for (int i = 0; i < row.length - 1; i++) {
                rowText.append(row[i] ? "1" : "0").append(" ");
            }
            rowText.append("| ").append(row[row.length - 1] ? "1" : "0");
            
            renderer.drawText(rowText.toString(), startX, startY + 50 + r * 16, 
                            smallFont, GameRenderer.RETRO_GREEN_DARK);
        }
    }
    
    private void renderResult(GameRenderer renderer) {
        double y = GameApplication.LOGICAL_HEIGHT - 150;
        
        if (correct) {
            renderer.fillRect(0, y - 20, GameApplication.LOGICAL_WIDTH, 120, 
                            Color.rgb(0, 100, 0, 0.8));
            renderer.drawCenteredText(
                "✓ CORRECT! Module repaired!",
                GameApplication.LOGICAL_WIDTH / 2.0,
                y + 30,
                titleFont,
                GameRenderer.RETRO_GREEN
            );
            renderer.drawCenteredText(
                "Press ENTER to continue",
                GameApplication.LOGICAL_WIDTH / 2.0,
                y + 70,
                labelFont,
                GameRenderer.RETRO_GREEN
            );
        } else {
            renderer.fillRect(0, y - 20, GameApplication.LOGICAL_WIDTH, 120, 
                            Color.rgb(100, 0, 0, 0.8));
            renderer.drawCenteredText(
                "✗ INCORRECT!",
                GameApplication.LOGICAL_WIDTH / 2.0,
                y + 30,
                titleFont,
                Color.RED
            );
            renderer.drawCenteredText(
                "Press ENTER to try again",
                GameApplication.LOGICAL_WIDTH / 2.0,
                y + 70,
                labelFont,
                Color.RED
            );
        }
    }
    
    @Override
    protected boolean checkAnswer() {
        System.out.println("\n=== TESTING CIRCUIT ===");
        System.out.println("Gates placed: " + placedGates.size());
        
        for (int row = 0; row < truthTable.size(); row++) {
            boolean[] testRow = truthTable.get(row);
            boolean[] inputs = Arrays.copyOf(testRow, testRow.length - 1);
            boolean expected = testRow[testRow.length - 1];
            
            boolean actual = simulateCircuit(inputs);
            
            System.out.println("Test " + row + ": inputs=" + Arrays.toString(inputs) + 
                             " expected=" + expected + " actual=" + actual +
                             (actual == expected ? " ✓" : " ✗"));
            
            if (actual != expected) {
                return false;
            }
        }
        
        System.out.println("All tests PASSED! ✓\n");
        return true;
    }
    
    /**
     * IMPROVED CIRCUIT SIMULATION
     */
    private boolean simulateCircuit(boolean[] inputs) {
        System.out.println("  Simulating with inputs: " + Arrays.toString(inputs));
        
        // Reset
        for (Gate gate : placedGates) {
            gate.inputA = null;
            gate.inputB = null;
            gate.output = null;
        }
        
        Map<String, Boolean> signals = new HashMap<>();
        
        // Set input signals
        for (int i = 0; i < numInputs; i++) {
            int y = 2 + i * 3;
            if (y < GRID_ROWS) {
                String key = key(1, y);
                signals.put(key, inputs[i]);
                System.out.println("    Input " + (char)('A' + i) + " at (" + 1 + "," + y + ") = " + inputs[i]);
            }
        }
        
        // Propagate signals (multiple iterations)
        int maxIter = 100;
        for (int iter = 0; iter < maxIter; iter++) {
            boolean changed = false;
            
            // Propagate through wires
            for (int y = 0; y < GRID_ROWS; y++) {
                for (int x = 0; x < GRID_COLS; x++) {
                    GridCell cell = grid[y][x];
                    String pos = key(x, y);
                    
                    if (signals.containsKey(pos)) {
                        boolean signal = signals.get(pos);
                        
                        // Spread to adjacent cells
                        int[][] dirs = {{-1,0}, {1,0}, {0,-1}, {0,1}};
                        for (int[] d : dirs) {
                            int nx = x + d[0];
                            int ny = y + d[1];
                            if (nx < 0 || nx >= GRID_COLS || ny < 0 || ny >= GRID_ROWS) continue;
                            
                            GridCell neighbor = grid[ny][nx];
                            String nkey = key(nx, ny);
                            
                            // Propagate to wires and outputs
                            if ((neighbor.type == ComponentType.WIRE || neighbor.type == ComponentType.OUTPUT) 
                                && !signals.containsKey(nkey)) {
                                signals.put(nkey, signal);
                                changed = true;
                            }
                            
                            // Propagate to gate inputs
                            if (neighbor.type == ComponentType.GATE && neighbor.gate != null) {
                                Gate gate = neighbor.gate;
                                int relX = nx - gate.centerX;
                                int relY = ny - gate.centerY;
                                
                                // Left edge = inputs
                                if (relX == -1) {
                                    if (gate.type.isSingleInput()) {
                                        if (relY == 0 && gate.inputA == null) {
                                            gate.inputA = signal;
                                            changed = true;
                                        }
                                    } else {
                                        if (relY == 1 && gate.inputA == null) {
                                            gate.inputA = signal;
                                            changed = true;
                                        } else if (relY == -1 && gate.inputB == null) {
                                            gate.inputB = signal;
                                            changed = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Evaluate gates
            for (Gate gate : placedGates) {
                if (gate.output != null) continue;
                
                boolean ready = false;
                if (gate.type.isSingleInput()) {
                    ready = (gate.inputA != null);
                } else {
                    ready = (gate.inputA != null && gate.inputB != null);
                }
                
                if (ready) {
                    if (gate.type.isSingleInput()) {
                        gate.output = gate.type.evaluate(gate.inputA, false);
                    } else {
                        gate.output = gate.type.evaluate(gate.inputA, gate.inputB);
                    }
                    
                    // Output to right side
                    int outX = gate.centerX + 1;
                    int outY = gate.centerY;
                    signals.put(key(outX, outY), gate.output);
                    changed = true;
                    
                    System.out.println("    Gate " + gate.type + " at (" + gate.centerX + "," + gate.centerY + 
                                     ") evaluated: A=" + gate.inputA + " B=" + gate.inputB + " → " + gate.output);
                }
            }
            
            if (!changed) {
                System.out.println("    Converged after " + (iter + 1) + " iterations");
                break;
            }
        }
        
        // Read output
        int outY = GRID_ROWS / 2;
        int outX = GRID_COLS - 2;
        String outKey = key(outX, outY);
        
        Boolean result = signals.get(outKey);
        System.out.println("    Output at (" + outX + "," + outY + ") = " + result);
        
        return result != null && result;
    }
    
    private String key(int x, int y) {
        return x + "," + y;
    }
    
    @Override
    public void reset() {
        super.reset();
        placedGates.clear();
        
        for (int y = 0; y < GRID_ROWS; y++) {
            for (int x = 0; x < GRID_COLS; x++) {
                GridCell cell = grid[y][x];
                if (cell.type != ComponentType.INPUT && cell.type != ComponentType.OUTPUT) {
                    cell.type = ComponentType.EMPTY;
                    cell.gate = null;
                }
            }
        }
        drawingWire = false;
    }
    
    // Inner classes
    private static class GridCell {
        ComponentType type = ComponentType.EMPTY;
        Gate gate = null;
        int inputIndex = 0;
    }
    
    private static class Gate {
        GateType type;
        int centerX, centerY;
        Boolean inputA = null;
        Boolean inputB = null;
        Boolean output = null;
        
        Gate(GateType type, int centerX, int centerY) {
            this.type = type;
            this.centerX = centerX;
            this.centerY = centerY;
        }
    }
    
    private enum ComponentType {
        EMPTY, INPUT, OUTPUT, GATE, WIRE
    }

    /**
 * Logic gate types
 */
    public enum GateType {
        NOT {
            public boolean evaluate(boolean a, boolean b) { return !a; }
            public boolean isSingleInput() { return true; }
        },
        NAND {
            public boolean evaluate(boolean a, boolean b) { return !(a && b); }
            public boolean isSingleInput() { return false; }
        },
        XOR {
            public boolean evaluate(boolean a, boolean b) { return a != b; }
            public boolean isSingleInput() { return false; }
        };  
        public abstract boolean evaluate(boolean a, boolean b);
        public abstract boolean isSingleInput();
    }
}