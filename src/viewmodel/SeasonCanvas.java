package viewmodel;

import datamodel.Database;
import processing.core.PApplet;
import processing.core.PFont;

public class SeasonCanvas extends PApplet {
	final int[] colorForWinMore = {252,146,114};
	final int[] colorForWinLess = {173,221,142};
	final int[] colorForWinEqual = {222,235,247};
	final int[] colorForWinBar = {99,99,99};
	final int[] colorForLoseBar = {240,240,240};
	final int[] whiteColor = {240,240,240};
	
	
	public static int displayType = 1;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int width, height;
	int textSize = 12;
	PFont font = createFont("Sans Serif", textSize);
	
	int leftTopX = 150, leftTopY = 50;
	
	Database database;
	// test diff score horizon graph
	OppoView oppoView;
	public TimeView timeView;

	public void setOppoView (int startX, int startY, int width, int height) {
		oppoView = new OppoView(database);
		oppoView.setPosition(startX, startY, width, height);
		oppoView.setup();
	}
	public void setTimeView (int startX, int startY, int width, int height) {
		timeView = new TimeView(database);
		timeView.setPosition(startX, startY, width, height);
		timeView.setup();
		
	}
	
	
	static public float translateTimeIndexToXPos (int timeIndex, int maxQuarter, float startX, float endX) {
		int maxTimeIndex = 4 * 12 * 60;
		if (maxQuarter < 4) {
			System.out.println("Error: The number of quarter is less than 4!");
			System.exit(1);
		} else if (maxQuarter > 4) {
			maxTimeIndex = maxTimeIndex + (maxQuarter - 4) * 5 * 60;
		}
		
		return (int) PApplet.map(timeIndex, 0, maxTimeIndex , startX , endX);
	}
	
	
	
	
	public SeasonCanvas() {
		
	}
	
	public SeasonCanvas (Database database, int width, int height) {
		this.database = database;
		this.width = width;
		this.height = height;
	}
	

    @Override
    public void setup () {
    	size(this.width, this.height);
    	textFont(font);
    	setOppoView(leftTopX, leftTopY, this.width, this.height);
    	setTimeView(leftTopX, leftTopY, this.width, this.height);
    	//this.loadXML();
    	//XML xml;
    }
	
    @Override
	public void draw () {
    	background(240);
    	smooth();
    	if (displayType == 1) {
    		timeView.draw(this);
    	} else if (displayType == 2) {
    		oppoView.draw(this);
    	}
    }
    
    
    public void mouseMoved () {
    	if (displayType == 2) {
        	if (mouseX > leftTopX && mouseY > leftTopY
        			&& mouseX < leftTopX + database.teamNum * (oppoView.cellSize + oppoView.cellGap)
        			&& mouseY < leftTopY + database.teamNum * (oppoView.cellSize + oppoView.cellGap)) {
        		oppoView.topHoverTextIndex = (mouseX - leftTopX) / (oppoView.cellSize + oppoView.cellGap);
        		oppoView.leftHoverTextIndex = oppoView.teamSortIndex[(mouseY - leftTopY) / (oppoView.cellSize + oppoView.cellGap)];
        	} else {
        		oppoView.leftHoverTextIndex = -1;
        		oppoView.topHoverTextIndex = -1;
        	}
    	} else if (displayType == 1) {
    		timeView.mouseMoved(this);
    	}
    }
    
    public void mouseClicked () {
    	if (displayType == 1) {
    		timeView.mouseClicked(this);
    	}
    }
    
    public void mouseReleased () {
    	timeView.mouseReleased(this);
    }
}
