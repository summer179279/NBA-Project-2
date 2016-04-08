package viewmodel;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import processing.core.PApplet;
import viewmodel.ScoreDiffLine.PointEntry;
import viewmodel.VerticalLineupStrokes.SingleStroke.PlayerStackRect;
import datamodel.Database;
import datamodel.SingleGame;
import datamodel.MadeEvent;
import datamodel.SinglePlayer;

public class VerticalLineupStrokes {
	static final int[] colorForPositions = {253,174,97, 180,232,220, 171,221,164, 89,175,217};
	static final int lineupHeight = 100;
	static final float animationRatio = 0.6f;
    
	Database database;
	int gameIndex;
	int maxQuarter;
	int maxGetPoint = -Integer.MAX_VALUE;
	
	//parameters for drawing
	//for the stroke field
	float totalStartX, totalEndX;
	float zeroDiffY;
	boolean zoomOut;
	
	SingleGameInfo singleGameInfo;
	ScoreDiffLine scoreDiffLine;
	HashMap<Integer, ArrayList<PointEntry>> quarterPointEntry;
	
	static final float littleBarWidth = 2f;//6f
	
	static PlayerStackRect hoverPlayerStackRect = null;
	static ArrayList<SinglePlayer> clickedPGS = new ArrayList<SinglePlayer>();
	
	ArrayList<PlayerCircle> upPlayersCircleList = new ArrayList<PlayerCircle>();
	ArrayList<PlayerCircle> downPlayersCircleList = new ArrayList<PlayerCircle>();
	
	ArrayList<SingleStroke> singleStrokeList = new ArrayList<SingleStroke>();
	ArrayList<ScoreDiffsStroke> scoreDiffsStrokeList = new ArrayList<ScoreDiffsStroke>();
	
	public VerticalLineupStrokes (int gameIndex, Database database, float totalStartX, float totalEndX, float zeroDiffY) {
		this.gameIndex = gameIndex;
		this.database = database;
		this.maxQuarter = database.gameMap.get(gameIndex).getMaxQuarter();
		this.totalStartX = totalStartX;
		this.totalEndX = totalEndX;
		this.zeroDiffY = zeroDiffY;
	}
	
	private int getQuarterTimeIndex (int quarter) {
		if (quarter < 4) {
			return quarter * 720 + 720;
		} else {
			return 2880 + (quarter - 3) * 300;
		}
	}
	
	public void setup () {
		setupScoreDiffLine();
		setSingleStrokeList();
		setupPlayersCircleList();
		setupSingleGameInfo();
	}
	
	private void setupScoreDiffLine() {
		scoreDiffLine = new ScoreDiffLine(gameIndex, database, zeroDiffY);
		quarterPointEntry = scoreDiffLine.quarterPointEntry;
		scoreDiffLine.setup();
	}

	private void setupSingleGameInfo() {
		String leftName = database.teamsMap.get(database.gameMap.get(gameIndex).leftTeamIndex).name;
		String rightName = database.teamsMap.get(database.gameMap.get(gameIndex).rightTeamIndex).name;
		singleGameInfo = new SingleGameInfo(totalStartX, totalEndX, zeroDiffY, maxQuarter, leftName, rightName); 
	}

	public void setupPlayersCircleList () {
		SingleGame game = database.gameMap.get(gameIndex);
		float xPostion = totalStartX;		
		float upStartY = zeroDiffY - 380;
		float downStartY = zeroDiffY + 380;
		float circleGap = 15;
		PlayerCircle tempPlayerCircle;
		for (int i = 0; i < game.leftPlayers.size(); i++) {
			tempPlayerCircle = new PlayerCircle(game.leftPlayers.get(i));
			tempPlayerCircle.setXYPosition(xPostion + i * (PlayerCircle.radius * 2 + circleGap), upStartY);
			upPlayersCircleList.add(tempPlayerCircle);
		}
		for (int i = 0; i < game.rightPlayers.size(); i++) {
			tempPlayerCircle = new PlayerCircle(game.rightPlayers.get(i));
			tempPlayerCircle.setXYPosition(xPostion + i * (PlayerCircle.radius * 2 + circleGap), downStartY);
			downPlayersCircleList.add(tempPlayerCircle);
		}
	}
	
	public void setSingleStrokeList () {
		if (singleStrokeList == null) {
			singleStrokeList = new ArrayList<SingleStroke>();
		}
		
		ArrayList<Integer> selfTime = new ArrayList<Integer>();
		ArrayList<Integer> scoreDiff = new ArrayList<Integer>();
		ArrayList<MadeEvent> selfMadeEvent = new ArrayList<MadeEvent>();
		ArrayList<MadeEvent> oppoMadeEvent = new ArrayList<MadeEvent>();
		SingleGame game = database.gameMap.get(gameIndex);
		
		boolean[] flag = new boolean[game.quarter];
		for (int i = 0; i < flag.length; i++) {
			flag[i] = false;
		}
			
		int selfTeamIndex, oppoTeamIndex;
		
		selfTeamIndex = game.leftTeamIndex;
		oppoTeamIndex = game.rightTeamIndex;
				
		MadeEvent tempEvent;
		scoreDiff.add(0);
		for (int i = 0; i < game.eventList.size(); i++) {
			if (game.eventList.get(i) instanceof MadeEvent) {
				tempEvent = game.eventList.get(i).getMadeInstance();
				if (tempEvent.actionTeamIndex == selfTeamIndex) {
					selfMadeEvent.add(tempEvent);
				} else if (tempEvent.actionTeamIndex == oppoTeamIndex) {
					oppoMadeEvent.add(tempEvent);
				}
			} else if (game.eventList.get(i).getType().equals("enter")) {
					for (int j = 0; j < flag.length; j++) {
						if (flag[j] == false && game.eventList.get(i).timeIndex > getQuarterTimeIndex(j)) {
							selfTime.add(getQuarterTimeIndex(j) - 1);
							if (scoreDiff.size() > 0) {
								scoreDiff.add(scoreDiff.get(scoreDiff.size() - 1));
							}
							
							flag[j] = true;
						}
					}
					selfTime.add(game.eventList.get(i).timeIndex);
					scoreDiff.add(game.eventList.get(i).getScoreDiff());
					
			}
		}
		

		selfTime.add(getQuarterTimeIndex(game.quarter - 1) - 1);
		scoreDiff.add(game.leftScore - game.rightScore);
		
		

		// set self
		int lastTimeIndex = 0;
		
		int upPoint = 0, downPoint = 0;
		for (int i = 0; i < selfTime.size(); i++) {
			if (selfTime.get(i) == lastTimeIndex) {
				continue;
			} else {
				upPoint = 0;
				downPoint = 0;
				for (int j = 0; j < selfMadeEvent.size(); j++) {
					if (selfMadeEvent.get(j).timeIndex >= lastTimeIndex && selfMadeEvent.get(j).timeIndex < selfTime.get(i)) {
						upPoint += selfMadeEvent.get(j).getPoint();
					}
				}
				for (int j = 0; j < oppoMadeEvent.size(); j++) {
					if (oppoMadeEvent.get(j).timeIndex >= lastTimeIndex && oppoMadeEvent.get(j).timeIndex < selfTime.get(i)) {
						downPoint += oppoMadeEvent.get(j).getPoint();
					}
				}
				if (upPoint > maxGetPoint) {
					maxGetPoint = upPoint;
				}
				if (downPoint > maxGetPoint) {
					maxGetPoint = downPoint;
				}
				
				SingleStroke seg = new SingleStroke(lastTimeIndex, selfTime.get(i), upPoint,  downPoint, getYPositionByScoreDiff(scoreDiff.get(i)));
				singleStrokeList.add(seg);
				lastTimeIndex = selfTime.get(i);
			}
		}
		
		for (int i = 0; i < singleStrokeList.size(); i++) {
			singleStrokeList.get(i).setup();
		}
		setSingleStrokePreCircleXY();
		setSingleStrokeNextCircleXY();
	}
	
	
	public float getYPositionByScoreDiff (int scoreDiff) {
//		float h = PApplet.map(scoreDiff >= 0 ? scoreDiff : -scoreDiff, 0, database.gameMap.get(gameIndex).getMaxScoreDiff(),
//				0, background.height / 2);
		float h = PApplet.map(scoreDiff >= 0 ? scoreDiff : -scoreDiff, 0, database.gameMap.get(gameIndex).getMaxScoreDiff(),
		0, scoreDiffLine.scoreDiffHeight);
		return (scoreDiff >= 0 ? zeroDiffY - h : zeroDiffY + h);
	}

