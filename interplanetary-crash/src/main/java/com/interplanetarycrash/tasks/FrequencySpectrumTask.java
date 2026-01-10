package com.interplanetarycrash.tasks;

import com.interplanetarycrash.GameApplication;
import com.interplanetarycrash.assets.AssetManager;
import com.interplanetarycrash.input.InputHandler;
import com.interplanetarycrash.rendering.GameRenderer;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Frequency Spectrum Task - Match periodic function parameters
 * Player adjusts amplitude, frequency, phase AND WAVEFORM TYPE
 * 
 * NOW WITH MULTIPLE WAVEFORMS AND REAL FFT!
 */
public class FrequencySpectrumTask extends Task {
    
    // Target values
    private double targetAmplitude;
    private double targetFrequency;
    private double targetPhase;
    private WaveformType targetWaveform;
    
    // Current values (what player sets)
    private double currentAmplitude;
    private double currentFrequency;
    private double currentPhase;
    private WaveformType currentWaveform;
    
    // Ranges
    private double amplitudeMin, amplitudeMax;
    private double frequencyMin, frequencyMax;
    private double phaseMin, phaseMax;
    
    // Tolerance for correct answer
    private double tolerance;
    
    // UI state
    private int selectedParameter; // 0=waveform, 1=amplitude, 2=frequency, 3=phase
    private static final int PARAM_WAVEFORM = 0;
    private static final int PARAM_FREQUENCY = 1;
    private static final int PARAM_PHASE = 2;
    private static final int PARAM_AMPLITUDE = 3;
    
    // Visualization 
    private static final int WAVEFORM_WIDTH = 550;
    private static final int WAVEFORM_HEIGHT = 180;
    private static final int SPECTRUM_WIDTH = 400;
    private static final int SPECTRUM_HEIGHT = 180;
    private static final int WAVEFORM_SAMPLES = 256; // Power of 2 for FFT
    
    // FFT parameters
    private static final double SAMPLE_RATE = 1000.0; // Hz
    
    private Font normalFont;
    private Font smallFont;
    private Font bigFont;
    
    /**
     * Waveform types with different spectral characteristics
     */
    public enum WaveformType {
        SINE("Sine"),
        SQUARE("Square"),
        SAWTOOTH("Sawtooth"),
        TRIANGLE("Triangle"),
        SINC("Sinc"),
        PULSE("Pulse");
        
        private final String displayName;
        
        WaveformType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    
    //constructor
    public FrequencySpectrumTask(String instruction,
                                double targetAmplitude, double targetFrequency, double targetPhase,
                                WaveformType targetWaveform,
                                double amplitudeMin, double amplitudeMax,
                                double frequencyMin, double frequencyMax,
                                double phaseMin, double phaseMax,
                                double tolerance, int difficulty) {
        super(TaskType.FREQUENCY_SPECTRUM, instruction, difficulty);
        
        this.targetAmplitude = targetAmplitude;
        this.targetFrequency = targetFrequency;
        this.targetPhase = targetPhase;
        this.targetWaveform = targetWaveform;
        
        this.amplitudeMin = amplitudeMin;
        this.amplitudeMax = amplitudeMax;
        this.frequencyMin = frequencyMin;
        this.frequencyMax = frequencyMax;
        this.phaseMin = phaseMin;
        this.phaseMax = phaseMax;
        
        this.tolerance = tolerance;
        
        // Start at middle of ranges
        this.currentAmplitude = (amplitudeMin + amplitudeMax) / 2.0;
        this.currentFrequency = (frequencyMin + frequencyMax) / 2.0;
        this.currentPhase = (phaseMin + phaseMax) / 2.0;
        this.currentWaveform = WaveformType.SINE;
        
        this.selectedParameter = PARAM_WAVEFORM;

        AssetManager asset = AssetManager.getInstance();
        this.normalFont = asset.getFont("retro");
        this.smallFont = asset.getFont("retro_small");
        this.bigFont = asset.getFont("retro_large");
    }
    
