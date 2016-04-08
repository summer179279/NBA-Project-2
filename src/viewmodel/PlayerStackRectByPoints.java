package viewmodel;

import java.util.Comparator;
import viewmodel.VerticalLineupStrokes.SingleStroke.PlayerStackRect;

public class PlayerStackRectByPoints implements Comparator<PlayerStackRect>{

	@Override
	public int compare(PlayerStackRect o1, PlayerStackRect o2) {
		// TODO Auto-generated method stub
		float pl1 = o1.points;
		float pl2 = o2.points;
		
		if (pl1 < pl2) {
			return 1;
		} else if (pl1== pl2) {
			return 0;
		} else {
			return -1;
		}
	}

}