	//main draw
	public void draw (SingleGameCanvas canvas) {
		
		
		
		canvas.fill(220,220,220,20);
		canvas.noStroke();
		canvas.rect(totalStartX-10, zeroDiffY - 350, totalEndX-totalStartX+10, 700);
		canvas.fill(220,220,220,100);
		canvas.rect(totalStartX-10, zeroDiffY - 110, totalEndX-totalStartX+10, 220);
		
		singleGameInfo.draw(canvas, zoomOut);
		

		//up line
		for (int i = 0; i < upPlayersCircleList.size(); i++) {
			upPlayersCircleList.get(i).draw(canvas);
		}
		//down line
		for (int i = 0; i < downPlayersCircleList.size(); i++) {
			downPlayersCircleList.get(i).draw(canvas);
		}
		//main part
//		int preUpJerseySum = 0;
//		int preDownJerseySum = 0;
//		for (int i = 0; i < singleStrokeList.size(); i++) {
//			int upJerseySum = singleStrokeList.get(i).upJerseySum;
//			int downJerseySum = singleStrokeList.get(i).downJerseySum;
//			if(upJerseySum!=preUpJerseySum){
//				singleStrokeList.get(i).draw(canvas, 0);
//			}
//			if(downJerseySum!=preDownJerseySum){
//			singleStrokeList.get(i).draw(canvas, 1);
//			}
//			
//			preUpJerseySum = upJerseySum;
//			preDownJerseySum = downJerseySum;
//			
//
//		}
		
		
	
		for (int i = 0; i < singleStrokeList.size(); i++) {
			singleStrokeList.get(i).draw(canvas, 0);
			singleStrokeList.get(i).draw(canvas, 1);
		}
		
	}
	
	public void mouseMoved (SingleGameCanvas canvas) {
		for (int i = 0; i < singleStrokeList.size(); i++) {
			singleStrokeList.get(i).mouseMoved(canvas);
			if (VerticalLineupStrokes.hoverPlayerStackRect != null) {
				break;
			}
		}
		for (int i = 0; i < upPlayersCircleList.size(); i++) {
			upPlayersCircleList.get(i).mouseMoved(canvas);
		}
		for (int i = 0; i < downPlayersCircleList.size(); i++) {
			downPlayersCircleList.get(i).mouseMoved(canvas);
		}
	}
	
	public void mouseClicked (SingleGameCanvas canvas) {

		for (int i = 0; i < singleStrokeList.size(); i++) {
			singleStrokeList.get(i).mouseClicked(canvas);
		}
		for (int i = 0; i < upPlayersCircleList.size(); i++) {
			upPlayersCircleList.get(i).mouseClicked(canvas);
		}
		for (int i = 0; i < downPlayersCircleList.size(); i++) {
			downPlayersCircleList.get(i).mouseClicked(canvas);
		}
	}
	
	// set the previous circle center
	public void setSingleStrokePreCircleXY () {
		if (singleStrokeList == null || singleStrokeList.size() <= 1) {
			return;
		}
		SingleStroke tempStroke, preStroke;
		preStroke = singleStrokeList.get(0);
		//first stroke
		for (int i = 0; i < preStroke.upPlayers.size(); i++) {
			PlayerStackRect psr = preStroke.upPlayers.get(i);
			psr.setPreCircleXY(psr.circleX, psr.circleY);
			psr.setPreRect(psr.rect);
			psr.preAtCourt = false;
		}
		for (int i = 0; i < preStroke.downPlayers.size(); i++) {
			PlayerStackRect psr = preStroke.downPlayers.get(i);
			psr.setPreCircleXY(psr.circleX, psr.circleY);
			psr.setPreRect(psr.rect);
			psr.preAtCourt = false;
		}
		//following strokes
		for (int i = 1; i < singleStrokeList.size(); i++) {
			tempStroke = singleStrokeList.get(i);
			// up players
			for (int k = 0; k < tempStroke.upPlayers.size(); k++) {
				if (preStroke.playerMap.containsKey(tempStroke.upPlayers.get(k).pgs)) {
					PlayerStackRect psr = preStroke.playerMap.get(tempStroke.upPlayers.get(k).pgs);
					tempStroke.upPlayers.get(k).setPreCircleXY(psr.circleX, psr.circleY);
					tempStroke.upPlayers.get(k).setPreRect(psr.rect);
					tempStroke.upPlayers.get(k).preAtCourt = true; 
				} else {
					tempStroke.upPlayers.get(k).setPreCircleXY(preStroke.upNotAtCourtCircle.x, preStroke.upNotAtCourtCircle.y);
					tempStroke.upPlayers.get(k).setPreRect(new Rectangle2D.Float(preStroke.upNotAtCourtCircle.x, preStroke.upNotAtCourtCircle.y, 0, 0));
					tempStroke.upPlayers.get(k).preAtCourt = false; 
				}
			}
			// down players
			for (int k = 0; k < tempStroke.downPlayers.size(); k++) {
				if (preStroke.playerMap.containsKey(tempStroke.downPlayers.get(k).pgs)) {
					PlayerStackRect psr = preStroke.playerMap.get(tempStroke.downPlayers.get(k).pgs);
					tempStroke.downPlayers.get(k).setPreCircleXY(psr.circleX, psr.circleY);
					tempStroke.downPlayers.get(k).setPreRect(psr.rect);
					tempStroke.downPlayers.get(k).preAtCourt = true;
				} else {
					tempStroke.downPlayers.get(k).setPreCircleXY(preStroke.downNotAtCourtCircle.x, preStroke.downNotAtCourtCircle.y);
					tempStroke.downPlayers.get(k).setPreRect(new Rectangle2D.Float(preStroke.downNotAtCourtCircle.x, preStroke.downNotAtCourtCircle.y, 0, 0));
					tempStroke.downPlayers.get(k).preAtCourt = false;
				}
			}
			preStroke = tempStroke;
		}		
	}
	
