package main;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Toolbar extends JPanel {
	
	private MainWindow window;
    private JButton brushBtn;
    private JButton sprayBtn;
    private JButton rectBtn;

    public Toolbar(MainWindow window) {
    	this.window = window;
    	
    	// Create buttons
    	brushBtn = new JButton("🖌");
    	sprayBtn = new JButton("░");
    	rectBtn = new JButton("■");
    	
	    this.setLayout(new FlowLayout(FlowLayout.CENTER));
	    
	    // Button actions
	    brushBtn.addActionListener(e -> window.setPaintMode(PaintMode.BRUSH));
        sprayBtn.addActionListener(e -> window.setPaintMode(PaintMode.SPRAY));
        rectBtn.addActionListener(e -> window.setPaintMode(PaintMode.RECT));
	    
	    // Assemble and display
	    this.add(brushBtn);
	    this.add(sprayBtn);
	    this.add(rectBtn);
	
	    
    }
}
