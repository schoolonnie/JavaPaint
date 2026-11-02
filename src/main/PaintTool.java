package main;

import java.awt.*;

public abstract class PaintTool {
	//Inherited data
	protected Color color = Color.BLACK;
	protected int strokeSize = 5;
	
	//Setters
	public void setColor(Color color) { this.color = color; }
	public void setStrokeSize(int size) { this.strokeSize = size; }
	
	//Abstract methods
	public abstract void onPress(Point point, Graphics2D g2d);
	public abstract void onDrag(Point point, Graphics2D g2d);
}
