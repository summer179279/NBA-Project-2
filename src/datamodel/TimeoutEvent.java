package datamodel;

public class TimeoutEvent extends Event {
	String timeoutType;
	
	public TimeoutEvent(int gameIndex, String type, String actionPlayer, int quarter,
			int timeIndex, int actionTeamIndex, int curLeftScore,
			int curRightScore) {
		super(gameIndex, type, actionPlayer, quarter, timeIndex, actionTeamIndex, curLeftScore,
				curRightScore);
		// TODO Auto-generated constructor stub
	}
	public void setTimeoutType (String timeoutType) {
		this.timeoutType = timeoutType;
	}

}
