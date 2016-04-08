package viewmodel;

import java.awt.geom.Point2D;
import java.util.Comparator;

public class PointSortByX implements Comparator<Point2D.Float>{

	@Override
	public int compare(Point2D.Float o1, Point2D.Float o2) {
		// TODO Auto-generated method stub
		float pl1 = o1.x, pl2 = o2.x;

	
		if (pl1 < pl2) {
			return 1;
		} else if (pl1== pl2) {
			return 0;
		} else {
			return -1;
		}
	}

}
