package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel {
    private MainWindow window;
    private BufferedImage canvasImage;
    private Graphics2D g2d;
    
    private BrushTool brushTool = new BrushTool();
    private SprayTool sprayTool = new SprayTool();
    private RectTool rectTool = new RectTool();

    public Canvas(MainWindow window) {
        this.window = window;
        setBackground(Color.WHITE);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
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

                switch (window.getPaintMode()) {
                    case BRUSH -> brushTool.onPress(e.getPoint(), g2d);
                    case SPRAY -> sprayTool.onPress(e.getPoint(), g2d);
                    case RECT -> rectTool.onPress(e.getPoint(), g2d);
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                switch (window.getPaintMode()) {
                    case BRUSH -> brushTool.onDrag(e.getPoint(), g2d);
                    case SPRAY -> sprayTool.onDrag(e.getPoint(), g2d);
                    case RECT -> rectTool.onDrag(e.getPoint(), g2d); // Just updates coordinates
                }
                repaint(); // Tells paintComponent to redraw and show the preview box live when drawing rectangles
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // When the mouse lifts, finalize the rectangle into the image
                if (window.getPaintMode() == PaintMode.RECT) {
                    rectTool.onRelease(e.getPoint(), g2d);
                }
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
        }
    }
}
