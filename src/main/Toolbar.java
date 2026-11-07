package main;

import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Toolbar extends JPanel {
	// I know the variables can be finalized, 
	// but I prefer to keep my organization this way
	private MainWindow window;
	private JButton undoBtn;
	private JButton redoBtn;
	private JButton saveBtn;
	private JButton loadBtn;
    private JButton brushBtn;
    private JButton sprayBtn;
    private JButton rectBtn;
	private JButton eraseBtn;
    private JButton colorBtn;
    private JComboBox<String> sizeBox;
    String[] sizeOptions = {"1","2","3","4","5","6","7","8","9","10"};
    private JButton clearBtn;

    public Toolbar(MainWindow window) {
    	this.window = window;
    	
    	// Create buttons
		undoBtn = new JButton("↻");
		redoBtn = new JButton("↺");
		saveBtn = new JButton("↓");
    	loadBtn = new JButton("↑");
    	brushBtn = new JButton("🖌");
    	sprayBtn = new JButton("░");
    	rectBtn = new JButton("□");
		eraseBtn = new JButton("✖");
    	colorBtn = new JButton("❖");
    	sizeBox = new JComboBox<>(sizeOptions);
    	sizeBox.setSelectedIndex(4);
    	clearBtn = new JButton("🗑");
    	
    	// Set Flow layout
	    this.setLayout(new FlowLayout(FlowLayout.CENTER));
	    
	    // Button actions
		saveBtn.addActionListener(e -> window.saveCanvas());
		loadBtn.addActionListener(e -> window.loadCanvas());
		undoBtn.addActionListener(e -> window.undoLastAction());
		redoBtn.addActionListener(e -> window.redoLastAction());
	    brushBtn.addActionListener(e -> window.setPaintMode(PaintMode.BRUSH));
        sprayBtn.addActionListener(e -> window.setPaintMode(PaintMode.SPRAY));
        rectBtn.addActionListener(e -> window.setPaintMode(PaintMode.RECT));
		eraseBtn.addActionListener(e -> window.setPaintMode(PaintMode.ERASE));
        colorBtn.addActionListener(e -> {
        	Color initColor = Color.BLACK;
        	// Open color chooser on click
        	Color color = JColorChooser.showDialog(this, "Select a Color", initColor);
        	// Set the color using the method from the MainWindow unless user hit cancel
        	if (color != null) {
        		window.setCurrentColor(color);
        	}
        });
        sizeBox.addActionListener(e -> {
        	String selected = (String) sizeBox.getSelectedItem();
        	int selectToInt = Integer.parseInt(selected);
        	window.setCurrentStroke(selectToInt);
        });
        clearBtn.addActionListener(e -> {
        	int response = JOptionPane.showConfirmDialog(
        		    window,
        		    "This will clear the canvas and cannot be undone. Continue?",
        		    "Warning!",
        		    JOptionPane.OK_CANCEL_OPTION,
        		    JOptionPane.WARNING_MESSAGE
        		);

        		if (response == JOptionPane.OK_OPTION) {
        		    // User clicked OK -> Proceed with operation
        			window.clearCanvas();
        		}
        });
	    
	    // Assemble and display
		this.add(undoBtn);
		this.add(redoBtn);
	    this.add(saveBtn);
	    this.add(loadBtn);
	    this.add(brushBtn);
	    this.add(sprayBtn);
	    this.add(rectBtn);
	    this.add(eraseBtn);
	    this.add(colorBtn);
	    this.add(sizeBox);
	    this.add(clearBtn);
    }
    
}