	// set the next circle center
	public void setSingleStrokeNextCircleXY () {
		if (singleStrokeList == null || singleStrokeList.size() <= 1) {
			return;
		}
		SingleStroke tempStroke, nextStroke;
		nextStroke = singleStrokeList.get(singleStrokeList.size()-1);
		//last stroke
		for (int i = 0; i < nextStroke.upPlayers.size(); i++) {
			PlayerStackRect psr = nextStroke.upPlayers.get(i);
			psr.setNextCircleXY(totalEndX, psr.circleY);	
			psr.nextAtCourt = false;
		}
		for (int i = 0; i < nextStroke.downPlayers.size(); i++) {
			PlayerStackRect psr = nextStroke.downPlayers.get(i);
			psr.setNextCircleXY(totalEndX, psr.circleY);
			psr.nextAtCourt = false;
		}
		//other strokes
		for (int i = singleStrokeList.size()-2; i > -1; i--) {
			tempStroke = singleStrokeList.get(i);
			// up players
			for (int k = 0; k < tempStroke.upPlayers.size(); k++) {
				if (nextStroke.playerMap.containsKey(tempStroke.upPlayers.get(k).pgs)) {
					PlayerStackRect psr = nextStroke.playerMap.get(tempStroke.upPlayers.get(k).pgs);
					tempStroke.upPlayers.get(k).setNextCircleXY(psr.circleX, psr.circleY);
					tempStroke.upPlayers.get(k).nextAtCourt = true; 
				} else {
					tempStroke.upPlayers.get(k).setNextCircleXY(nextStroke.upNotAtCourtCircle.x, nextStroke.upNotAtCourtCircle.y);
					tempStroke.upPlayers.get(k).nextAtCourt = false; 
				}
			}
			// down players
			for (int k = 0; k < tempStroke.downPlayers.size(); k++) {
				if (nextStroke.playerMap.containsKey(tempStroke.downPlayers.get(k).pgs)) {
					PlayerStackRect psr = nextStroke.playerMap.get(tempStroke.downPlayers.get(k).pgs);
					tempStroke.downPlayers.get(k).setNextCircleXY(psr.circleX, psr.circleY);
					tempStroke.downPlayers.get(k).nextAtCourt = true; 
				} else {
					tempStroke.downPlayers.get(k).setNextCircleXY(nextStroke.downNotAtCourtCircle.x, nextStroke.downNotAtCourtCircle.y);
					tempStroke.downPlayers.get(k).nextAtCourt = false; 
				}
			}
			nextStroke = tempStroke;
		}		
	}
	
	//setAllSingleStrokeNewPosition
	public void setAllSingleStrokeNewPosition (SingleStroke zoomoutStroke) {
		int strokeIndex = singleStrokeList.indexOf(zoomoutStroke);
		float originLength = zoomoutStroke.timePeriod.width;
		
		if (strokeIndex >= 0 && strokeIndex < singleStrokeList.size()) {
			//the zoomoutStroke
			if (strokeIndex == 0)
				zoomoutStroke.setNewXPosition(zoomoutStroke.startX, zoomoutStroke.endX + originLength * 0.5f);
			else if (strokeIndex == singleStrokeList.size() - 1)
				zoomoutStroke.setNewXPosition(zoomoutStroke.startX  - originLength * 0.5f, zoomoutStroke.endX);
			else
				zoomoutStroke.setNewXPosition(zoomoutStroke.startX  - originLength * 0.5f, zoomoutStroke.endX + originLength * 0.5f);
			
			//others
			float beforeLength = zoomoutStroke.startX - totalStartX;
			float afterLength = totalEndX - zoomoutStroke.startX - zoomoutStroke.timePeriod.width;
			int totalTime = database.gameMap.get(gameIndex).getMaxTimeIndex();
			
			//the beforeStrokes			
			for (int i = 0; i < strokeIndex; i++) {
				setNewXPosition(singleStrokeList.get(i), 0, zoomoutStroke.startTimeIndex, totalStartX, beforeLength);
			}
			//the afterStrokes
			for (int i = strokeIndex + 1; i < singleStrokeList.size(); i++) {
				setNewXPosition(singleStrokeList.get(i), zoomoutStroke.endTimeIndex, totalTime, totalEndX - afterLength, afterLength);
			}
			setSingleStrokePreCircleXY();
			setSingleStrokeNextCircleXY();
		}
	}
	
	public void setNewXPosition (SingleStroke stroke, int startTimeIndex, int endTimeIndex, float nStartX, float nLength) {
		float newStartX, newEndX;
		newStartX = PApplet.map(stroke.startTimeIndex, startTimeIndex, endTimeIndex, nStartX, nStartX + nLength);
		newEndX = PApplet.map(stroke.endTimeIndex, startTimeIndex, endTimeIndex, nStartX, nStartX + nLength);

		
		stroke.setNewXPosition(newStartX, newEndX);
	}
	
	public void setOriginalStrokesPosition () {
		float newStartX, newEndX;//seperate x
		for (int i = 0; i < singleStrokeList.size(); i++) {
			newStartX = SingleGameCanvas.translateTimeIndexToXPos(singleStrokeList.get(i).startTimeIndex, maxQuarter, totalStartX, totalEndX);
			newEndX = SingleGameCanvas.translateTimeIndexToXPos(singleStrokeList.get(i).endTimeIndex, maxQuarter, totalStartX, totalEndX);
			singleStrokeList.get(i).setNewXPosition(newStartX, newEndX);
		}
		setSingleStrokePreCircleXY();
		setSingleStrokeNextCircleXY();
	}
	//one Stroke
	public class SingleStroke {
		static final float circleRadius = 10f;
		boolean showEventCircles = false;
		boolean showCircles = true;
		float startX;
		float endX;
		float yUp, yDown;
		float yScore;//determines yUp and yDown
		float yPos;
		int upJerseySum;
		int downJerseySum;
		
		float lastStartXForAnimation, lastEndXForAnimation;
		Rectangle2D.Float timePeriod = new Rectangle2D.Float();
		
		int startTimeIndex, endTimeIndex;
		int pointUp, pointDown;
		
		Ellipse2D.Float upNotAtCourtCircle = new Ellipse2D.Float();
		Ellipse2D.Float downNotAtCourtCircle = new Ellipse2D.Float();
		
		ArrayList<PlayerStackRect> upPlayers = new ArrayList<PlayerStackRect>();
		ArrayList<PlayerStackRect> downPlayers = new ArrayList<PlayerStackRect>();
		HashMap<SinglePlayer, PlayerStackRect> playerMap = new HashMap<SinglePlayer, PlayerStackRect>();
		ScoreDiffsStroke scoreDiffsStroke;
		
		public SingleStroke (int startTimeIndex, int endTimeIndex, int pointUp, int pointDown, float yScore) {
			this.startTimeIndex = startTimeIndex;
			this.endTimeIndex = endTimeIndex;
			this.pointUp = pointUp;
			this.pointDown = pointDown;
			//this.yScore = yScore;
			this.yScore = zeroDiffY;
			this.yPos = yScore;
			upJerseySum = 0;
			downJerseySum = 0;
		}
		public void setup () {
			this.startX = SingleGameCanvas.translateTimeIndexToXPos(startTimeIndex, maxQuarter, totalStartX, totalEndX);
			this.endX = SingleGameCanvas.translateTimeIndexToXPos(endTimeIndex, maxQuarter, totalStartX, totalEndX);
			
			///////�ֲ�
			this.scoreDiffsStroke = new ScoreDiffsStroke(quarterPointEntry, startX, endX, yPos, startTimeIndex, endTimeIndex);
			scoreDiffsStrokeList.add(this.scoreDiffsStroke);
			///////////////////////
			this.timePeriod.x = SingleGameCanvas.translateTimeIndexToXPos(startTimeIndex, maxQuarter, totalStartX, totalEndX);
			this.lastStartXForAnimation = this.startX;
			this.lastEndXForAnimation = this.endX;
			
			this.timePeriod.width = this.endX - this.startX;
			this.yUp = yScore - PApplet.map(pointUp, 0, maxGetPoint, 0, lineupHeight);
			this.yDown = yScore + PApplet.map(pointDown, 0, maxGetPoint, 0, lineupHeight);

			setPlayers();
			sortPlayersByType("points");
			
			for (int i = 0; i < upPlayers.size(); i++) {
				upPlayers.get(i).setAttributeGlyph();
			}
			for (int i = 0; i < downPlayers.size(); i++) {
				downPlayers.get(i).setAttributeGlyph();
			}
			
		}
		
