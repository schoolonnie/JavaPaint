package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

public class MainWindow extends JFrame {
	private PaintMode activeMode = PaintMode.BRUSH;
	private Color currentColor = Color.black;
	private int currentStrokeSize = 5;
	
	private Canvas canvas;
	private Toolbar toolbar;

    public MainWindow() {
        // Configure the window itself
        super("Javaint");
        setTitle("Javaint");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(800, 600));
        
        // Add content
        canvas = new Canvas(this);
        toolbar = new Toolbar(this);
        
        this.pack();
        this.setLocationRelativeTo(null);
        this.getContentPane().add(toolbar, BorderLayout.NORTH); 
        this.getContentPane().add(canvas, BorderLayout.CENTER);
    }
    // Method for clearing the canvas
    public void clearCanvas() {
        // Remove the old canvas instance from the content pane
        this.getContentPane().remove(this.canvas);
        
        // Create new canvas instance
        this.canvas = new Canvas(this);
        
        // Add the new canvas to the exact same layout position
        this.getContentPane().add(this.canvas, BorderLayout.CENTER);
        
        // Force Swing to re-layout the window and redraw the new component
        this.getContentPane().revalidate();
        this.getContentPane().repaint();
    }
    
    // Getters and setters for color and stroke size
    public void setCurrentColor(Color color) {
    	this.currentColor = color;
    }
    public void setCurrentStroke(int size) {
    	this.currentStrokeSize = size;
    }
    public Color getCurrentColor() {
    	return this.currentColor;
    }
    public int getCurrentStroke() {
    	return this.currentStrokeSize;
    }
    // Method allowing for Toolbar to update the mode
    public void setPaintMode(PaintMode mode) {
    	this.activeMode = mode;
    }
    // Method allowing for Canvas to see mode
    public PaintMode getPaintMode() {
    	return this.activeMode;
    }
    // Method for saving
    public void saveCanvas() {
    	SaveManager.saveImageAtPath(this.canvas, this.canvas.getCurrentImage());
    }
    // Method for loading
    public void loadCanvas() {
    	BufferedImage newImage = SaveManager.loadImageFromPath(this.canvas, this.canvas.getCurrentImage());
        canvas.setCurrentImage(newImage);
    }
}