    @Override
    public void update(double deltaTime, InputHandler input) {
        if (completed) {
            return;
        }
        
        // Navigate between parameters (W/S)
        if (input.isNavigatingDown()) {
            if (difficulty < 3) {
                selectedParameter = (selectedParameter + 1) % 4;
            }
            else {
                selectedParameter = (selectedParameter + 1) % 3;
            }
        }

        if (input.isNavigatingUp()) {
            selectedParameter--;
            if (selectedParameter < 0 && difficulty < 3) {
                selectedParameter = 3;
            } else if (selectedParameter < 0) {
                selectedParameter = 2;
            }
        }
        
        // Adjust selected parameter
        if (selectedParameter == PARAM_WAVEFORM) {
            // Cycle waveforms with A/D or Q/E
            if (input.isSelectingLeft()) {
                cycleWaveform(-1);
            }
            if (input.isSelectingRight()) {
                cycleWaveform(1);
            }
        } else {
            // Adjust numeric parameters (A/D)
            double adjustSpeed = 0.02;
            
            if (input.isMovingLeft() || input.isSelectingLeft()) {
                adjustParameter(-adjustSpeed);
            }
            
            if (input.isMovingRight() || input.isSelectingRight()) {
                adjustParameter(adjustSpeed);
            }
        }
        
        // Submit answer (ENTER)
        if (input.isConfirming()) {
            submitAnswer();
        }
    }
    
    private void cycleWaveform(int direction) {
        WaveformType[] types = WaveformType.values();
        int currentIndex = currentWaveform.ordinal();
        currentIndex = (currentIndex + direction + types.length) % types.length;
        currentWaveform = types[currentIndex];
    }
    
    private void adjustParameter(double delta) {
        switch (selectedParameter) {
            case PARAM_AMPLITUDE:
                currentAmplitude += delta * (amplitudeMax - amplitudeMin) / 10.0;
                currentAmplitude = clamp(currentAmplitude, amplitudeMin, amplitudeMax);
                break;
                
            case PARAM_FREQUENCY:
                currentFrequency += delta * (frequencyMax - frequencyMin) / 10.0;
                currentFrequency = clamp(currentFrequency, frequencyMin, frequencyMax);
                break;
                
            case PARAM_PHASE:
                currentPhase += delta * (phaseMax - phaseMin) / 10.0;
                currentPhase = clamp(currentPhase, phaseMin, phaseMax);
                break;
        }
    }
    
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    @Override
    public void render(GameRenderer renderer) {
        
        // Background
        renderer.fillRect(0, 0, GameApplication.LOGICAL_WIDTH,
                         GameApplication.LOGICAL_HEIGHT,
                         Color.rgb(0, 20, 0));
        
        // Title
        renderer.drawCenteredText(
            "REPAIR MODULE - MATCH FREQUENCY SPECTRUM",
            GameApplication.LOGICAL_WIDTH / 2.0,
            80,
            normalFont,
            GameRenderer.RETRO_GREEN
        );
        
        // Instruction
        renderer.drawCenteredText(
            question,
            GameApplication.LOGICAL_WIDTH / 2.0,
            110,
            normalFont,
            GameRenderer.RETRO_GREEN_DARK
        );
        
        // Layout: Waveform (left) | FFT Spectrum (right)

        if (difficulty < 3) drawWaveformComparison(renderer);
        drawFFTSpectrum(renderer);
        
        // Parameter controls below
        drawParameterControls(renderer);
        
        // Show result if completed
        if (completed) {
            renderResult(renderer);
        } else {
            renderer.drawCenteredText(
                "↑↓: Select  |  ←→/Q/E: Adjust  |  ENTER: Submit | ESC: Exit",
                GameApplication.LOGICAL_WIDTH / 2.0,
                GameApplication.LOGICAL_HEIGHT - 30,
                smallFont,
                GameRenderer.RETRO_GREEN_DARKER
            );
        }
    }
    
    /**
     * Draw waveform comparison (left side)
     */
    private void drawWaveformComparison(GameRenderer renderer) {
        double startX = 530;
        double startY = 170;
        double width = WAVEFORM_WIDTH;
        double height = WAVEFORM_HEIGHT;
        
        // Background
        renderer.fillRect(startX, startY, width, height, Color.rgb(0, 20, 0));
        renderer.drawRect(startX, startY, width, height, GameRenderer.RETRO_GREEN);
        
        // Title
        renderer.drawText("WAVEFORM (Time Domain)", startX + 10, startY - 8, smallFont, 
                         GameRenderer.RETRO_GREEN);
        
        // Center line
        double centerY = startY + height / 2;
        renderer.drawLine(startX, centerY, startX + width, centerY, 
                         GameRenderer.RETRO_GREEN_DARKER, 1);
        
        // Draw target waveform (semi-transparent)
        drawWaveform(renderer, startX, startY, width, height, 
                    targetAmplitude, targetFrequency, targetPhase, targetWaveform,
                    Color.rgb(0, 255, 0, 0.3), 2);
        
        // Draw current waveform
        drawWaveform(renderer, startX, startY, width, height,
                    currentAmplitude, currentFrequency, currentPhase, currentWaveform,
                    GameRenderer.RETRO_GREEN, 2);
        
        // Legend with waveform types
        renderer.drawText("Target: " + targetWaveform.getDisplayName(), 
                         startX + 10, startY + 15, smallFont, 
                         Color.rgb(0, 255, 0, 0.5));
        renderer.drawText("Current: " + currentWaveform.getDisplayName(), 
                         startX + 10, startY + 30, smallFont, 
                         GameRenderer.RETRO_GREEN);
    }
    