		public void setPlayers () {
			PlayerStackRect tempRect;
			//up
			ArrayList<SinglePlayer> leftPlayers = database.gameMap.get(gameIndex).leftPlayers;
			float lastY = yScore;
			for (int i = 0; i < leftPlayers.size(); i++) {
				if (leftPlayers.get(i).atCourtWithinTime(startTimeIndex, endTimeIndex)) {
					tempRect = new PlayerStackRect(leftPlayers.get(i), true);
					tempRect.setYPosition(lastY - tempRect.rect.height);
					upPlayers.add(tempRect);
					playerMap.put(leftPlayers.get(i), tempRect);
					lastY = tempRect.rect.y - 2 * (SingleStroke.circleRadius);
				}
			}			
			
			upNotAtCourtCircle.x = startX;
			upNotAtCourtCircle.y = lastY - (circleRadius - 1) - upPlayers.get(upPlayers.size() - 1).rect.height;
			upNotAtCourtCircle.width = 2 * circleRadius;
			upNotAtCourtCircle.height = 2 * circleRadius;
			this.timePeriod.y = upNotAtCourtCircle.y - circleRadius;
			
			//down
			lastY = yScore;
			ArrayList<SinglePlayer> rightPlayers = database.gameMap.get(gameIndex).rightPlayers;
			for (int i = 0; i < rightPlayers.size(); i++) {
				if (rightPlayers.get(i).atCourtWithinTime(startTimeIndex, endTimeIndex)) {
					tempRect = new PlayerStackRect(rightPlayers.get(i), false);
					tempRect.setYPosition(lastY);
					downPlayers.add(tempRect);
					playerMap.put(rightPlayers.get(i), tempRect);
					lastY = lastY + tempRect.rect.height + 2 * (SingleStroke.circleRadius);
				}
			}
		
			downNotAtCourtCircle.x = startX;
			downNotAtCourtCircle.y = lastY + downPlayers.get(downPlayers.size() - 1).rect.height + (circleRadius - 1);
			downNotAtCourtCircle.width = 2 * circleRadius;
			downNotAtCourtCircle.height = 2 * circleRadius;
			this.timePeriod.height = downNotAtCourtCircle.y - upNotAtCourtCircle.y + 2 * circleRadius;
			
		}
		
		public void setNewXPosition (float newStartX, float newEndX) {
			
			if (newEndX > totalEndX) {
				newEndX = totalEndX;
			} else if (newEndX < totalStartX) {
				newEndX = totalStartX;
			}
			if (newStartX < totalStartX) {
				newStartX = totalStartX;
			} else if (newStartX > totalEndX) {
				newStartX = totalEndX;
			}
									
			for (int i = 0; i < upPlayers.size(); i++) {
				upPlayers.get(i).setNewXPosition(newStartX, newEndX);
			}
			for (int i = 0; i < downPlayers.size(); i++) {
				downPlayers.get(i).setNewXPosition(newStartX, newEndX);
			}
			//�ֲ�
			scoreDiffsStroke.setNewXPosition(startX, endX, newStartX, newEndX);

			this.lastStartXForAnimation = this.startX;
			this.lastEndXForAnimation = this.endX;
			this.endX = newEndX;
			this.startX = newStartX;
			this.timePeriod.x = newStartX;
			this.timePeriod.width = newEndX - newStartX;
			upNotAtCourtCircle.x = newStartX;
			downNotAtCourtCircle.x = newStartX;
			
		}
		
		public void sortPlayersByType (String type) {
			if (type.equals("points")) {
				Collections.sort(upPlayers, new PlayerStackRectByPoints());
				Collections.sort(downPlayers, new PlayerStackRectByPoints());
			}
			rePositionPlayers();
		}
		public void rePositionPlayers () {
			PlayerStackRect tempRect;
			float lastY = yScore -110;
	
			for (int i = 0; i < upPlayers.size(); i++) {
				tempRect = upPlayers.get(i);
				tempRect.setYPosition(lastY - tempRect.rect.height);
				lastY = tempRect.rect.y - 2 * (SingleStroke.circleRadius);
			}
			upNotAtCourtCircle.x = startX;
			upNotAtCourtCircle.y = lastY - (circleRadius - 1) - upPlayers.get(upPlayers.size() - 1).rect.height;
			upNotAtCourtCircle.width = 2 * circleRadius;
			upNotAtCourtCircle.height = 2 * circleRadius;
			lastY = yScore +110;
			for (int i = 0; i < downPlayers.size(); i++) {
				tempRect = downPlayers.get(i);
				tempRect.setYPosition(lastY);
				lastY = lastY + tempRect.rect.height + 2 * (SingleStroke.circleRadius);
			}
			downNotAtCourtCircle.x = startX;
			downNotAtCourtCircle.y = lastY + downPlayers.get(downPlayers.size() - 1).rect.height + (circleRadius - 1);
			downNotAtCourtCircle.width = 2 * circleRadius;
			downNotAtCourtCircle.height = 2 * circleRadius;
		}
		
		
		//inside SingleStroke: main draw
		public void draw (SingleGameCanvas canvas , int qaq) {
			canvas.pushStyle();	
			
			//upPlayers: circle, shapes, lines, curves			
			for (int i = 0; i < upPlayers.size(); i++) {
				if(qaq==0){
					upPlayers.get(i).drawCircle(canvas, this.lastStartXForAnimation);
					if(showEventCircles){
						upPlayers.get(i).drawAttribute(canvas);
					}				
					upPlayers.get(i).drawLine(canvas);
					upPlayers.get(i).drawCurve(canvas);
				} else{
					
				}
			}
			//downPlayers: circle, shapes, lines, curves
			for (int i = 0; i < downPlayers.size(); i++) {
				if(qaq==1){
					downPlayers.get(i).drawCircle(canvas, this.lastStartXForAnimation);
					if(showEventCircles){
						downPlayers.get(i).drawAttribute(canvas);
					}
					downPlayers.get(i).drawLine(canvas);
					downPlayers.get(i).drawCurve(canvas);
				} else{
					
				}
			}
			//�ֲ�
			for (int i=0; i<scoreDiffsStrokeList.size(); i++){
				scoreDiffsStrokeList.get(i).draw(canvas);
			}
			
		    //NotAtCourtCircle
	    	canvas.strokeWeight(1f);
	    	
		    if (VerticalLineupStrokes.hoverPlayerStackRect == null) {
		    	canvas.fill(255);
		    	canvas.stroke(200);
				canvas.ellipse(this.lastStartXForAnimation, upNotAtCourtCircle.y, upNotAtCourtCircle.width, upNotAtCourtCircle.height);
				canvas.ellipse(this.lastStartXForAnimation, downNotAtCourtCircle.y, downNotAtCourtCircle.width, downNotAtCourtCircle.height);		    	
		    } else if (!playerMap.containsKey(VerticalLineupStrokes.hoverPlayerStackRect.pgs)) {
		    	if (VerticalLineupStrokes.hoverPlayerStackRect.isUp){
		    		//up	    		
		    		canvas.fill(255);
		    		canvas.strokeWeight(3f);
		    		canvas.stroke(colorForPositions[hoverPlayerStackRect.posIndex * 3], colorForPositions[hoverPlayerStackRect.posIndex * 3 + 1], colorForPositions[hoverPlayerStackRect.posIndex * 3 + 2]);
					canvas.ellipse(this.lastStartXForAnimation, upNotAtCourtCircle.y, upNotAtCourtCircle.width, upNotAtCourtCircle.height);
					//down
					canvas.fill(255,255,255,50);
		    		canvas.stroke(200,200,200,50);
					canvas.ellipse(this.lastStartXForAnimation, downNotAtCourtCircle.y, downNotAtCourtCircle.width, downNotAtCourtCircle.height);
		    	} else{
		    		//up
		    		canvas.fill(255,255,255,50);
		    		canvas.stroke(200,200,200,50);	    		
					canvas.ellipse(this.lastStartXForAnimation, upNotAtCourtCircle.y, upNotAtCourtCircle.width, upNotAtCourtCircle.height);
					//down
					canvas.fill(255);	
					canvas.strokeWeight(3f);
		    		canvas.stroke(colorForPositions[hoverPlayerStackRect.posIndex * 3], colorForPositions[hoverPlayerStackRect.posIndex * 3 + 1], colorForPositions[hoverPlayerStackRect.posIndex * 3 + 2]);
					canvas.ellipse(this.lastStartXForAnimation, downNotAtCourtCircle.y, downNotAtCourtCircle.width, downNotAtCourtCircle.height);
		    	}
		    } else{
		    		canvas.fill(255,255,255,50);
		    		canvas.stroke(200,200,200,50);
					canvas.ellipse(this.lastStartXForAnimation, upNotAtCourtCircle.y, upNotAtCourtCircle.width, upNotAtCourtCircle.height);
					canvas.ellipse(this.lastStartXForAnimation, downNotAtCourtCircle.y, downNotAtCourtCircle.width, downNotAtCourtCircle.height);    			    	
		    }	
		    
			//drawAnimation
			if (this.lastStartXForAnimation != this.startX) {	
				this.lastStartXForAnimation -= ((this.lastStartXForAnimation - this.startX) * animationRatio);
			}
			
			canvas.popStyle();
			
		}
		
