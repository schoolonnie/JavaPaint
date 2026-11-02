package main;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

public class SprayTool extends PaintTool {
	private Random random = new Random();
	
	@Override
	public void onPress(Point startPoint, Graphics2D g2d) {
		onDrag(startPoint, g2d); // Spray instantly on click
	}
	
	@Override
	public void onDrag(Point point, Graphics2D g2d) {
		if(g2d == null) return;
		
		g2d.setColor(this.color);
		
		int radius = 15; // Spray area radius
		int dotCount = 20; // Paint drops per spray
		
		for (int i = 0; i < dotCount; i++) {
			// Generate offset based on mouse position
			int offsetX = random.nextInt(radius * 2) - radius;
            int offsetY = random.nextInt(radius * 2) - radius;
            // Draw tiny 2x2 dots
            g2d.fillOval(point.x + offsetX, point.y + offsetY, 2, 2);
		}
	}
}
