package viewmodel;

import java.util.Comparator;

import viewmodel.ScoreDiffLine.PointEntry;

public class PointEntrySortByX implements Comparator<PointEntry>{

	@Override
	public int compare(PointEntry o1, PointEntry o2) {
		// TODO Auto-generated method stub
		float pl1 = o1.x, pl2 = o2.x;

	
		if (pl1 > pl2) {
			return 1;
		} else if (pl1== pl2) {
			return 0;
		} else {
			return -1;
		}
	}

}