		public void mouseMoved (SingleGameCanvas canvas) {
			boolean isHoverOnPlayerCircle = false;
			for (int i = 0; i < upPlayers.size(); i++) {
				if (upPlayers.get(i).isMouseInCircle(canvas)) {
					isHoverOnPlayerCircle = true;
					VerticalLineupStrokes.hoverPlayerStackRect = upPlayers.get(i);
					//upDatePointForLineList();
					return;
				}
			}
			
			for (int i = 0; i < downPlayers.size(); i++) {
				if (downPlayers.get(i).isMouseInCircle(canvas)) {
					isHoverOnPlayerCircle = true;
					VerticalLineupStrokes.hoverPlayerStackRect = downPlayers.get(i);
					//upDatePointForLineList();
					return;
				}
			}
			
			if (!isHoverOnPlayerCircle) {
				VerticalLineupStrokes.hoverPlayerStackRect = null;
				//pointsForLine.clear();
			}
			
		}
		
		public void mouseClicked (SingleGameCanvas canvas) {
				if (this.timePeriod.contains(canvas.mouseX, canvas.mouseY)) {
					if (canvas.mouseButton == PApplet.LEFT) {
						//System.out.println("Click Left");												
						setAllSingleStrokeNewPosition(this);
						showEventCircles = true;
						zoomOut = true; 
					} else if (canvas.mouseButton == PApplet.RIGHT) {
						//System.out.println("Click Right");
						setOriginalStrokesPosition();
						showEventCircles = false;
						zoomOut = false;
					}
					
				}

		}
		

		
		
		public class PlayerStackRect {
			SinglePlayer pgs;
			Rectangle2D.Float rect = new Rectangle2D.Float();
			Rectangle2D.Float preRect = rect;
			float contribution;
			float insideCircleRadius;
			float circleX, circleY;
			float preCircleX = totalStartX, preCircleY = zeroDiffY;
			float nextCircleX = totalStartX, nextCircleY = zeroDiffY;
			boolean atCourt;
			boolean preAtCourt, nextAtCourt;
			int jersey;
			boolean isUp = true;
			
			boolean rectHighlight = false;
			boolean circleHighlight = false;
			boolean notDisplay = false;
					
			int posIndex = 3; // 0 for G, 1 for F, 2 for C, 3 for notFound
			
			//attribute and its timeIndexList					
			ArrayList<Integer> twoPtsList = new ArrayList<Integer>();
			ArrayList<Integer> threePtsList = new ArrayList<Integer>();
			ArrayList<Integer> freeList = new ArrayList<Integer>();
			ArrayList<Integer> ofRbdList = new ArrayList<Integer>();
			ArrayList<Integer> defRbdList = new ArrayList<Integer>();		
			ArrayList<Integer> turnoverList = new ArrayList<Integer>();
			ArrayList<Integer> missList = new ArrayList<Integer>();
			HashMap<String, Integer> astList = new HashMap<String, Integer>();
			HashMap<String, Integer> blkList = new HashMap<String, Integer>();
			HashMap<String, Integer> stlList = new HashMap<String, Integer>();
			HashMap<String, Integer> foulList = new HashMap<String, Integer>();
			
			//attribute drawing
			ArrayList<Double> twoPtsXList = new ArrayList<Double>();
			ArrayList<Double> threePtsXList = new ArrayList<Double>();
			ArrayList<Double> freeXList = new ArrayList<Double>();
			ArrayList<Double> defRbdXList = new ArrayList<Double>();
			ArrayList<Double> ofRbdXList = new ArrayList<Double>();
			ArrayList<Double> turnoverXList = new ArrayList<Double>();
			ArrayList<Double> missXList = new ArrayList<Double>();
			ArrayList<Rectangle2D.Float> astXYList = new ArrayList<Rectangle2D.Float>();
			ArrayList<Rectangle2D.Float> blkXYList = new ArrayList<Rectangle2D.Float>();
			ArrayList<Rectangle2D.Float> stlXYList = new ArrayList<Rectangle2D.Float>();
			ArrayList<Rectangle2D.Float> foulXYList = new ArrayList<Rectangle2D.Float>();
			
			//attr sum
			float twoPts;
			float threePts;
			float free;
			float ofRbd;
			float defRbd;
			float turnover;
			float miss;
			
			float ast;
			float blk;
			float stl;
			float foul;
			
			float points;
			
			//attr radius
			float twoPtsRadius = 25;
			float threePtsRadius = 25;
			float freeRadius = 25;
			float defRbdRadius = 30;
			float ofRbdRadius = 30;
			float turnoverRadius = 20;
			float missRadius = 25;
			
			float astRadius = 20;
			float blkRadius = 20;
			float stlRadius = 20;
			float foulRadius = 20;
		
			public PlayerStackRect (SinglePlayer pgs, boolean isUp) {
				this.pgs = pgs;
				
				this.isUp = isUp;
				this.rect.x = startX - littleBarWidth / 2;
				this.circleX = startX;
				this.rect.width = littleBarWidth;
				this.jersey = pgs.getJersey();
				this.atCourt = true;
				if(isUp){
					upJerseySum +=  this.jersey;
				} else{
					downJerseySum +=  this.jersey;
				}
				
				// set color index according to the player's position
				String pos = this.pgs.pos;
				if (pos != null) {
					if (this.pgs.pos.startsWith("C")) {
						posIndex = 2;
					} else if (this.pgs.pos.startsWith("F")) {
						posIndex = 1;
					} else if (this.pgs.pos.startsWith("G")) {
						posIndex = 0;
					} 
				}
								
				//set attribute
				setAllEventList();
				twoPts = twoPtsList.size();
				threePts = threePtsList.size();
				free = freeList.size();
				ofRbd = ofRbdList.size();
				defRbd = defRbdList.size();
				turnover = turnoverList.size();
				miss = missList.size();
				ast = astList.size();
				blk = blkList.size();
				stl = stlList.size();
				foul = foulList.size();	
				
				points = 2*twoPts+3*threePts+free;
				//
				setHeight(points);
				
			}
			
