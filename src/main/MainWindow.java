package main;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

public class MainWindow extends JFrame {
	private PaintMode activeMode = PaintMode.BRUSH;
	
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
    // Method allowing for Toolbar to update the mode
    public void setPaintMode(PaintMode mode) {
    	this.activeMode = mode;
    }
    // Method allowing for Canvas to see mode
    public PaintMode getPaintMode() {
    	return this.activeMode;
    }
}
