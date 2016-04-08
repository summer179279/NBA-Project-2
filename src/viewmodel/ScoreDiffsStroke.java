package viewmodel;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import processing.core.PApplet;
import viewmodel.ScoreDiffLine.PointEntry;


public class ScoreDiffsStroke {
	
	ArrayList<Rectangle2D.Float> scoreDiffs;
	
	public ScoreDiffsStroke(HashMap<Integer, ArrayList<PointEntry>> quarterPointEntry, float startX, float endX, float yPos, float startTimeIndex, float endTimeIndex){
		Rectangle2D.Float start = new Rectangle2D.Float();						
		start.x = startX;
		start.y = yPos;
		scoreDiffs = new ArrayList<Rectangle2D.Float>();
		scoreDiffs.add(start);

		float lastTimeIndex = 0;
		Iterator<Entry<Integer, ArrayList<PointEntry>>> iter = quarterPointEntry.entrySet().iterator();
		while(iter.hasNext()){
			Entry<Integer, ArrayList<PointEntry>> entry = iter.next();
			ArrayList<PointEntry> pointEntries= new ArrayList<PointEntry>();
			pointEntries = entry.getValue();
			for(int i=0; i<pointEntries.size(); i++){
				if(pointEntries.get(i).timeIndex>=startTimeIndex && pointEntries.get(i).timeIndex<=endTimeIndex ){
					if(pointEntries.get(i).timeIndex>=lastTimeIndex){
						Rectangle2D.Float diff = new Rectangle2D.Float();
						diff.x = PApplet.map((float)pointEntries.get(i).timeIndex, startTimeIndex, endTimeIndex, startX, endX);	
						diff.y = pointEntries.get(i).y;
						scoreDiffs.add(diff);
						lastTimeIndex = pointEntries.get(i).timeIndex;
					}
				}
			}
		}
		Rectangle2D.Float end = new Rectangle2D.Float();
		end.x = endX;
		end.y = scoreDiffs.get(scoreDiffs.size()-1).y;
		scoreDiffs.add(end);
	}
	
	public void draw(SingleGameCanvas canvas){
		//draw scoreDiffs
		canvas.strokeWeight(3);
		for(int i=1; i<scoreDiffs.size(); i++){
			canvas.stroke(180,0,0);
			canvas.strokeWeight(1);
			canvas.line(scoreDiffs.get(i-1).x, scoreDiffs.get(i-1).y, scoreDiffs.get(i).x, scoreDiffs.get(i).y);
		}
		canvas.strokeWeight(1);
	}
	
	public void setNewXPosition(float startX, float endX, float newStartX, float newEndX){
		for(int i=0; i<scoreDiffs.size(); i++){
			float x = PApplet.map(scoreDiffs.get(i).x, startX, endX, newStartX, newEndX);
			scoreDiffs.get(i).x = x;
		}
	}	
}