			public void setNewXPosition (float newStartX, float newEndX) {
				rect.x = newStartX - littleBarWidth / 2;
				circleX = newStartX;
				for(int i=0; i<twoPtsXList.size(); i++){				
					Double x = new Double(PApplet.map((float)twoPtsXList.get(i).doubleValue(), startX, endX, newStartX, newEndX));
					twoPtsXList.set(i, x);
				}
				for(int i=0; i<threePtsXList.size(); i++){				
					Double x = new Double(PApplet.map((float)threePtsXList.get(i).doubleValue(), startX, endX, newStartX, newEndX));
					threePtsXList.set(i, x);
				}
				for(int i=0; i<freeXList.size(); i++){				
					Double x = new Double(PApplet.map((float)freeXList.get(i).doubleValue(), startX, endX, newStartX, newEndX));
					freeXList.set(i, x);
				}
				for(int i=0; i<defRbdXList.size(); i++){				
					Double x = new Double(PApplet.map((float)defRbdXList.get(i).doubleValue(), startX, endX, newStartX, newEndX));
					defRbdXList.set(i, x);
				}
				for(int i=0; i<ofRbdXList.size(); i++){				
					Double x = new Double(PApplet.map((float)ofRbdXList.get(i).doubleValue(), startX, endX, newStartX, newEndX));
					ofRbdXList.set(i, x);
				}
				for(int i=0; i<turnoverXList.size(); i++){				
					Double x = new Double(PApplet.map((float)turnoverXList.get(i).doubleValue(), startX, endX, newStartX, newEndX));
					turnoverXList.set(i, x);
				}
				for(int i=0; i<missXList.size(); i++){				
					Double x = new Double(PApplet.map((float)missXList.get(i).doubleValue(), startX, endX, newStartX, newEndX));
					missXList.set(i, x);
				}
				
				//2
				for(int i=0; i<astXYList.size(); i++){
					astXYList.get(i).x = PApplet.map(astXYList.get(i).x, startX, endX, newStartX, newEndX);					
				}
				for(int i=0; i<blkXYList.size(); i++){
					blkXYList.get(i).x = PApplet.map(blkXYList.get(i).x, startX, endX, newStartX, newEndX);					
				}
				for(int i=0; i<stlXYList.size(); i++){
					stlXYList.get(i).x = PApplet.map(stlXYList.get(i).x, startX, endX, newStartX, newEndX);					
				}
				for(int i=0; i<foulXYList.size(); i++){
					foulXYList.get(i).x = PApplet.map(foulXYList.get(i).x, startX, endX, newStartX, newEndX);					
				}
							
			}
			
			public void setPreCircleXY (float x, float y) {
				this.preCircleX = x;
				this.preCircleY = y;
			}
			
			public void setNextCircleXY (float x, float y) {
				this.nextCircleX = x;
				this.nextCircleY = y;
			}
			
			public void setPreRect(Rectangle2D.Float rect) {
				this.preRect = rect;
			}
			
			public void setYPosition (float y) {
				this.rect.y = y;
				this.circleY = isUp ? (rect.y - (circleRadius)) : (rect.y + rect.height + (circleRadius)); 
			}
			
//			public int getEventList (String type) {
//				return pgs.getStatByTypeWithinTime(type, startTimeIndex, endTimeIndex);
//			}
			public void setAllEventList(){
				twoPtsList=pgs.getTimeListByTypeWithinTime("twoPts", startTimeIndex, endTimeIndex);
				threePtsList=pgs.getTimeListByTypeWithinTime("threePts", startTimeIndex, endTimeIndex);
				freeList=pgs.getTimeListByTypeWithinTime("free", startTimeIndex, endTimeIndex);
				ofRbdList=pgs.getTimeListByTypeWithinTime("ofRbd", startTimeIndex, endTimeIndex);			
				defRbdList=pgs.getTimeListByTypeWithinTime("defRbd", startTimeIndex, endTimeIndex);	
				turnoverList=pgs.getTimeListByTypeWithinTime("turnover", startTimeIndex, endTimeIndex);
				missList=pgs.getTimeListByTypeWithinTime("miss", startTimeIndex, endTimeIndex);
				
				astList=pgs.getStatByTypeWithinTime("ast", startTimeIndex, endTimeIndex);															
				blkList=pgs.getStatByTypeWithinTime("blk", startTimeIndex, endTimeIndex);		
				stlList=pgs.getStatByTypeWithinTime("stl", startTimeIndex, endTimeIndex);				
				foulList=pgs.getStatByTypeWithinTime("foul", startTimeIndex, endTimeIndex);			
			}	
			public void setAttributeGlyph(){
				for(int i=0; i<twoPtsList.size();i++){
					int timeIndex = twoPtsList.get(i);
					float x = SingleGameCanvas.translateTimeIndexToXPos(timeIndex, maxQuarter, totalStartX, totalEndX);
					Double xx = new Double(x);
					twoPtsXList.add(xx);
				}
				for(int i=0; i<threePtsList.size();i++){
					int timeIndex = threePtsList.get(i);
					float x = SingleGameCanvas.translateTimeIndexToXPos(timeIndex, maxQuarter, totalStartX, totalEndX);
					Double xx = new Double(x);
					threePtsXList.add(xx);
				}
				for(int i=0; i<freeList.size();i++){
					int timeIndex = freeList.get(i);
					float x = SingleGameCanvas.translateTimeIndexToXPos(timeIndex, maxQuarter, totalStartX, totalEndX);
					Double xx = new Double(x);
					freeXList.add(xx);
				}
				for(int i=0; i<defRbdList.size();i++){
					int timeIndex = defRbdList.get(i);
					float x = SingleGameCanvas.translateTimeIndexToXPos(timeIndex, maxQuarter, totalStartX, totalEndX);
					Double xx = new Double(x);
					defRbdXList.add(xx);
				}
				for(int i=0; i<ofRbdList.size();i++){
					int timeIndex = ofRbdList.get(i);
					float x = SingleGameCanvas.translateTimeIndexToXPos(timeIndex, maxQuarter, totalStartX, totalEndX);
					Double xx = new Double(x);
					ofRbdXList.add(xx);
				}
				for(int i=0; i<turnoverList.size();i++){
					int timeIndex = turnoverList.get(i);
					float x = SingleGameCanvas.translateTimeIndexToXPos(timeIndex, maxQuarter, totalStartX, totalEndX);
					Double xx = new Double(x);
					turnoverXList.add(xx);
				}
				for(int i=0; i<missList.size();i++){
					int timeIndex = missList.get(i);
					float x = SingleGameCanvas.translateTimeIndexToXPos(timeIndex, maxQuarter, totalStartX, totalEndX);
					Double xx = new Double(x);
					missXList.add(xx);
				}
				
				//2
				Iterator<Entry<String, Integer>> iter = astList.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String, Integer> entry = iter.next(); 		
					Rectangle2D.Float rect = new Rectangle2D.Float();
					int timeIndex = entry.getValue();
					float x = SingleGameCanvas.translateTimeIndexToXPos(timeIndex, maxQuarter, totalStartX, totalEndX);
					rect.x = x;
					if(isUp){											
						for(int i=0; i<upPlayers.size(); i++){							
							if(upPlayers.get(i).pgs.name.equals(entry.getKey())){
								rect.y = upPlayers.get(i).circleY;								
							}
						}
					} else{
						for(int i=0; i<downPlayers.size(); i++){							
							if(downPlayers.get(i).pgs.name.equals(entry.getKey())){
								rect.y = downPlayers.get(i).circleY;								
							}
						}
					}
					astXYList.add(rect);
				}
				
				iter = blkList.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String, Integer> entry = iter.next();
					Rectangle2D.Float rect = new Rectangle2D.Float();
					int timeIndex = entry.getValue();
					float x = SingleGameCanvas.translateTimeIndexToXPos(timeIndex, maxQuarter, totalStartX, totalEndX);
					rect.x = x;
					if(isUp){											
						for(int i=0; i<downPlayers.size(); i++){							
							if(downPlayers.get(i).pgs.name.equals(entry.getKey())){
								rect.y = downPlayers.get(i).circleY;								
							}																				
						}
					} else{
						for(int i=0; i<upPlayers.size(); i++){							
							if(upPlayers.get(i).pgs.name.equals(entry.getKey())){
								rect.y = upPlayers.get(i).circleY;								
							}																				
						}
					}
					blkXYList.add(rect);
				}
				
