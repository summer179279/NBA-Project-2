package viewmodel;

import datamodel.Database;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShape;

public class SingleGameCanvas extends PApplet {
	
	static final int quarterGapWidth = 15;

	public int segTeam = 0;
	public int[] segX = {0,0};
	int textSize = 12;
	
	//resources
	//	shapes
	PShape twoPts;
	PShape threePts;
	PShape free;
	PShape ast;
	PShape blk;
	PShape stl;
	PShape defRbd;
	PShape ofRbd;
	PShape foul;
	PShape turnover;
	PShape miss;
	//	fonts
	PFont font = createFont("Sans Serif", textSize);
	
	//game info
	Database database;
	int gameIndex;
	
	//screen parameters
	int singleGameCanvasWidth, singleGameCanvasHeight;

	public SingleGameView singleGame;
			
	public SingleGameCanvas (int gameIndex, Database database, int singleGameCanvasWidth, int singleGameCanvasHeight) {
		this.gameIndex = gameIndex;
		this.database = database;
		this.singleGameCanvasWidth = singleGameCanvasWidth;
		this.singleGameCanvasHeight = singleGameCanvasHeight;
	}
	
	public void setSingleGameView (int gameIndex, int singleGameCanvasWidth, int singleGameCanvasHeight) {
		singleGame = new SingleGameView(gameIndex, database);
		singleGame.setstrokesBackgroundSize((float)singleGameCanvasWidth, (float)singleGameCanvasHeight);
		singleGame.setup();
	}
	
    @Override
    public void setup () {
    	size(this.singleGameCanvasWidth, this.singleGameCanvasHeight);
    	smooth();
    	textFont(font);
		setSingleGameView(gameIndex, (int)(singleGameCanvasWidth), (int)(singleGameCanvasHeight));
		
		shapeMode(CENTER);
		twoPts = loadShape("./icon/2points.svg");
		threePts = loadShape("./icon/3points.svg");
		free = loadShape("./icon/free_throw.svg");
		ast = loadShape("./icon/assist.svg");
		blk = loadShape("./icon/block.svg");
		stl = loadShape("./icon/steal.svg");
		defRbd = loadShape("./icon/def_rebound.svg");
		ofRbd = loadShape("./icon/of_rebound.svg");
		foul = loadShape("./icon/foul.svg");
		turnover = loadShape("./icon/turnover.svg");
		miss = loadShape("./icon/miss.svg"); 
		
    }
	
    @Override
	public void draw () {
    	background(255);
    	smooth();
    	singleGame.draw(this);
    }
    
    @Override
    public void mouseMoved () {
    	singleGame.mouseMoved(this);
    }
    
    @Override
    public void mouseClicked () {
    	singleGame.mouseClicked(this);
    }
    
    
	static public float translateTimeIndexToXPos (int timeIndex, int maxQuarter, float startX, float endX) {		
		float totalLength = endX - startX;
		
		int whichQuarter, timeIndexWithinQuarter;
		float quarterStartX, quarterLength;
		
		float result = 0f;
		
		if (maxQuarter <= 4) {
			whichQuarter = timeIndex / 720;
			timeIndexWithinQuarter = timeIndex % 720;
			quarterLength = (totalLength - 3 * quarterGapWidth) / 4;
			quarterStartX = (quarterLength + quarterGapWidth) * whichQuarter;
			result = PApplet.map(timeIndexWithinQuarter, 0, 720, quarterStartX, quarterStartX + quarterLength);
			
		} else {
			whichQuarter = (timeIndex - 2880) / 300;
			timeIndexWithinQuarter = (timeIndex - 2880) % 300;
			quarterLength = (totalLength - (maxQuarter - 1) * quarterGapWidth) / (48 + (maxQuarter - 4) * 5);
			quarterStartX = (quarterLength * 12 + quarterGapWidth) * 4 + (quarterLength * 5 + quarterGapWidth) * whichQuarter;
			result = PApplet.map(timeIndexWithinQuarter, 0, 300, quarterStartX, quarterLength);
		}
		
		return result + startX;
	}
    
}
