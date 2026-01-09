package com.interplanetarycrash.tasks;

import com.interplanetarycrash.GameApplication;
import com.interplanetarycrash.assets.AssetManager;
import com.interplanetarycrash.input.InputHandler;
import com.interplanetarycrash.rendering.GameRenderer;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Multiple choice question task (A, B, C, D)
 * 
 * File format:
 * ABCD
 * Question text?
 * Answer A text
 * Answer B text
 * Answer C text
 * Answer D text
 * B  (correct answer: A, B, C, or D)
 * 1  (difficulty: 1-5)
 */
public class MultipleChoiceTask extends Task {
    
    private String[] answers; // 4 answers
    private int correctAnswerIndex; // 0=A, 1=B, 2=C, 3=D
    private int selectedAnswerIndex; // Currently selected
    
    private Font questionFont;
    private Font answerFont;
    private Font letterFont;
    
    public MultipleChoiceTask(String question, String[] answers, char correctAnswer, int difficulty) {
        super(TaskType.MULTIPLE_CHOICE, question, difficulty);
        
        if (answers.length != 4) {
            throw new IllegalArgumentException("MultipleChoice must have exactly 4 answers");
        }
        
        this.answers = answers;
        this.correctAnswerIndex = correctAnswer - 'A'; // 'A'=0, 'B'=1, etc.
        this.selectedAnswerIndex = 0;
        
        if (correctAnswerIndex < 0 || correctAnswerIndex > 3) {
            throw new IllegalArgumentException("Correct answer must be A, B, C, or D");
        }

        AssetManager asset = AssetManager.getInstance();
        this.questionFont = asset.getFont("retro");
        this.answerFont = asset.getFont("retro");
        this.letterFont = asset.getFont("retro_large");
    }
    
    @Override
    public void update(double deltaTime, InputHandler input) {
        if (completed) return;
        
        // Navigation with W/S or Arrow keys
        if (input.isNavigatingDown()) {
            selectedAnswerIndex = (selectedAnswerIndex + 1) % 4;
        }
        
        if (input.isNavigatingUp()) {
            selectedAnswerIndex--;
            if (selectedAnswerIndex < 0) {
                selectedAnswerIndex = 3;
            }
        }
        
        // Quick selection with A/B/C/D keys
        if (input.isKeyJustPressed(KeyCode.A)) selectedAnswerIndex = 0;
        if (input.isKeyJustPressed(KeyCode.B)) selectedAnswerIndex = 1;
        if (input.isKeyJustPressed(KeyCode.C)) selectedAnswerIndex = 2;
        if (input.isKeyJustPressed(KeyCode.D)) selectedAnswerIndex = 3;
        
        // Submit with ENTER
        if (input.isConfirming()) {
            submitAnswer();
        }
    }
    
    @Override
    public void render(GameRenderer renderer) {
        
        renderer.fillRect(0, 0, GameApplication.LOGICAL_WIDTH,
                         GameApplication.LOGICAL_HEIGHT,
                         Color.rgb(0, 20, 0));
        
        renderer.drawCenteredText(
            "REPAIR MODULE - ANSWER QUESTION",
            GameApplication.LOGICAL_WIDTH / 2.0,
            100,
            letterFont,
            GameRenderer.RETRO_GREEN
        );
        
        double questionY = 200;
        renderWrappedText(renderer, question, 200, questionY, 
                         GameApplication.LOGICAL_WIDTH - 400, questionFont);
        
        double answerStartY = 350;
        double answerSpacing = 80;
        char[] letters = {'A', 'B', 'C', 'D'};
        
        for (int i = 0; i < 4; i++) {
            double y = answerStartY + i * answerSpacing;
            boolean selected = (i == selectedAnswerIndex);
            
            renderAnswer(renderer, letters[i], answers[i], y, selected);
        }
        
        if (completed) {
            renderResult(renderer);
        } else {
            renderer.drawCenteredText(
                "↑↓ or W/S: Navigate  |  ENTER: Confirm  |  A/B/C/D: Quick select",
                GameApplication.LOGICAL_WIDTH / 2.0,
                GameApplication.LOGICAL_HEIGHT - 50,
                answerFont,
                GameRenderer.RETRO_GREEN_DARKER
            );
        }
    }
    