    /**
     * Draw a waveform based on type
     */
    private void drawWaveform(GameRenderer renderer, double startX, double startY, 
                             double width, double height,
                             double amplitude, double frequency, double phase,
                             WaveformType waveform,
                             Color color, double lineWidth) {
        double prevX = startX;
        double prevY = startY + height / 2;
        
        for (int i = 0; i <= WAVEFORM_SAMPLES; i++) {
            double t = (double)i / SAMPLE_RATE;
            double x = startX + (i / (double)WAVEFORM_SAMPLES) * width;
            
            // Generate waveform sample
            double sample = generateWaveformSample(t, amplitude, frequency, phase, waveform);
            
            // Scale to display
            double maxAmplitude = Math.max(amplitudeMax, 10.0);
            double y = startY + height / 2 - (sample / maxAmplitude) * (height / 2 - 10);
            
            if (i > 0) {
                renderer.drawLine(prevX, prevY, x, y, color, lineWidth);
            }
            
            prevX = x;
            prevY = y;
        }
    }
    
    /**
     * Generate a single waveform sample based on type
     */
    private double generateWaveformSample(double t, double amplitude, double frequency, 
                                         double phase, WaveformType waveform) {
        double omega = 2 * Math.PI * frequency;
        double arg = omega * t + phase;
        
        switch (waveform) {
            case SINE:
                return amplitude * Math.sin(arg);
                
            case SQUARE:
                return amplitude * Math.signum(Math.sin(arg));
                
            case SAWTOOTH:
                // Sawtooth: ramp from -1 to 1
                double sawPhase = (arg / (2 * Math.PI)) % 1.0;
                return amplitude * (2.0 * sawPhase - 1.0);
                
            case TRIANGLE:
                // Triangle: |sawtooth|
                double triPhase = (arg / (2 * Math.PI)) % 1.0;
                return amplitude * (1.0 - 4.0 * Math.abs(triPhase - 0.5));
                
            case SINC:
                // Sinc function: sin(πx)/(πx)
                // Modulated by carrier frequency
                double x = frequency * t * 4; // Scale factor for visibility
                if (Math.abs(x) < 0.001) {
                    return amplitude;
                }
                return amplitude * Math.sin(Math.PI * x) / (Math.PI * x) * Math.cos(arg);
                
            case PULSE:
                // Narrow pulse train
                double pulsePhase = (arg / (2 * Math.PI)) % 1.0;
                return (pulsePhase < 0.1) ? amplitude : 0;
                
            default:
                return 0;
        }
    }
    
