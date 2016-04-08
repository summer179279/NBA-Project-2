package viewmodel;

import java.awt.geom.Ellipse2D;
import processing.core.PApplet;
import datamodel.SinglePlayer;

public class PlayerCircle {
	static final float radius = 10f;
	
	SinglePlayer pgs;
	Ellipse2D.Float circle = new Ellipse2D.Float();
	int[] color = new int[3];
	
	boolean hover = false;
	boolean clicked = false;
		
	public PlayerCircle (SinglePlayer pgs) {
		this.pgs = pgs;
		this.circle.width = (float)2.5 * radius;
		this.circle.height = (float)2.5 * radius;
		
		// set color
		String pos = this.pgs.pos;
		int tempI = 3;
		if (pos != null) {
			if (pos.startsWith("G")) {
				tempI = 0;
			} else if (pos.startsWith("F")) {
				tempI = 1;
			} else if (pos.startsWith("C")) {
				tempI = 2;
			}
		} else {
			tempI = 3;
		}
		
		color[0] = VerticalLineupStrokes.colorForPositions[tempI * 3];
		color[1] = VerticalLineupStrokes.colorForPositions[tempI * 3 + 1];
		color[2] = VerticalLineupStrokes.colorForPositions[tempI * 3 + 2];
		
	}
	
	public void setXYPosition (float x, float y) {
		if (circle == null) {
			circle = new Ellipse2D.Float();
		}
		circle.x = x;
		circle.y = y;
	}
	
	public void draw (SingleGameCanvas canvas) {
		canvas.pushStyle();
		
		if (clicked) {
			canvas.fill(color[0], color[1], color[2]);
			canvas.noStroke();
			canvas.ellipse(circle.x, circle.y, circle.width, circle.height);
		} else{
			canvas.stroke(color[0], color[1], color[2]);
			canvas.strokeWeight(3);
			canvas.noFill();
			canvas.ellipse(circle.x, circle.y, circle.width, circle.height);
		}
		
		// draw player jersey
		canvas.fill(0);
		canvas.textSize(15);
		canvas.textAlign(PApplet.CENTER, PApplet.CENTER);
		canvas.text(pgs.getJersey(), circle.x, circle.y);
		canvas.popStyle();
	}
	
	public void mouseMoved (SingleGameCanvas canvas) {
		if (PApplet.dist(canvas.mouseX, canvas.mouseY, circle.x, circle.y) <= radius) {
			this.hover = true;
		} else {
			this.hover = false;
		}
	}
	
	public void mouseClicked (SingleGameCanvas canvas) {
		if (this.hover == true) {
			this.clicked = !this.clicked;
			if (VerticalLineupStrokes.clickedPGS.contains(this.pgs)) {
				VerticalLineupStrokes.clickedPGS.remove(this.pgs);
				
			} else {
				VerticalLineupStrokes.clickedPGS.add(this.pgs);
			}
			
			//System.out.println("press");
		}
	}
}