				iter = stlList.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String, Integer> entry = iter.next();
					Rectangle2D.Float rect = new Rectangle2D.Float();
					int timeIndex = entry.getValue();
					float x = SingleGameCanvas.translateTimeIndexToXPos(timeIndex, maxQuarter, totalStartX, totalEndX);
					rect.x = x;
					if(isUp){											
						for(int i=0; i<downPlayers.size(); i++){							
							if(downPlayers.get(i).pgs.name.equals(entry.getKey())){
								rect.y = downPlayers.get(i).circleY;								
							}																				
						}
					} else{
						for(int i=0; i<upPlayers.size(); i++){							
							if(upPlayers.get(i).pgs.name.equals(entry.getKey())){
								rect.y = upPlayers.get(i).circleY;								
							}																				
						}
					}
					stlXYList.add(rect);
				}
				
				iter = foulList.entrySet().iterator();
				while(iter.hasNext()){
					Entry<String, Integer> entry = iter.next();
					Rectangle2D.Float rect = new Rectangle2D.Float();
					int timeIndex = entry.getValue();
					float x = SingleGameCanvas.translateTimeIndexToXPos(timeIndex, maxQuarter, totalStartX, totalEndX);
					rect.x = x;
					if(isUp){											
						for(int i=0; i<downPlayers.size(); i++){							
							if(downPlayers.get(i).pgs.name.equals(entry.getKey())){
								rect.y = downPlayers.get(i).circleY;								
							}
						}
					} else{
						for(int i=0; i<upPlayers.size(); i++){							
							if(upPlayers.get(i).pgs.name.equals(entry.getKey())){
								rect.y = upPlayers.get(i).circleY;								
							}
						}
					}
					foulXYList.add(rect);
				}
				
			}
			
			
			//drawAttribute
			public void drawAttribute (SingleGameCanvas canvas) {
				
				canvas.pushStyle();		
				canvas.fill(0);
				//canvas.text((int)free, circleX, circleY-insideCircleRadius);
				
				//2
				canvas.strokeWeight(2);
				for(int i=0; i<astXYList.size();i++){
					float x = astXYList.get(i).x;
					float y = astXYList.get(i).y;
					canvas.stroke(220);
					canvas.line(x, circleY, x, y);	
					canvas.shape(canvas.ast, x, circleY, astRadius, astRadius);					
																	
				}
				
				for(int i=0; i<blkXYList.size();i++){
					float x = blkXYList.get(i).x;
					float y = blkXYList.get(i).y;
					canvas.stroke(220);
					canvas.line(x, circleY, x, y);	
					canvas.shape(canvas.blk, x, circleY, blkRadius, blkRadius);												
				}
				
				for(int i=0; i<stlXYList.size();i++){
					float x = stlXYList.get(i).x;
					float y = stlXYList.get(i).y;
					canvas.stroke(220);
					canvas.line(x, circleY, x, y);	
					canvas.shape(canvas.stl, x, circleY, stlRadius, stlRadius);																
				}
				
				for(int i=0; i<foulXYList.size();i++){
					float x = foulXYList.get(i).x;
					float y = foulXYList.get(i).y;
					canvas.stroke(220);
					canvas.line(x, circleY, x, y);	
					canvas.shape(canvas.foul, x, circleY, foulRadius, foulRadius);											
				}
						
				//1
				for(int i=0; i<twoPtsList.size();i++){
					float x= (float) twoPtsXList.get(i).doubleValue();
					canvas.shape(canvas.twoPts, x, circleY, twoPtsRadius, twoPtsRadius);
				}
				for(int i=0; i<threePtsList.size();i++){
					float x= (float) threePtsXList.get(i).doubleValue();
					canvas.shape(canvas.threePts, x, circleY, threePtsRadius, threePtsRadius);
				}
				for(int i=0; i<freeList.size();i++){
					float x= (float) freeXList.get(i).doubleValue();					
					canvas.shape(canvas.free, x, circleY, freeRadius, freeRadius);
				}
				for(int i=0; i<defRbdList.size();i++){
					float x= (float) defRbdXList.get(i).doubleValue();				
					canvas.shape(canvas.defRbd, x, circleY, defRbdRadius, defRbdRadius);
				}
				for(int i=0; i<ofRbdList.size();i++){
					float x= (float) ofRbdXList.get(i).doubleValue();
					canvas.shape(canvas.ofRbd, x, circleY, ofRbdRadius, ofRbdRadius);
				}
				for(int i=0; i<turnoverList.size();i++){
					float x= (float) turnoverXList.get(i).doubleValue();
					canvas.shape(canvas.turnover, x, circleY, turnoverRadius, turnoverRadius);
				}
				for(int i=0; i<missList.size();i++){
					float x= (float) missXList.get(i).doubleValue();
					canvas.shape(canvas.miss, x, circleY, missRadius, missRadius);
				}
				
				canvas.popStyle();
			}		
			
			
			public void setHeight (float point) {
				

				int maxPoints = isUp ? pointUp : pointDown;
				
				float thisMaxHeight = isUp ? yScore - yUp : yDown - yScore;

				this.rect.height = 15;
				//this.rect.height = point/(originalLength)*1500+3;
				this.insideCircleRadius = circleRadius;
			}
			
			//circle
			public void drawCircle (SingleGameCanvas canvas, float lastStartXForAnimation) {
				canvas.pushStyle();			
				
				canvas.strokeWeight(1);
				//text
				canvas.textSize(10);
				canvas.textAlign(PApplet.CENTER, PApplet.CENTER);								
				//--if it is chosen by clicking
				if (clickedPGS.size() >= 1 && !clickedPGS.contains(this.pgs)) {
					canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2], 50);
					if(!preAtCourt){
						canvas.stroke(180,0,0,50);
					} else if(!nextAtCourt){
						canvas.stroke(0,0,180,50);
					} else{
						canvas.stroke(20,20,20,50);
					}
					
					canvas.ellipse(lastStartXForAnimation, circleY, 2 * insideCircleRadius, 2 * insideCircleRadius);	
					if(showEventCircles){
						canvas.stroke(180,180,180,120);
						canvas.strokeWeight(1);
						canvas.strokeCap(PApplet.PROJECT);
						canvas.line(lastStartXForAnimation+insideCircleRadius+2, circleY, endX-2, circleY);
					}		
					//
					//canvas.fill(0,0,0,50);
					//canvas.text(pgs.getJersey(), lastStartXForAnimation, circleY - 2);
				}
				else if (clickedPGS.size() >= 1 && clickedPGS.contains(this.pgs)) {
					canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2]);
					//canvas.stroke(180,180,180);
					if(!preAtCourt){
						canvas.stroke(180,0,0);
					} else if(!nextAtCourt){
						canvas.stroke(0,0,180);
					} else{
						canvas.stroke(180,180,180);
					}
					canvas.ellipse(lastStartXForAnimation, circleY, 2 * insideCircleRadius, 2 * insideCircleRadius);
					if(showEventCircles){
						canvas.stroke(180,180,180,120);
						canvas.strokeWeight(1);
						canvas.strokeCap(PApplet.PROJECT);
						canvas.line(lastStartXForAnimation+insideCircleRadius+2, circleY, endX-2, circleY);	
					}		
					//
					canvas.fill(0,0,0);
					canvas.text(pgs.getJersey(), lastStartXForAnimation, circleY - 2);
				}
				else if (clickedPGS.size() >= 1 && this.pgs.equals(VerticalLineupStrokes.hoverPlayerStackRect.pgs)) {
					canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2]);
					//canvas.stroke(180,180,180);
					if(!preAtCourt){
						canvas.stroke(180,0,0);
					} else if(!nextAtCourt){
						canvas.stroke(0,0,180);
					} else{
						canvas.stroke(180,180,180);
					}
					canvas.ellipse(lastStartXForAnimation, circleY, 2 * insideCircleRadius, 2 * insideCircleRadius);
					if(showEventCircles){
						canvas.stroke(180,180,180,120);
						canvas.strokeWeight(1);
						canvas.strokeCap(PApplet.PROJECT);
						canvas.line(lastStartXForAnimation+insideCircleRadius+2, circleY, endX-2, circleY);	
					}		
					//
					canvas.fill(0,0,0);
					canvas.text(pgs.getJersey(), lastStartXForAnimation, circleY - 2);
				}
				//--if none is chosen by clicking
				//----if none is hovered
				else if (clickedPGS.size() == 0 && VerticalLineupStrokes.hoverPlayerStackRect == null) {
					if(!zoomOut){
						canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2]);
					} else if(showEventCircles){
						canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2]);
					} else{
						canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2], 50);
					}
					
					//canvas.stroke(180,180,180);
					if(!preAtCourt){
						canvas.stroke(180,0,0);
					} else if(!nextAtCourt){
						canvas.stroke(0,0,180);
					} else{
						canvas.stroke(180,180,180);
					}
					canvas.ellipse(lastStartXForAnimation, circleY, 2 * insideCircleRadius, 2 * insideCircleRadius);
					if(showEventCircles){
						canvas.stroke(180,180,180,120);
						canvas.strokeWeight(1);
						canvas.strokeCap(PApplet.PROJECT);
						canvas.line(lastStartXForAnimation+insideCircleRadius+2, circleY, endX-2, circleY);	
					}
					//
					canvas.fill(0,0,0);
					if(!zoomOut){
						canvas.text(pgs.getJersey(), lastStartXForAnimation, circleY - 2);
					} else if(showEventCircles){
						canvas.text(pgs.getJersey(), lastStartXForAnimation, circleY - 2);
					} else{
					}
					
				}				
				//----if one is hovered				
				//------if it is not hovered, half transparency
				else if (clickedPGS.size() == 0 && VerticalLineupStrokes.hoverPlayerStackRect != null && !this.pgs.equals(VerticalLineupStrokes.hoverPlayerStackRect.pgs)) {
					canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2], 50);
					//canvas.stroke(20,20,20,50);
					if(!preAtCourt){
						canvas.stroke(180,0,0,50);
					} else if(!nextAtCourt){
						canvas.stroke(0,0,180,50);
					} else{
						canvas.stroke(20,20,20,50);
					}
					canvas.ellipse(lastStartXForAnimation, circleY, 2 * insideCircleRadius, 2 * insideCircleRadius);
					if(showEventCircles){
						canvas.stroke(180,180,180,120);
						canvas.strokeWeight(1);
						canvas.strokeCap(PApplet.PROJECT);
						canvas.line(lastStartXForAnimation+insideCircleRadius+2, circleY, endX-2, circleY);	
					}
					//
					//canvas.fill(0,0,0,50);
					//canvas.text(pgs.getJersey(), lastStartXForAnimation, circleY - 2);
				}
				//------if it is hovered
				else if (clickedPGS.size() == 0 && VerticalLineupStrokes.hoverPlayerStackRect != null && this.pgs.equals(VerticalLineupStrokes.hoverPlayerStackRect.pgs)) {
					canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2]);
					//canvas.stroke(180,180,180);
					if(!preAtCourt){
						canvas.stroke(180,0,0);
					} else if(!nextAtCourt){
						canvas.stroke(0,0,180);
					} else{
						canvas.stroke(180,180,180);
					}
					canvas.ellipse(lastStartXForAnimation, circleY, 2 * insideCircleRadius, 2 * insideCircleRadius);
					if(showEventCircles){
						canvas.stroke(180,180,180,120);
						canvas.strokeWeight(1);
						canvas.strokeCap(PApplet.PROJECT);
						canvas.line(lastStartXForAnimation+insideCircleRadius+2, circleY, endX-2, circleY);	
					}
					//
					canvas.fill(0,0,0);
					canvas.text(pgs.getJersey(), lastStartXForAnimation, circleY - 2);
				}
				
				canvas.popStyle();
			}
			
					
			//line
			public void drawLine (SingleGameCanvas canvas) {
				canvas.pushStyle();
				canvas.fill(220);
				canvas.noStroke();
				
				//--if none is hovered
				//canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2]);	
				
				//--if one is hovered
				//----if it is hovered
				if ((VerticalLineupStrokes.hoverPlayerStackRect != null && this.pgs.equals(VerticalLineupStrokes.hoverPlayerStackRect.pgs)) 
						|| (clickedPGS.size() >= 1 && clickedPGS.contains(this.pgs))){
					canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2]);	
				}		
				
				//----if it is not hovered, half transparency
				if (VerticalLineupStrokes.hoverPlayerStackRect != null) {
					canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2], 50);	
				}
				
				//--if it is chosen by clicking, others: half transparency
				if (clickedPGS.size() >= 1 && !clickedPGS.contains(this.pgs)) {
					canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2], 50);	
				}

				canvas.rect(lastStartXForAnimation-littleBarWidth/2, rect.y, rect.width, rect.height);

				canvas.popStyle();				
			}
			
			public void drawCurve (SingleGameCanvas canvas) {
				canvas.pushStyle();
				float fraction = (float) (2.0/5);
				
				if ((VerticalLineupStrokes.hoverPlayerStackRect != null && this.pgs.equals(VerticalLineupStrokes.hoverPlayerStackRect.pgs)) 
						|| (clickedPGS.size() >= 1 && clickedPGS.contains(this.pgs))) {
						if (rect.height == 0) {
							canvas.stroke(220);
						} else {
							canvas.noStroke();
						}
						
						//set color of bezier curve
						//canvas.fill(220, 220,220,points*45>100?100:(points+1)*70);
						canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2], points*45>200?200:(float)(points+0.5)*70);
							canvas.beginShape();
														
							if (!isUp) {
							
							canvas.vertex(circleX, circleY - insideCircleRadius);
							
							canvas.bezierVertex(circleX + (nextCircleX - circleX) * fraction, circleY-insideCircleRadius, nextCircleX - (nextCircleX - circleX) * fraction, nextCircleY - insideCircleRadius, nextCircleX, nextCircleY - insideCircleRadius);
							canvas.vertex(nextCircleX, nextCircleY+insideCircleRadius);
							
							canvas.bezierVertex(nextCircleX - (nextCircleX - circleX) * fraction, nextCircleY+insideCircleRadius, circleX + (nextCircleX - circleX) * fraction, circleY + insideCircleRadius, circleX, circleY+insideCircleRadius);
							
							canvas.endShape();							
						} else {
							
							canvas.vertex(circleX, circleY - insideCircleRadius);
							
							canvas.bezierVertex(circleX + (nextCircleX - circleX) * fraction, circleY-insideCircleRadius, nextCircleX - (nextCircleX - circleX) * fraction, nextCircleY - insideCircleRadius, nextCircleX, nextCircleY - insideCircleRadius);
							canvas.vertex(nextCircleX, nextCircleY+insideCircleRadius);
							
							canvas.bezierVertex(nextCircleX - (nextCircleX - circleX) * fraction, nextCircleY+insideCircleRadius, circleX + (nextCircleX - circleX) * fraction, circleY + insideCircleRadius, circleX, circleY+insideCircleRadius);
							
							canvas.endShape();
						}
				}
											
				canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2]);
				canvas.noStroke();
				
				
				if (VerticalLineupStrokes.hoverPlayerStackRect != null
						&& !this.pgs.equals(VerticalLineupStrokes.hoverPlayerStackRect.pgs)) {
					canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2], 50);	
				}
				if (clickedPGS.size() >= 1 && !clickedPGS.contains(this.pgs)) {
					canvas.fill(colorForPositions[posIndex * 3], colorForPositions[posIndex * 3 + 1], colorForPositions[posIndex * 3 + 2], 50);	
				}
												
				canvas.popStyle();
			}
			
			public boolean isMouseInCircle (SingleGameCanvas canvas) {
				if (PApplet.dist(circleX, circleY, canvas.mouseX, canvas.mouseY) <= SingleStroke.circleRadius) {
					return true;
				}
				return false;
			}
			

		}
	

		
		public class DiffLine {
			//ArrayList<>
		}
	}
	
	
}
