package main;

import java.awt.*;

public class EraseTool extends PaintTool {
    private Point lastPoint;

    @Override
    public void onPress(Point startPoint, Graphics2D g2d) {
        this.lastPoint = startPoint;
        onDrag(startPoint, g2d);
    }

    @Override
    public void onDrag(Point currentPoint, Graphics2D g2d) {
        if (g2d != null && lastPoint != null) {
            g2d.setStroke(new BasicStroke((this.strokeSize * 10), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.setColor(Color.WHITE);
            g2d.drawLine(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y);
        }
        this.lastPoint = currentPoint;
    }
}