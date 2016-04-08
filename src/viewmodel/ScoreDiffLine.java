package viewmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import processing.core.PApplet;
import datamodel.Database;
import datamodel.Event;
import datamodel.MadeEvent;

public class ScoreDiffLine {
	int gameIndex;
	Database database;
	int maxQuarter;
	float centerY;
	float scoreDiffHeight=100; 
	HashMap<Integer, ArrayList<PointEntry>> quarterPointEntry;

	public ScoreDiffLine (int gameIndex, Database database, float centerY) {
		this.gameIndex = gameIndex;
		this.database = database;
		this.maxQuarter = database.gameMap.get(gameIndex).getMaxQuarter();
		this.centerY = centerY;
		this.quarterPointEntry = new HashMap<Integer, ArrayList<PointEntry>>();
	}
	
	public void setup () {
		initPointEntryList();
	}
	
	public void initPointEntryList () {
		for (int i = 1; i <= maxQuarter; i++) {
			quarterPointEntry.put(i, new ArrayList<PointEntry>());
		}
				
		int formerScoreDiff;
		ArrayList<Event> events = database.gameMap.get(gameIndex).getEventList();
			
		formerScoreDiff = 0;
		int timeIndex;
		int quarter;
		for (int i = 0; i < events.size(); i++) {
			if (events.get(i) instanceof MadeEvent) {
				timeIndex = events.get(i).timeIndex;
				quarter = events.get(i).quarter;
				quarterPointEntry.get(quarter).add(new PointEntry(timeIndex, formerScoreDiff));
				quarterPointEntry.get(quarter).add(new PointEntry(timeIndex, events.get(i).getScoreDiff()));
				formerScoreDiff = events.get(i).getScoreDiff();
			}
		}
		
		int[][] startAndEndScore = new int[maxQuarter][2];
		startAndEndScore[0][0] = 0;
		for (int i = 0; i < events.size() - 1; i++) {
			if (events.get(i + 1).quarter > events.get(i).quarter) {
				startAndEndScore[events.get(i).quarter - 1][1] = events.get(i).getScoreDiff();
				startAndEndScore[events.get(i + 1).quarter - 1][0] = events.get(i).getScoreDiff();
			}
		}
		startAndEndScore[maxQuarter - 1][1] = database.gameMap.get(gameIndex).getDirectScoreDiff();
		
		for (int i = 1; i <= maxQuarter; i++) {
			if (i <= 4) {
				timeIndex = (i - 1) * 720;
				quarterPointEntry.get(i).add(new PointEntry(timeIndex, startAndEndScore[i - 1][0]));
				quarterPointEntry.get(i).add(new PointEntry(timeIndex + 719, startAndEndScore[i - 1][1]));
			} else {
				timeIndex = (i - 5) * 300 + 2880;
				quarterPointEntry.get(i).add(new PointEntry(timeIndex, startAndEndScore[i - 1][0]));
				quarterPointEntry.get(i).add(new PointEntry(timeIndex + 299, startAndEndScore[i - 1][1]));
			}
			Collections.sort(quarterPointEntry.get(i), new PointEntrySortByX());
		}				
	}
	
	public class PointEntry {
		int timeIndex;
		int scoreDiff;
		float x;
		float y;
		
		PointEntry (int timeIndex, int scoreDiff) {
			this.timeIndex = timeIndex;
			this.scoreDiff = scoreDiff;
//			float h = PApplet.map(scoreDiff >= 0 ? scoreDiff : -scoreDiff, 0, database.gameMap.get(gameIndex).getMaxScoreDiff(),
//			0, background.height / 2);
			float h = PApplet.map(scoreDiff >= 0 ? scoreDiff : -scoreDiff, 0, database.gameMap.get(gameIndex).getMaxScoreDiff(),
			0, scoreDiffHeight);
			this.y = scoreDiff >= 0 ? centerY - h : centerY + h;
		}
	}
	

	

	
}