    /**
     * Draw FFT spectrum (right side) - REAL FFT!
     */
    private void drawFFTSpectrum(GameRenderer renderer) {
        double startX = 80;
        double startY = 170;
        double width = SPECTRUM_WIDTH;
        double height = SPECTRUM_HEIGHT;
        
        // Background
        renderer.fillRect(startX, startY, width, height, Color.rgb(0, 20, 0));
        renderer.drawRect(startX, startY, width, height, GameRenderer.RETRO_GREEN);
        
        // Title
        renderer.drawText("FFT SPECTRUM (Frequency Domain)", startX + 10, startY - 8, smallFont, 
                         GameRenderer.RETRO_GREEN);
        
        // Compute FFT for both signals
        double[] targetFFT = computeFFT(targetAmplitude, targetFrequency, targetPhase, targetWaveform);
        double[] currentFFT = computeFFT(currentAmplitude, currentFrequency, currentPhase, currentWaveform);
        
        // Draw frequency axis labels
        double freqStep = SAMPLE_RATE / 2.0 / 5.0;
        for (int i = 0; i <= 5; i++) {
            double freq = i * freqStep;
            double x = startX + 40 + (width - 60) * (i / 5.0);
            renderer.drawText(String.format("%.0f", freq), x - 10, startY + height + 15, 
                            smallFont, GameRenderer.RETRO_GREEN_DARKER);
        }
        renderer.drawText("Hz", startX + width - 30, startY + height + 15,
                         smallFont, GameRenderer.RETRO_GREEN_DARKER);
        
        // Draw FFT bars
        drawFFTBars(renderer, startX + 40, startY + 20, width - 60, height - 50, 
                   targetFFT, Color.rgb(0, 255, 0, 0.5));
        drawFFTBars(renderer, startX + 40, startY + 20, width - 60, height - 50, 
                   currentFFT, GameRenderer.RETRO_GREEN);
        
        // Legend
        renderer.drawText("Target", startX + 10, startY + 15, smallFont, 
                         Color.rgb(0, 255, 0, 0.5));
        renderer.drawText("Current", startX + 10, startY + 30, smallFont, 
                         GameRenderer.RETRO_GREEN);
        
        // Show dominant frequency and harmonic info
        double targetDominant = findDominantFrequency(targetFFT);
        double currentDominant = findDominantFrequency(currentFFT);
        int targetHarmonics = countSignificantHarmonics(targetFFT);
        int currentHarmonics = countSignificantHarmonics(currentFFT);
        
        double textY = startY + height + 35;
        renderer.drawText(String.format("Target Dominant Frequency: %.1f Hz (%d harmonics)", targetDominant, targetHarmonics),
                         startX + 5, textY, smallFont, GameRenderer.RETRO_GREEN_DARK);
        renderer.drawText(String.format("Current Dominant Frequency: %.1f Hz (%d harmonics)", currentDominant, currentHarmonics),
                         startX + 5, textY + 15, smallFont, GameRenderer.RETRO_GREEN);
        
    }
    
    /**
     * Compute FFT magnitude spectrum for any waveform
     * 
     */
    private double[] computeFFT(double amplitude, double frequency, double phase, WaveformType waveform) {
        int n = WAVEFORM_SAMPLES;
        double[] signal = new double[n];
        
        // Generate signal
        for (int i = 0; i < n; i++) {
            double t = i / SAMPLE_RATE;
            signal[i] = generateWaveformSample(t, amplitude, frequency, phase, waveform);
        }
        
        // Perform FFT
        Complex[] fft = fft(signal);
        
        // Compute magnitude spectrum (only positive frequencies)
        int numBins = n / 2;
        double[] magnitudes = new double[numBins];
        
        for (int i = 0; i < numBins; i++) {
            magnitudes[i] = fft[i].abs() * 2.0 / n; // Normalize
        }
        
        return magnitudes;
    }
    
    /**
     * Count significant harmonics (above threshold)
     */
    private int countSignificantHarmonics(double[] magnitudes) {
        double maxMagnitude = 0;
        for (double mag : magnitudes) {
            if (mag > maxMagnitude) maxMagnitude = mag;
        }
        
        double threshold = maxMagnitude * 0.05; // 5% of max
        int count = 0;
        
        for (double mag : magnitudes) {
            if (mag > threshold) count++;
        }
        
        return count;
    }
    
    /**
     * Fast Fourier Transform (Cooley-Tukey algorithm)
     */
    private Complex[] fft(double[] signal) {
        int n = signal.length;
        
        if (n == 1) {
            return new Complex[] { new Complex(signal[0], 0) };
        }
        
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n must be power of 2");
        }
        
        double[] even = new double[n / 2];
        double[] odd = new double[n / 2];
        for (int i = 0; i < n / 2; i++) {
            even[i] = signal[2 * i];
            odd[i] = signal[2 * i + 1];
        }
        
        Complex[] evenFFT = fft(even);
        Complex[] oddFFT = fft(odd);
        
        Complex[] result = new Complex[n];
        for (int k = 0; k < n / 2; k++) {
            double angle = -2 * Math.PI * k / n;
            Complex wk = new Complex(Math.cos(angle), Math.sin(angle));
            Complex term = wk.multiply(oddFFT[k]);
            
            result[k] = evenFFT[k].add(term);
            result[k + n / 2] = evenFFT[k].subtract(term);
        }
        
        return result;
    }
    
