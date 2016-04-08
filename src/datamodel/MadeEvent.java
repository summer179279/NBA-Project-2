package datamodel;

public class MadeEvent extends Event {
	int point; // 1 for freethrow, 2 for 2-pt, 3 for 3-pt
	int distance;
	boolean assisted = false;
	String assistPlayer;
	
	public MadeEvent(int gameIndex, String type, String actionPlayer, int quarter,
					 int timeIndex, int actionTeamIndex, int curLeftScore,
					 int curRightScore) {
		super( gameIndex, type, actionPlayer, quarter, timeIndex, actionTeamIndex, curLeftScore,
				curRightScore);
		// TODO Auto-generated constructor stub
		this.distance = distance;
		this.point = point;
	}
	
	public int getDistanceLevel () {
		if (this.point == 1) {
			return -1;
		}
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
	public void setDistance (int distance) {
		this.distance = distance;
	}
	
	public int getPoint () {
		return point;
	}
	
	public void setAssistedPlayer(String player) {
		assistPlayer = player;
		assisted = true;
	}
	
	public boolean isAssisted () {
		return assisted;
	}
	
	public String assistedBy () {
		return assistPlayer;
	}
	
}
