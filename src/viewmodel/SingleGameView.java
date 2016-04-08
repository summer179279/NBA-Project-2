package viewmodel;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import datamodel.Database;

public class SingleGameView {
	// data related
	int gameIndex;
	Database database;

	// draw related
	Rectangle2D.Float strokesBackground = new Rectangle2D.Float();
	float centerY;
	float strokesStartX = 200, strokesStartY = 0;
	float strokesEndX, strokesEndY;
	float strokesOffsetX = 50;
	float strokesOffsetY = 0;
	
	VerticalLineupStrokes verticalLineupStrokes;
	
	public SingleGameView (int gameIndex, Database database) {
		this.gameIndex = gameIndex;
		this.database = database;
	}
	
	public void setup () {
		verticalLineupStrokes = new VerticalLineupStrokes(gameIndex, database, strokesBackground.x, strokesBackground.x + strokesBackground.width, centerY);
		verticalLineupStrokes.setup();
	}
	
	public void setstrokesBackgroundSize (float singleGameCanvasWidth, float singleGameCanvasHeight) {
		this.strokesBackground.x = strokesStartX;
		this.strokesBackground.y = strokesStartY;
		this.strokesEndX = singleGameCanvasWidth - strokesOffsetX;
		this.strokesEndY = singleGameCanvasHeight - strokesOffsetY;
		this.strokesBackground.width = strokesEndX-strokesStartX;
		this.strokesBackground.height = strokesEndY-strokesStartY;
		centerY = strokesBackground.y + strokesBackground.height / 2;
	}
	
	public void draw (SingleGameCanvas canvas) {
		verticalLineupStrokes.draw(canvas);
	}
	
	public void mouseMoved (SingleGameCanvas canvas) {
		verticalLineupStrokes.mouseMoved(canvas);
	}
	public void mouseClicked (SingleGameCanvas canvas) {
		verticalLineupStrokes.mouseClicked(canvas);
	}
	
}
