package main;

import java.awt.*;

public class RectTool extends PaintTool {
    private Point startPoint;
    private Point currentPoint;
    private boolean isDrawing = false;

    @Override
    public void onPress(Point point, Graphics2D g2d) {
        this.startPoint = point;
        this.currentPoint = point;
        this.isDrawing = true;
    }

    @Override
    public void onDrag(Point point, Graphics2D g2d) {
        // Only update the live mouse position while dragging
        this.currentPoint = point;
    }

    // On release, finalize rectangle on canvas
    public void onRelease(Point releasePoint, Graphics2D permanentG2d) {
        if (!isDrawing || permanentG2d == null) return;
        
        this.currentPoint = releasePoint;
        permanentG2d.setColor(this.color);
        permanentG2d.setStroke(new BasicStroke(this.strokeSize));
        
        // Commit the final rectangle to the permanent image buffer
        drawCalculatedRect(permanentG2d, startPoint, currentPoint);
        
        // Reset the tool state
        this.isDrawing = false;
        this.startPoint = null;
        this.currentPoint = null;
    }

    // Draw a temporary live preview of the rectangle
    public void drawPreview(Graphics2D previewG2d) {
        if (!isDrawing || startPoint == null || currentPoint == null) return;
        
        previewG2d.setColor(this.color);
        previewG2d.setStroke(new BasicStroke(this.strokeSize));
        
        // Draw the preview box
        drawCalculatedRect(previewG2d, startPoint, currentPoint);
    }

    // Calculate absolute values for when the rectangle values are negative
    private void drawCalculatedRect(Graphics2D g, Point p1, Point p2) {
        int x = Math.min(p1.x, p2.x);
        int y = Math.min(p1.y, p2.y);
        int width = Math.abs(p1.x - p2.x);
        int height = Math.abs(p1.y - p2.y);
        
        g.drawRect(x, y, width, height);
    }
}
