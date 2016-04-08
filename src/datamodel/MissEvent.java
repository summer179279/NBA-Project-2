package datamodel;

public class MissEvent extends Event {
	int point; // 1 for freethrow, 2 for 2-pt, 3 for 3-pt
	int distance;
	boolean blocked = false;
	String blockPlayer;

	public MissEvent(int gameIndex, String type, String actionPlayer, int quarter,
					 int timeIndex, int actionTeamIndex, int curLeftScore,
					 int curRightScore) {
		super( gameIndex, type, actionPlayer, quarter, timeIndex, actionTeamIndex, curLeftScore,
				curRightScore);
		// TODO Auto-generated constructor stub

	}
	
	public int getDistanceLevel () {
		if (this.distance >= 0 && this.distance <= 3) {
			return 0;
		} else if (this.distance > 3 && this.distance <= 10) {
			return 1;
		} else if (this.distance > 10 && this.distance <= 16) {
			return 2;
		} else if (this.point == 2 && this.distance > 16) {
			return 3;
		} else if (this.point == 3) {
			return 4;
		}
		return 0;
	}
	
	public void setPoint (int point) {
		this.point = point;
	}
	public int getPoint () {
		return point;
	}
	
	public void setDistance (int distance) {
		this.distance = distance;
	}
	
	public void setBlockPlayer (String player) {
		blockPlayer = player;
		blocked = true;
	}
	
	public boolean isBlocked () {
		return blocked;
	}
	
	public String blockedBy () {
		return blockPlayer;
	}

}
