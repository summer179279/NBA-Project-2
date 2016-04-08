package datamodel;

//ÿһ���¼�
public class Event {
	public int gameIndex;

	public int quarter;
	public int timeIndex;
	public int curLeftScore, curRightScore;

	public int actionTeamIndex;
	public String actionPlayer;
	public String type;

	public Event (int gameIndex, String type, String actionPlayer, int quarter, int timeIndex, int actionTeamIndex, int curLeftScore, int curRightScore) {
		this.gameIndex = gameIndex;
		this.quarter = quarter;
		this.actionTeamIndex = actionTeamIndex;
		this.curLeftScore = curLeftScore;
		this.curRightScore = curRightScore;
		this.type = type;
		this.actionPlayer = actionPlayer;
		this.timeIndex = timeIndex;
	}

	public static int timeTranslate (String timeDisplay, int quarter) {
		String[] array = timeDisplay.split(":");
		int min = Integer.parseInt(array[0]);
		int sec = Integer.parseInt(array[1]);
		int milSec = Integer.parseInt(array[2]);
		return 720 - (min * 60 + sec);
	}
	public int getTimeIndex () {
		return timeIndex;
	}
	public String getType () {
		return type;
	}
	public int getScoreDiff () {
		return curLeftScore - curRightScore;
	}
	public int getActionTeamIndex () {
		return actionTeamIndex;
	}
	public int getQuarter () {
		return quarter;
	}
	
	public MadeEvent getMadeInstance () {
		return (MadeEvent)this;
	}
	public MissEvent getMissInstance () {
		return (MissEvent)this;
	}
	public ReboundEvent getReboundInstance () {
		return (ReboundEvent)this;
	}
	public FoulEvent getFoulInstance () {
		return (FoulEvent)this;
	}
	public EnterEvent getEnterInstance () {
		return (EnterEvent)this;
	}
	public TurnoverEvent getTurnoverInstance () {
		return (TurnoverEvent)this;
	}
	public TimeoutEvent getTimeoutInstance () {
		return (TimeoutEvent)this;
	}
}