    /**
     * Draw FFT bars as continuous spectrum
     */
    private void drawFFTBars(GameRenderer renderer, double startX, double startY,
                            double width, double height, double[] magnitudes, Color color) {
        double maxMagnitude = 0;
        for (double mag : magnitudes) {
            if (mag > maxMagnitude) maxMagnitude = mag;
        }
        
        if (maxMagnitude == 0) maxMagnitude = 1.0;
        
        double barWidth = width / magnitudes.length;
        
        for (int i = 0; i < magnitudes.length; i++) {
            double barHeight = (magnitudes[i] / maxMagnitude) * height;
            double x = startX + i * barWidth;
            double y = startY + height - barHeight;
            
            renderer.fillRect(x, y, Math.max(barWidth - 1, 1), barHeight, color);
        }
    }
    
    /**
     * Find dominant frequency in FFT spectrum
     */
    private double findDominantFrequency(double[] magnitudes) {
        int maxIndex = 0;
        double maxMagnitude = 0;
        
        for (int i = 1; i < magnitudes.length; i++) {
            if (magnitudes[i] > maxMagnitude) {
                maxMagnitude = magnitudes[i];
                maxIndex = i;
            }
        }
        
        double frequencyResolution = SAMPLE_RATE / (2.0 * magnitudes.length);
        return maxIndex * frequencyResolution;
    }
    
    /**
     * Draw parameter adjustment controls
     */
    private void drawParameterControls(GameRenderer renderer) {
        double startY = GameApplication.LOGICAL_HEIGHT / 3.0 * 2 - 50;
        double spacing = 55;
        
        // Waveform selector (special UI)
        drawWaveformSelector(renderer, startY, selectedParameter == PARAM_WAVEFORM);
        
        drawParameterSlider(renderer, "FREQ (Hz)", 
                           currentFrequency, frequencyMin, frequencyMax,
                           startY + spacing, selectedParameter == PARAM_FREQUENCY);
        
        drawParameterSlider(renderer, "PHASE (rad)", 
                           currentPhase, phaseMin, phaseMax,
                           startY + 2 * spacing, selectedParameter == PARAM_PHASE);
        
        if (difficulty < 3){
        drawParameterSlider(renderer, "AMPLITUDE", 
                           currentAmplitude, amplitudeMin, amplitudeMax,
                           startY + 3 * spacing, selectedParameter == PARAM_AMPLITUDE);    
        }               
    }
    
    /**
     * Draw waveform selector (different UI than sliders)
     */
    private void drawWaveformSelector(GameRenderer renderer, double y, boolean selected) {
        double labelX = 100;
        double selectorX = 280;
        double selectorWidth = 500;
        double selectorHeight = 35;
        
        Color labelColor = selected ? GameRenderer.RETRO_GREEN : GameRenderer.RETRO_GREEN_DARK;
        Color borderColor = selected ? GameRenderer.RETRO_GREEN : GameRenderer.RETRO_GREEN_DARKER;
        
        if (selected) {
            renderer.fillRect(labelX - 20, y - 10, selectorWidth + 280, 50, Color.rgb(0, 50, 0, 0.3));
            renderer.drawRect(labelX - 20, y - 10, selectorWidth + 280, 50, GameRenderer.RETRO_GREEN);
            renderer.drawText("→", labelX - 50, y + 20, normalFont,
                            GameRenderer.RETRO_GREEN);
        }
        
        renderer.drawText("WAVEFORM:", labelX, y + 20, smallFont, labelColor);
        
        // Selector box
        renderer.fillRect(selectorX, y, selectorWidth, selectorHeight, Color.rgb(0, 30, 0));
        renderer.drawRect(selectorX, y, selectorWidth, selectorHeight, borderColor);
        
        // Current waveform name (centered)
        renderer.drawCenteredText(currentWaveform.getDisplayName(), 
                                 selectorX + selectorWidth / 2, y + 22,
                                 normalFont, labelColor);
        
        // Navigation arrows
        renderer.drawText("◄", selectorX + 10, y + 22, normalFont, labelColor);
        renderer.drawText("►", selectorX + selectorWidth - 25, y + 22, normalFont, labelColor);
        
        // Indicator text
        renderer.drawText("← Q/E →", selectorX + selectorWidth + 20, y + 20,
                         smallFont, labelColor);
    }
    
