package viewmodel;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import processing.core.PApplet;

public class WinLostCell {
	public Rectangle2D.Float rect = new Rectangle2D.Float();
	int color[] = new int[3];
	int strokeW = 1;
	ArrayList<LittleGameBar> gameBarList = new ArrayList<LittleGameBar>();
	
	public WinLostCell (int x, int y, int width, int height) {
		rect.x = x;
		rect.y = y;
		rect.width = width;
		rect.height = height;
	}
	
	public void setColor (int[] color) {
		this.color[0] = color[0];
		this.color[1] = color[1];
		this.color[2] = color[2];
	}
	
	public void addLittleGameBar (int gameIndex, int x, int y, int width, int height, int[] color) {
		gameBarList.add(new LittleGameBar(gameIndex, x, y, width, height, color));
	}
	
	public void setNewPosition (int newX, int newY) {
		int transX = newX - (int)rect.x;
		int transY = newY - (int)rect.y;
		rect.x = newX;
		rect.y = newY;
		for (int i = 0; i < gameBarList.size(); i++) {
			gameBarList.get(i).leftTopX += transX;
			gameBarList.get(i).leftTopY += transY;
		}
		
	}
	
	public void draw (SeasonCanvas canvas) {
		canvas.pushStyle();
		canvas.stroke(180, 180, 180);
		canvas.strokeWeight(strokeW);
		if (canvas.mouseX < rect.x + rect.width && canvas.mouseX > rect.x &&
				canvas.mouseY > rect.y && canvas.mouseY < rect.y + rect.height) {
			canvas.stroke(50);
			canvas.strokeWeight(2);
		}
		
		canvas.fill(color[0], color[1], color[2]);
		canvas.rect(rect.x, rect.y, rect.width, rect.height);
		for (int i = 0; i < gameBarList.size(); i++) {
			gameBarList.get(i).draw(canvas);
		}		
		canvas.popStyle();
	}
	
	public boolean isMouseHover (SeasonCanvas canvas) {
		if (rect.contains(canvas.mouseX, canvas.mouseY)) {
			return true;
		}
		return false;
	}
	
	
}