    /**
     * Render single answer option
     */
    private void renderAnswer(GameRenderer renderer, char letter, String text, 
                             double y, boolean selected) {
        double boxX = 250;
        double boxWidth = GameApplication.LOGICAL_WIDTH - 500;
        double boxHeight = 70;
        
        Color bgColor = selected ? GameRenderer.RETRO_GREEN_DARK : Color.rgb(0, 30, 0);
        Color textColor = selected ? GameRenderer.RETRO_GREEN : GameRenderer.RETRO_GREEN_DARK;
        Color borderColor = selected ? GameRenderer.RETRO_GREEN : GameRenderer.RETRO_GREEN_DARKER;
        
        renderer.fillRect(boxX, y - 10, boxWidth, boxHeight, bgColor);
        
        if (selected) {
            renderer.drawRect(boxX - 5, y - 15, boxWidth + 10, boxHeight + 10, 
                            GameRenderer.RETRO_GREEN);
        }
        renderer.drawRect(boxX, y - 10, boxWidth, boxHeight, borderColor);
        
        renderer.fillRect(boxX + 20, y, 50, 50, borderColor);
        renderer.drawCenteredText(
            String.valueOf(letter),
            boxX + 45,
            y + 35,
            letterFont,
            selected ? Color.BLACK : GameRenderer.RETRO_GREEN
        );
        
        renderer.drawText(
            text,
            boxX + 90,
            y + 35,
            answerFont,
            textColor
        );
        
        if (selected) {
            renderer.drawText(
                "→",
                boxX - 40,
                y + 35,
                letterFont,
                GameRenderer.RETRO_GREEN
            );
        }
    }
    
    /**
     * Render result (correct/incorrect)
     */
    private void renderResult(GameRenderer renderer) {
        double y = GameApplication.LOGICAL_HEIGHT - 150;
        
        if (correct) {
            renderer.fillRect(0, y - 20, GameApplication.LOGICAL_WIDTH, 120, 
                            Color.rgb(0, 100, 0, 0.8));
            renderer.drawCenteredText(
                "✓ CORRECT! Module repaired!",
                GameApplication.LOGICAL_WIDTH / 2.0,
                y + 30,
                letterFont,
                GameRenderer.RETRO_GREEN
            );
            renderer.drawCenteredText(
                "Press ENTER to continue",
                GameApplication.LOGICAL_WIDTH / 2.0,
                y + 70,
                answerFont,
                GameRenderer.RETRO_GREEN
            );
        } else {
            renderer.fillRect(0, y - 20, GameApplication.LOGICAL_WIDTH, 120, 
                            Color.rgb(100, 0, 0, 0.8));
            renderer.drawCenteredText(
                "✗ INCORRECT!",
                GameApplication.LOGICAL_WIDTH / 2.0,
                y + 30,
                letterFont,
                Color.RED
            );
            renderer.drawCenteredText(
                "Press ENTER to try again",
                GameApplication.LOGICAL_WIDTH / 2.0,
                y + 70,
                answerFont,
                Color.RED
            );
        }
    }
    
    /**
     * Render text with word wrapping
     */
    private void renderWrappedText(GameRenderer renderer, String text, 
                                   double x, double y, double maxWidth, Font font) {
       
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        double currentY = y;
        double lineHeight = 35;
        
        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            
            if (testLine.length() * 14 > maxWidth && currentLine.length() > 0) {
                renderer.drawCenteredText(
                    currentLine.toString(),
                    GameApplication.LOGICAL_WIDTH / 2.0,
                    currentY,
                    font,
                    GameRenderer.RETRO_GREEN
                );
                currentLine = new StringBuilder(word);
                currentY += lineHeight;
            } else {
                currentLine = new StringBuilder(testLine);
            }
        }
        
        if (currentLine.length() > 0) {
            renderer.drawCenteredText(
                currentLine.toString(),
                GameApplication.LOGICAL_WIDTH / 2.0,
                currentY,
                font,
                GameRenderer.RETRO_GREEN
            );
        }
    }
    
    @Override
    protected boolean checkAnswer() {
        return selectedAnswerIndex == correctAnswerIndex;
    }
    
    @Override
    public void reset() {
        super.reset();
        selectedAnswerIndex = 0;
    }
}