    private void drawParameterSlider(GameRenderer renderer, String label, double value, 
                                     double min, double max, double y, boolean selected) {
        double labelX = 100;
        double sliderX = 280;
        double sliderWidth = 500;
        double sliderHeight = 25;
        
        Color labelColor = selected ? GameRenderer.RETRO_GREEN : GameRenderer.RETRO_GREEN_DARK;
        Color sliderColor = selected ? GameRenderer.RETRO_GREEN : GameRenderer.RETRO_GREEN_DARKER;
        
        if (selected) {
            renderer.fillRect(labelX - 20, y - 10, sliderWidth + 280, 45, Color.rgb(0, 50, 0, 0.3));
            renderer.drawRect(labelX - 20, y - 10, sliderWidth + 280, 45, GameRenderer.RETRO_GREEN);
            renderer.drawText("→", labelX - 50, y + 18, normalFont, 
                            GameRenderer.RETRO_GREEN);
        }

        renderer.drawText(label + ":", labelX, y + 17, smallFont, labelColor);
        
        renderer.fillRect(sliderX, y, sliderWidth, sliderHeight, Color.rgb(0, 30, 0));
        renderer.drawRect(sliderX, y, sliderWidth, sliderHeight, sliderColor);
        
        double fillWidth = ((value - min) / (max - min)) * sliderWidth;
        renderer.fillRect(sliderX + 2, y + 2, fillWidth - 4, sliderHeight - 4, sliderColor);
        
        double handleX = sliderX + fillWidth;
        renderer.fillRect(handleX - 3, y - 4, 6, sliderHeight + 8, GameRenderer.RETRO_GREEN);
        renderer.drawRect(handleX - 3, y - 4, 6, sliderHeight + 8, Color.WHITE);
        
        renderer.drawText(String.format("%.2f", value), sliderX + sliderWidth + 20, y + 17,
                         normalFont, labelColor);
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
                bigFont,
                GameRenderer.RETRO_GREEN
            );
            renderer.drawCenteredText(
                "Press ENTER to continue",
                GameApplication.LOGICAL_WIDTH / 2.0,
                y + 70,
                normalFont,
                GameRenderer.RETRO_GREEN
            );
        } else {
            renderer.fillRect(0, y - 20, GameApplication.LOGICAL_WIDTH, 120, 
                            Color.rgb(100, 0, 0, 0.8));
            renderer.drawCenteredText(
                "✗ INCORRECT!",
                GameApplication.LOGICAL_WIDTH / 2.0,
                y + 30,
                bigFont,
                Color.RED
            );
            renderer.drawCenteredText(
                "Press ENTER to try again",
                GameApplication.LOGICAL_WIDTH / 2.0,
                y + 70,
                normalFont,
                Color.RED
            );
        }
    }
    
    @Override
    protected boolean checkAnswer() {
        // Must match waveform type first!
        if (currentWaveform != targetWaveform) {
            System.out.println("Wrong waveform type: " + currentWaveform + " vs " + targetWaveform);
            return false;
        }
        boolean ampCorrect = true;
        if (difficulty < 3) {
            ampCorrect = Math.abs(currentAmplitude - targetAmplitude) <= tolerance;
        }
        boolean freqCorrect = Math.abs(currentFrequency - targetFrequency) <= tolerance;
        boolean phaseCorrect = Math.abs(currentPhase % (2 * Math.PI)  - targetPhase % (2 * Math.PI))  <= tolerance;
        
        return ampCorrect && freqCorrect && phaseCorrect;
    }
    
    @Override
    public void reset() {
        super.reset();
        currentAmplitude = (amplitudeMin + amplitudeMax) / 2.0;
        currentFrequency = (frequencyMin + frequencyMax) / 2.0;
        currentPhase = (phaseMin + phaseMax) / 2.0;
        currentWaveform = WaveformType.SINE;
        selectedParameter = PARAM_WAVEFORM;
    }
    
    /**
     * Complex number class for FFT calculations
     */
    private static class Complex {
        private final double real;
        private final double imag;
        
        public Complex(double real, double imag) {
            this.real = real;
            this.imag = imag;
        }
        
        public Complex add(Complex other) {
            return new Complex(real + other.real, imag + other.imag);
        }
        
        public Complex subtract(Complex other) {
            return new Complex(real - other.real, imag - other.imag);
        }
        
        public Complex multiply(Complex other) {
            double r = real * other.real - imag * other.imag;
            double i = real * other.imag + imag * other.real;
            return new Complex(r, i);
        }
        
        public double abs() {
            return Math.sqrt(real * real + imag * imag);
        }
    }
}