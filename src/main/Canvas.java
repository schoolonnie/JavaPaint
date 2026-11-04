package main;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Stack;
import javax.swing.*;

public class Canvas extends JPanel {
    private MainWindow window;
    private BufferedImage canvasImage;
    private Graphics2D g2d;
    private Point cursorPosition;
    private Stack<BufferedImage> undoStack = new Stack<>();
    private Stack<BufferedImage> redoStack = new Stack<>();
    
    private BrushTool brushTool = new BrushTool();
    private SprayTool sprayTool = new SprayTool();
    private RectTool rectTool = new RectTool();
    private EraseTool eraseTool = new EraseTool();

    public Canvas(MainWindow window) {
        this.window = window;
        setBackground(Color.WHITE);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                cursorPosition = e.getPoint();
                int currentWidth = getWidth();
                int currentHeight = getHeight();
                if (currentWidth <= 0 || currentHeight <= 0) return;

                // Run sizing checks to make sure canvasImage exists
                if (canvasImage == null) {
                    canvasImage = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_ARGB);
                    g2d = canvasImage.createGraphics();
                    g2d.setColor(Color.WHITE);
                    g2d.fillRect(0, 0, currentWidth, currentHeight);
                }
                undoStack.push(copyImage(canvasImage));
                redoStack.clear();
                // Update color
                brushTool.setColor(window.getCurrentColor());
                sprayTool.setColor(window.getCurrentColor());
                rectTool.setColor(window.getCurrentColor());
                // Update stroke size
                brushTool.setStrokeSize(window.getCurrentStroke());
                sprayTool.setStrokeSize(window.getCurrentStroke());
                rectTool.setStrokeSize(window.getCurrentStroke());
                eraseTool.setStrokeSize(window.getCurrentStroke());

                switch (window.getPaintMode()) {
                    case BRUSH -> brushTool.onPress(e.getPoint(), g2d);
                    case SPRAY -> sprayTool.onPress(e.getPoint(), g2d);
                    case RECT -> rectTool.onPress(e.getPoint(), g2d);
                    case ERASE -> eraseTool.onPress(e.getPoint(), g2d);
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                cursorPosition = e.getPoint();
            	// Update color
                brushTool.setColor(window.getCurrentColor());
                sprayTool.setColor(window.getCurrentColor());
                rectTool.setColor(window.getCurrentColor());
                // Update stroke size
                brushTool.setStrokeSize(window.getCurrentStroke());
                sprayTool.setStrokeSize(window.getCurrentStroke());
                rectTool.setStrokeSize(window.getCurrentStroke());
                eraseTool.setStrokeSize(window.getCurrentStroke());
                switch (window.getPaintMode()) {
                    case BRUSH -> brushTool.onDrag(e.getPoint(), g2d);
                    case SPRAY -> sprayTool.onDrag(e.getPoint(), g2d);
                    case RECT -> rectTool.onDrag(e.getPoint(), g2d); // Just updates coordinates
                    case ERASE -> eraseTool.onDrag(e.getPoint(), g2d);
                }
                repaint(); // Tells paintComponent to redraw and show the preview box live when drawing rectangles
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                cursorPosition = e.getPoint();
                // When the mouse lifts, finalize the rectangle into the image
                if (window.getPaintMode() == PaintMode.RECT) {
                    rectTool.onRelease(e.getPoint(), g2d);
                }
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                cursorPosition = e.getPoint();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                cursorPosition = null;
                repaint();
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //Get window size
        int currentWidth = getWidth();
        int currentHeight = getHeight();
        
        // If the panel size is invalid, exit
        if (currentWidth <= 0 || currentHeight <= 0) return; 
        
        // Check if window size has been changed
        if (canvasImage == null || canvasImage.getWidth() < currentWidth || canvasImage.getHeight() < currentHeight) {
            
            // Target larger bounds to accommodate sudden window expansions smoothly
            int newWidth = Math.max(currentWidth, canvasImage != null ? canvasImage.getWidth() : 0);
            int newHeight = Math.max(currentHeight, canvasImage != null ? canvasImage.getHeight() : 0);
            
            // Scale the buffered image (drawing)
            BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D newG2d = newImage.createGraphics();
            
            // Apply smooth anti-aliasing configurations so it isn't blurry from scaling
            newG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fill the new canvas expansion area with your default background color
            newG2d.setColor(Color.WHITE);
            newG2d.fillRect(0, 0, newWidth, newHeight);
            
            // Copy existing brush strokes onto the new canvas buffer
            if (canvasImage != null) {
                newG2d.drawImage(canvasImage, 0, 0, null);
                g2d.dispose(); // Discard old graphics
            }
            
            // Swap in new tracking pointers
            canvasImage = newImage;
            g2d = newG2d;
        }
        //Render drawing
        g.drawImage(canvasImage, 0, 0, null);
        
        // Draw the live preview for rectangles being drawn
        if (window.getPaintMode() == PaintMode.RECT) {
            Graphics2D previewG2d = (Graphics2D) g.create(); // Create a temp pen
            
            // Turn on smoothing for the preview line
            previewG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Ask the tool to sketch its guide box onto the screen
            rectTool.drawPreview(previewG2d);
            
            previewG2d.dispose(); // Destroy the temp pen safely

            // Draw cursor preview for drawing tools
        } else if (cursorPosition != null) {
            int diameter = window.getCurrentStroke();
            if (window.getPaintMode() == PaintMode.SPRAY) {
                diameter *= 6;
            } else if (window.getPaintMode() == PaintMode.ERASE) {
                diameter *= 10;
            }

            Graphics2D previewG2d = (Graphics2D) g.create();
            previewG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            previewG2d.setColor(new Color(0, 0, 0, 150));
            previewG2d.setStroke(new BasicStroke(1f));
            previewG2d.drawOval(
                    cursorPosition.x - diameter / 2,
                    cursorPosition.y - diameter / 2,
                    diameter,
                    diameter);
            previewG2d.dispose();
        }
    }
    // Getter for saving and loading BufferedImage
    public BufferedImage getCurrentImage() {
    	return canvasImage;
    }
    // Setter for loading BufferedImage
    public void setCurrentImage(BufferedImage newImage) {
	    if (g2d != null) {
	        g2d.dispose();
	    }
    	// Replace the current canvas image with the new one
    	canvasImage = new BufferedImage(newImage.getWidth(), newImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    	g2d = canvasImage.createGraphics();
    	g2d.drawImage(newImage, 0, 0, null);
    	repaint(); // Refresh the canvas to show the new image
    }

    private BufferedImage copyImage(BufferedImage source) {
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D copyGraphics = copy.createGraphics();
        copyGraphics.drawImage(source, 0, 0, null);
        copyGraphics.dispose();
        return copy;
    }

    // public method to undo the last action
    public void undoLastAction() {
        if (!undoStack.isEmpty()) {
            // Pop the last state from the stack and set it as the current image
            BufferedImage lastState = undoStack.pop();
            redoStack.push(copyImage(canvasImage));
            setCurrentImage(lastState);
        } else {
            // Optionally, you can show a message that there's nothing to undo
            JOptionPane.showMessageDialog(this, "Nothing to undo!", "Undo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    // public method to redo the last undone action
    public void redoLastAction() {
        if (!redoStack.isEmpty()) {
            // Pop the last state from the redo stack and set it as the current image
            BufferedImage nextState = redoStack.pop();
            undoStack.push(copyImage(canvasImage));
            setCurrentImage(nextState);
        } else {
            // Optionally, you can show a message that there's nothing to redo
            JOptionPane.showMessageDialog(this, "Nothing to redo!", "Redo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
